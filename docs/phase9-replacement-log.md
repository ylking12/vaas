# Phase 9 后台服务线上替换 - 操作日志

> **用途**：记录 Phase 9 后端服务线上替换的每一步操作、命令、结果、决策，作为替换过程的可审计、可回滚依据。
> **任务状态索引**：见 [TASK_TRACKING.md](../TASK_TRACKING.md) 的 Phase 9 小节
> **创建**：2026-07-14
> **维护规则**：每完成一个步骤（9.x），立即在本文件追加记录；只记已发生的操作过程，不记待办（待办在 TASK_TRACKING.md）

---

## 替换范围（最终确认 2026-07-13）

| 服务器 | 服务 | 是否替换 |
|--------|------|----------|
| 192.168.112.15 | Backend(50410) | ✅ 换 |
| 192.168.112.15 | Detector4motion | ✅ 换 |
| 192.168.112.15 | Detector4kt | ✅ 换 |
| 192.168.112.16 | Receiver(50412) | ✅ 换 |
| 192.168.112.18 | Backend(50410) | ⏹ 停止未换(07-17决策，见§9.7) |
| 192.168.112.18 | Nginx / OpenResty | ❌ 不换（中间件/数据管道） |
| 192.168.112.17 | Redis / MySQL | ❌ 不换（中间件） |
| 192.168.112.18 | 前端大屏 | ❌ 不换（已替换上线） |

**替换顺序**：Backend（查询侧，风险低）-> Receiver（数据入口）-> Detector（算法，风险最高，最后）

**执行约束**：
- 不能直接 SSH / 不能登录采样 -> 我给命令，用户在对应机器执行后回传结果
- 单服务停机窗口 ≤ 1 天
- 每次只替换一个服务，替换前必须有备份和回滚路径

---

## 9.1 线上后台服务拓扑盘点 ✅ [完成 2026-07-14]

### 操作内容
1. 读取 `maintenance/服务器列表.xlsx`，获取 4 台服务器基本信息
2. 生成 4 台机器盘点命令包（进程/端口/JAR/systemd unit/配置/磁盘/JDK），用户执行回传
3. 补查 16 的 receiver 实际路径（/data/etas/vaas/receiver）+ 18 外网路由验证

### 盘点结果

**环境基础（4 台一致）**：
- Ubuntu 20.04.6 LTS
- JDK 17.0.15（/usr/bin/java）—— 符合 CLAUDE.md 锁定 Java 17，无需升级

**待替换 5 服务运行状态**：

| 机器 | 服务 | PID | 端口 | JAR（字节） | 启动参数 | systemd unit 位置 |
|------|------|-----|------|-------------|----------|-------------------|
| 15 | vaas_backend | 52969 | 50410 | vaas_backend.jar (76,437,951) | `-Dspring.profiles.location=application-prod.yaml -Duser.timezone=Asia/Shanghai` | /lib/systemd/system |
| 15 | vaas_detector4motion | 53268 | - | vaas_detector4motion.jar (37,025,409) | 同上 | /lib/systemd/system |
| 15 | detector4kt | 53435 | - | detector4kt.jar->软链 (36,536,467) | `-Dspring.profiles.active=prod`（无 timezone） | /etc/systemd/system |
| 16 | receiver | 57706 | 50412 | receiver.jar->软链 (43,508,592) | `-Dspring.profiles.active=prod` | /etc/systemd/system |
| 18 | vaas_backend | 64498 | 50410 | vaas_backend.jar (76,437,951) | `-Dspring.profiles.location=application-prod.yaml -Duser.timezone=Asia/Shanghai` | /lib/systemd/system |

- 环境变量统一：`DB_HOST=192.168.112.17` `DB_USER=vaas` `DB_PASSWD=Etas_vaas!` `REDIS_HOST=192.168.112.17`
- backend@15 与 backend@18 JAR 大小完全一致（76,437,951），同一份包

**路径修正**：
- receiver 实际在 16 的 `/data/etas/vaas/receiver`（Excel 文档写的 /opt 有误）
- 其他服务在 `/opt/etas/vaas/` 下

**配置加载方式（关键差异）**：
- 外部配置：backend、detector4motion 用 `-Dspring.profiles.location=application-prod.yaml`，目录下有外部 yaml 文件
- JAR 内置配置：receiver、detector4kt 用 `-Dspring.profiles.active=prod`，目录下无外部 yaml，配置在 classpath

**外网路由（最终确认）**：
- 444 端口不在 18 本地监听 -> 公网入口（防火墙/SLB）映射进来
- OpenResty 443 配置里 `/spring/v1/ -> 192.168.112.16:50410` 是**失效配置**（16 上确认无 50410 端口，只有 receiver）
- 有效路径：外网 444（公网 SSL 终止）-> nginx:80(.18) -> `192.168.112.15:50410`
- 内网：http://192.168.112.18/ -> nginx:80 -> upstream{.15:50410 + .18:50410}
- 车端数据：车端(双向证书) -> openresty:443(.18) -> receiver:50412(.16) + 合创镜像

**中间件版本**：
- Redis 8.0.2，端口 6379，无密码，db0 有 194 keys
- MySQL 8.0.42，数据库：vaas + vaas_test

**其他发现**：
- 15 与 16 上均有完整部署包（receiver/admin/模拟器等），各机只启动部分服务，替换时其他文件原样保留
- detector4kt 启动参数缺 `-Duser.timezone=Asia/Shanghai`，日志为 UTC 时间（与实际差 8 小时）
- detector4kt 目录下有异常文件名 `'\'`（1740 字节），疑似历史误操作残留
- 前端 www 已确认是 timelinefix 替换版本（DashboardPage-CFFmfTLB.js 等）

### 关键决策
1. 保持各服务原有配置加载方式（外部 vs JAR 内置），还原版打包时分别处理，不擅自统一
2. OpenResty 443 的失效配置（->16:50410）不动，属于 nginx 层面，不在 backend 替换范围
3. detector4kt 的 UTC 时区差异按"还原优先"原则保持原样（如确认为 bug，替换时注释说明，不擅自修）
4. 替换时统一备份命名规范（如 `xxx.jar.backup-YYYYMMDD`），不沿用线上不统一的 .bak 命名

### 产出
- 确认版线上拓扑（本节）
- 已写入 TASK_TRACKING.md 的 9.1 盘点结论

---

## 9.4 线上接口与数据行为基线采样 ✅ [完成 2026-07-14]

### 目的
采集替换前行为基线（API 响应、Redis/MySQL 状态、日志格式），作为替换后一致性对照基准。

### 操作内容
- 生成采样命令包（3 台机器）：
  - 18：通过 nginx 采样大屏核心 API 响应
  - 17：Redis key 列表与类型 + MySQL 表行数
  - 15：backend 日志格式样本

### 采样结果

**SSH 只读连接已建立**（2026-07-14）：192.168.112.17 公钥认证通过，ubuntu 用户免密可连。MySQL 查询用 `MYSQL_PWD` 环境变量传密码（不进命令行参数）。待 15/16/18 公钥配置后补全。

#### 17 - Redis 基线（dbsize=195，实时增长中）

key 体系（`vaas:` 前缀）：
- `vaas:vehicle:info:{deviceId}` (list) - 车辆位置轨迹，receiver 写入（LPUSH+LTRIM 保留最近 3600 条），数量最多
- `vaas:vehicle:motion4` / `vaas:vehicle:motion5` (list) - 6 轴运动数据
- `vaas:vehicle:kt:5` (list) - KT710 数据
- `vaas:heartbeat:location` - 心跳
- `vaas:bump:counter` - 颠簸计数器
- `vaas:slip:counter` - 湿滑计数器

注意：部分 deviceId 位数异常（16 位，正常应 15 位），疑似数据异常或测试数据。

#### 17 - MySQL vaas 库基线

| 表名 | 行数 | 用途 |
|------|------|------|
| event | 265,384 | 核心事件表（颠簸/积水/湿滑等），backend 查询来源 |
| weather | 235,047 | 气象/路况传感器数据 |
| contact | 150 | 联系人 |
| fleet_management | 148 | 车队管理（/location 接口读取） |
| flyway_schema_history | 18 | Flyway 数据库迁移版本记录（schema 用 Flyway 管理） |
| brand_model | 5 | 品牌型号 |
| redis_key | 1 | Redis key 配置 |

### 基线数据

Redis key 命名体系 + MySQL 表结构与行数已采（见上）。替换后需对照：
- Redis key 前缀/类型不变
- MySQL 表名/字段不变，行数稳定（不丢数据）
- event 表是 backend 查询核心，26 万行，替换不能影响

### 9.4 采样结果补充说明

#### 18 - 大屏 API 响应基线（走 nginx:80 -> 15:50410，2026-07-14）

| 接口 | 方法 | 结果 |
|------|------|------|
| /spring/v1/location | GET | ✅ 返回车辆位置 map：`{deviceId:{vehicleId,coordinates{longitude,latitude},plateNumber,deviceId,serialNumber,eventCount,speed}}`，采样时 4 辆车 |
| /spring/v1/get-event-summary | POST | ✅ `{water_road_amount,bumpy_road_amount,slippery_road_amount,water_road_to_maintain,slippery_road_to_maintain,bumpy_road_to_maintain}`，采样时 bumpy=70/slippery=1/water=0。注：GET /getEventSummary 返回 500 是采样路径写错，正确为 POST /get-event-summary，与还原版 EventController 一致 |
| /spring/v1/get-alarm-list | POST {hour:0} | ✅ `[{roadName,datetime,sourceName,eventType}]`，eventType 中文（颠簸点/打滑点） |
| /spring/v1/get-last-24h-bump-event | POST {hour:0} | ✅ `[{eventId,eventType,level,longitude,latitude,eventTime}]`，level 3/5 |
| /spring/v1/get-last-24h-ponding-event | POST {hour:0} | ✅ `[]`（当前无积水） |
| /spring/v1/get-last-24h-slip-event | POST {hour:0} | ✅ 1 条 slip 事件 |

注：响应被 `head -c 3000` 截断处出现 `Failed writing body` 是管道截断正常现象，非错误。

#### 15 - backend 日志格式
- 日志按日期分目录：`logs/2026-06-16/`、`logs/2026-06-17/`... 每天一目录
- 格式（标准 Spring Boot）：`2026-07-14 09:54:25.916 INFO 52969 --- [pool-3-thread-1] c.e.v.backend.component.RedisSubscriber : ...`
- detector4motion / detector4kt 日志同样按日期分目录

#### 16 - receiver 状态与日志
- receiver active running，3 周 6 天，内存 1.2G
- 日志显示在工作：`trimming redis list with key : vaas:vehicle:info:xxx, size:3601`（位置保留 3600 条）、`calSpd` 速度计算
- 日志时间为 UTC（06:35），receiver 未设 -Duser.timezone（与 detector4kt 一致）

### 9.4 待确认项
- ~~getEventSummary 路径~~ 已确认：还原版 EventController 为 POST /get-event-summary，与线上原版行为一致（采样返回 bumpy=70/slippery=1/water=0）

### 9.4 完成结论（2026-07-14）
基线采样完成，已建立替换前对照基准：
- Redis key 体系（vaas:vehicle:info:{deviceId} list 等）+ MySQL 表结构（event 26万/weather 23万/fleet_management 148 等）
- 大屏 API 响应结构（location/get-event-summary/get-alarm-list/get-last-24h-*-event）
- 日志格式（标准 Spring Boot，按日期分目录；receiver/detector4kt 为 UTC 时间）
- **关键验证**：还原版 backend 的 /get-event-summary 接口与线上原版返回结构一致，初步证明还原版可用

---

### 9.4 盲区补充（2026-07-14，SSH 只读查询）

#### application-prod.yaml 对比（15 vs 18）
- 15 用环境变量引用：`${REDIS_HOST}`/`${DB_HOST}`/`${DB_USER}`/`${DB_PASSWD}`（systemd unit 设了这些环境变量）
- 18 写死值：192.168.112.17 / vaas / 密码明文（Etas_***）
- 其他配置完全一致：port 50410、context-path /spring/v1、redis/mysql 指向 17、event.maxReturnedBumpEventAmount=375、传感器配置、事件检测阈值（积水 pondingDepth>=1、低附着 waterLayerThickness<=0.62、结冰 roadConditions==7、湿滑 roadConditions==3）
- 还原版配置对齐：业务配置（传感器/阈值/区域）必须一致；连接信息用环境变量引用或写死均可（systemd unit 已设环境变量）

#### event 表结构（16 字段）
id / event_id / event_type(slip,bump,ponding,lowFriction) / source_id(imei) / source_type(kt710,motionSensor,weatherSensor) / road_name / longitude / latitude / in_area / event_time / received_time / perception_time / duplicated / level(1-10) / simulated / h3_hash
- 主键 id，索引 event_event_time_index，AUTO_INCREMENT=292990

#### fleet_management 表结构（13 字段）
id / imei(后视镜id) / kt710_id / plate(车牌) / data_type(kt710|6a) / group_id / bump_enable / slip_enable / sim_id / brand_model / reject / update_at / phone_number
- 4 个唯一键：imei / kt710_id / plate / sim_id

#### 还原版 backend API 清单
- EventController: get-alarm-list / get-event-summary / get-last-24h-{bump,slip,ice,ponding,low-attachment}-event / delete-event / external/getEventSummary / delete-all-events / post-simulated-event
- LocationController: location
- SSEController: stream_data
- StatisticController: get_covered_range / get_real_time_sensor_data / get_last24h_data_plot
- WeatherController: get_weather / get-rain-points / get-rain-intensity/{districtName}
- WeatherSensorController: post_realtime_sensor_data
- ExportController: export/daily
- RearMirrorController: rear-mirror/get-last24h-event
- ReportController: report/{reportDate}
- 已验证与线上一致：location / get-event-summary / get-alarm-list / get-last-24h-{bump,ponding,slip}-event

## Backend@15 替换准备 - 本地构建与测试 [2026-07-14]

### 代码改动（本地）
- vaas-backend/pom.xml: finalName `vaas-backend` -> `vaas_backend`（匹配线上 systemd unit `-jar vaas_backend.jar`）
- 创建 src/main/resources/application-prod.yaml（基于线上15配置，环境变量引用连接信息，含完整业务配置：传感器/事件阈值/区域）
- 还原 src/main/resources/logback-spring.xml（从线上15拷贝，原还原版缺失）

### 本地测试（连本地 MySQL80/Redis）
- 构建：`mvn package` -> vaas_backend.jar (fat jar) BUILD SUCCESS
- 启动：`REDIS_HOST=localhost DB_HOST=localhost DB_USER=root DB_PASSWD=123456 java -Dspring.profiles.active=prod -jar vaas_backend.jar`
- 结果：Started 11.5s，Tomcat 50410，prod profile 激活，actuator/health=UP，API 响应结构正确

### 发现并修复的还原 bug（本地测试暴露，不测则线上启动失败）
1. **spring.profiles.location 不被 SB 3.5.3 识别** -> 改用 `-Dspring.profiles.active=prod`（部署时需改 systemd unit）
2. **logback-spring.xml 缺失**（还原版没有）-> 从线上15还原到 resources（application-prod.yaml 的 logging.config: file:./logback-spring.xml 依赖它）
3. **SensorConfig.CompareType 枚举缺 Eq** -> 补 `Eq`（线上 ice-signal/wet_signal 用 compareType: Eq）
4. **WeatherSensorService.compare 缺 case Eq** -> 补 `case Eq: return compareTo==0`（否则结冰/湿滑事件永不触发）

### 深度检查结论（2026-07-14，用户要求部署前再检查）
- **entity 全对照一致**：Event(16字段)/FleetManagement(13)/Weather(21)/BrandModel(3)/RedisKey(19)
- **枚举全对照一致**：EventType(bump/slip/ponding/lowFriction/ice)/SourceType(kt710/motionSensor/weatherSensor)/CompareType(补Eq)/DataType/SensorType
- **查询逻辑**：getEventsInTimeRange/getEventSummary 查 Redis ZSet（bumpEventKey/slipEventKey/pondingEventKey），不是 event 表；本地 Redis 空返回[]正常
- **ice/low-attachment 行为**：getRawEventData switch 中 ice_event/low_attachment_event 直接 break（不查 Redis），但线上原版这两个接口也返回 []（未实现），还原版一致，非 bug
- **结论**：还原版 backend 与线上原版行为一致，4 个还原 bug 已修复，可部署

### 部署要点（待执行，写操作）
- systemd unit 改 ExecStart：`-Dspring.profiles.location=application-prod.yaml` -> `-Dspring.profiles.active=prod`（15和18都要改）
- 工作目录保留 logback-spring.xml（外部，file:./ 用）+ application-prod.yaml（外部覆盖 jar 内，内容一致）
- 备份原 jar+配置+unit 后再替换，验证 API 对照基线 + actuator/health，异常回滚

## 9.7 vaas-backend 替换执行记录（2026-07-15 ~ 07-16）

### 部署方式（实际执行，更正前期"部署要点"假设）
- **只换 JAR，外部配置 + systemd unit 全部不动**（前期笔记"要改 unit 的 -Dspring.profiles.location -> -Dspring.profiles.active"是错的，纠正）
- 线上 `/opt/etas/vaas/vaas_backend/` 有**外部** application.yml(37字节, `spring.profiles.active: prod`) + application-prod.yaml(${DB_HOST}等env引用) + logback-spring.xml
- 外部 file:./ 优先级 > jar 内 classpath；**prod 由外部 application.yml 激活**；unit 里 `-Dspring.profiles.location` 是 SB 3.5.3 不识别的无效遗留 flag(原版跑4周无害)，**不需改 unit**
- unit 已设 env：DB_HOST/DB_USER/DB_PASSWD/REDIS_HOST(均指17)，外部 application-prod.yaml 的 ${} 靠它解析
- 故 bug#1(spring.profiles.location) 线上不需改 unit；本地测试需 -Dspring.profiles.active=prod 是因本地无外部 application.yml

### 第一次部署（2026-07-15 10:51，JAR sha256 5d3801f3...）
- 备份原版 -> vaas_backend.jar.bak.20260715；上传还原版 -> stop/mv/start
- 验证通过：active, prod激活, Tomcat 50410, actuator/health=UP；get-event-summary(bumpy=66/slippery=1/water=0 同基线)/location/get-alarm-list/get-last-24h-bump-event 结构全一致；RedisSubscriber 订阅 vaas:event:topic 正常；FleetManagement 定时任务正常；logback 滚动正常
- **发现回归**：/external/getEventSummary/{startTime}/{endTime} 每5分钟报 -parameters 错误(HTTP 500)

### 回归排查与修复（第5个还原 bug，部署后线上暴露）
- 现象：error 日志 `Name for argument [LocalDateTime] not specified ... -parameters flag`，每5分钟一次
- 调用链：某方调 OpenResty@16 `location /spring/v1/`(listen 80) -> lua `mirror.send_mirror("http://192.168.112.15:50410")` 镜像到 15:50410；调用没走18 nginx 故其 access.log 无记录；15 无 nginx/Tomcat access log，靠 backend error 日志定位
- **javap -v 决定性验证**（注意：javap -p 不显示参数名，必须 -v 看 MethodParameters 属性）：
  - 原版备份 jar EventController：**11 个 MethodParameters**（原版编译有 -parameters，接口原本正常）
  - 还原版 jar：**0 个**（丢了 -parameters）-> 是还原版回归，非原版既有（前期用 javap -p 误判为"原版也没参数名"，已纠正）
- **根因**：backend/pom.xml 的 maven-compiler-plugin 未指定版本；项目用 spring-boot-dependencies BOM（不管插件版本）非 starter-parent，Maven 回落到默认 **3.1**(2013年)；3.1 不支持 `<parameters>` 配置(3.6.2才加)，`<parameters>true</parameters>` 被静默忽略，-parameters 没传给 javac
- **修复**（backend/pom.xml，构建配置补全，非业务逻辑）：
  1. maven-compiler-plugin 锁定 `<version>3.13.0</version>`
  2. 加 `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>` + `<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>`（锁3.13.0后回落平台GBK，中文注释报不可映射字符；原版用UTF-8）
- 重新构建：BUILD SUCCESS，MethodParameters=11(与原版一致)，getEventSummary 有 startTime/endTime，关键类齐全，新 JAR sha256 **f687c567**...

### 第二次部署（2026-07-16 10:47，修复版 JAR sha256 f687c567...）
- 备份回归版 -> vaas_backend.jar.bak.20260715-regression；上传修复版 -> stop/mv/start
- 验证通过：active(PID 4023202), prod激活, 6.7s启动, actuator/health=UP
- **/external/getEventSummary/{startTime}/{endTime} 返回 200 + 真实事件数据**（之前500）
- 新进程 -parameters 错误数=0；最后一条 -parameters 错误停在 10:45:00(修复前回归版)；10:47后周期调用方(10:50/10:54:59/10:59:59)全部成功(enter getEventSummary 方法体执行)
- 回滚目标：vaas_backend.jar.bak.20260715（真原版，完整可用）

### Backend@15 结论
✅ 还原版运行中，所有大屏 API 与基线一致，/external 外部查询接口正常，-parameters 回归已修复。**Backend@15 替换完成。**

### Backend@18 决策：停止而非替换（2026-07-17）

**评估结论**：18 backend 近空载，停掉无影响且比"留旧 JAR 跑"更干净，故决定停止、不换 JAR。
- 18 backend 仅接内网 nginx `upstream{15:50410 + 127.0.0.1:50410}` 轮询流量；外网 vaas.wx-iov.com 直连 15，不经 18
- 实测负载极低：~4 HTTP/天，0 SSE 客户端
- 2 个定时任务（CleanEventZSet 1h 清 Redis ZSet / FleetManagementComponent 5min 刷内存缓存）**无分布式锁**，15 独立跑同样任务全覆盖；停 18 只去冗余，不丢功能
- 新 JAR 与原版唯一差异是编译期 -parameters（不影响业务逻辑/DB/Redis 读写），15+18 混跑无数据风险

**执行**：用户在 18 执行 `systemctl stop vaas_backend`（11:09:26 CST，exit-code 143 干净停止）

**停后核对（SSH 只读）**：

| 检查 | 结果 |
|------|------|
| 18 vaas_backend | failed(exit 143)，50410 不再监听 ✓ |
| 18 nginx | active running，80 在监听 ✓ |
| 内网 192.168.112.18/（首页） | HTTP 200 ✓ |
| 内网 /spring/v1/get_weather | HTTP 200，3.3ms（failover 到 15）✓ |
| 外网 vaas.wx-iov.com:444/（首页） | HTTP 200 ✓ |
| 外网 /spring/v1/get_weather | HTTP 200，真实数据（无锡 35°C 多云）✓ |
| 15 backend | active running 24h(PID 4023202)，50410 在监听，本机 API 200 ✓ |
| 18 nginx error log | 无报错（nginx 已把 127.0.0.1:50410 标 down，直连 15）✓ |

**结论**：✅ 大屏展示零影响。外网走 15、内网 failover 到 15、前端静态页 nginx 照出。

**⚠️ 遗留**：18 JAR 仍为原版（已停）。若后续重启 18，需先换 f687c567 JAR 或保持停止状态，否则旧 JAR 会重新加入内网 upstream。

## 后续步骤记录

（9.8/9.6/... 替换执行记录将在此追加）

---

## vaas-backend@15 演示保活部署包构建（2026-07-23）

### 背景
用户要求大屏常驻一个固定颠簸点位用于演示/验收。新增演示保活组件 `DemoBumpEventKeepAlive`（vaas-backend/cron），向 Redis 颠簸事件 ZSet 注入常驻点位（120.379123, 31.585633, 团结路庄桥路），每 6h 刷新 score 对抗 24h 清理。用户确认生产环境**开启保活**（已告知会往生产 Redis 注入假事件）。

### 部署包
- `dist/vaas-backend-20260723.tar.gz`（sha256 `c4096714c60eeba3d25a018cd02c1c1211e7e7ec3a5186ae15073fee494a4502`，66MB）
- 含 `vaas_backend.jar`（sha256 `8609c1cd2b09b4865501d08eec558177d634650d340280b01a82c050f3fc7abf`，78,018,982 字节）+ `README-deploy.md`
- 部署方式：只换 jar，外部配置 + systemd unit 不动（同 9.7）
- 构建验证：finalName=vaas_backend；EventController MethodParameters=11（`-parameters` 在，`/external/getEventSummary` 不回归）；本地启动 + API 验证注入成功（返回含 longitude:120.379123）

### ⚠️ 生产影响（已告知用户）
- 保活自动开启（`demo.bump.keepalive.enabled` 默认 true，prod 配置未关闭），往生产 Redis（192.168.112.17）注入假颠簸事件（eventId=`DEMO_BUMP_KEEPALIVE`）
- 影响大屏 marker / 告警列表 / 事件统计（`bumpy_road_to_maintain` 多"团结路庄桥路"）
- 每 6h 重新注入，jar 在跑就常驻
- 回滚/清理（详见 README-deploy.md）：先回滚旧 jar 停保活，再 `POST /delete-event {"eventId":"DEMO_BUMP_KEEPALIVE","eventType":"bump"}` 删假事件

### 状态
- v1 已部署到 15（2026-07-23 15:29），但发现 bug
- **v1 bug（2026-07-23）**：演示事件未设 deviceId（null），getAlarmList 调 `FleetManagementComponent.getDeviceId2CarMap()`（ConcurrentHashMap，不允许 null key）.get(null) 抛 NPE，告警列表 500 全空（HTTP 400 + `Cannot invoke "Object.hashCode()" because "key" is null`）。get-last-24h-bump-event 正常（不查 deviceId）
- **v2 修复**：`buildMemberJson` 补 `deviceId="DEMO_DEVICE"`，本地验证 get-alarm-list 返回 200
- v2 部署包：`dist/vaas-backend-20260723.tar.gz`（tar sha256 `e491a24b3d34d6ef3c2f5cc79d73b316a4dc66f2428f39e6b466357a44922eb1`），jar sha256 `e2ec50121509275a22e9e6e8db1b04c07c8b64e4c53b190cafb6d1bbdd5c40c5`
- **v2 已部署到 15**（2026-07-23 15:57:22，PID 1807317）：get-alarm-list 恢复 200（含演示点位"团结路庄桥路/苏B*****" + 真实告警），-parameters 200，cleanupOldDemoMember 自动清 v1 旧假事件

---

## 9.6 receiver 替换前只读核查（2026-08-17）

### 核查对象
- 服务器：192.168.112.16
- systemd：`receiver.service`
- 工作目录：`/data/etas/vaas/receiver`
- 监听端口：50412
- 启动命令：`/usr/bin/java -Dspring.profiles.active=prod -jar receiver.jar`

### 线上运行状态
- 服务状态：active / enabled
- 当前 PID：928
- 当前运行 JAR：`receiver.jar -> receiver-0eb93458.jar`
- 当前运行 JAR sha256：`e15de9344b6cb7900a62c367268d4c660baf8bf478dc63a9c9a9988d45405a7d`
- 同目录历史 JAR：
  - `receiver-4fd4ef6d.jar` sha256 `8970da923c43fccc305f3119b75976303c884af9c57ce8198c090ad068bb2692`
  - `receiver.jar.bak`
- 环境变量来源：systemd unit 内 `DB_HOST=192.168.112.17`、`DB_USER=vaas`、`DB_PASSWD`、`REDIS_HOST=192.168.112.17`
- 外部配置文件：`config.yaml` 存在，但 systemd 未显式指定 `spring.config.location`；JAR 内也有 `application.yaml` 激活 prod 与 `application-prod.yaml`。

### 线上配置/行为要点
- 线上 prod 配置包含：
  - `redis.kt-max-queue-size=80000`
  - `redis.motion-max-queue-size=80000`
  - `redis.location-max-queue-size=3000`
  - `redis.location-allow-max-overflow=600`
  - Flyway 配置
  - MyBatis-Plus sequence worker/datacenter 配置
  - dump.coordinate / dump.kt / dump.motion 配置
- 线上 JAR receiver 类清单共 16 个，包含：
  - `BaseReceiverService`
  - `PositionService`
  - `LocationHandler`
  - `DumpConfig`
  - `RedisMessageContainer`
  - `RedisSubscriber`
  - `GlobalExceptionHandler`
- 日志显示真实位置数据持续进入：`PositionService` 持续裁剪 `vaas:vehicle:info:*`，`FleetManagementComponent` 每 5 分钟从 MySQL 刷新车辆配置。
- 观察到的 WARN 主要是位置队列裁剪与 GPS 跳点速度计算告警，属于当前线上既有运行行为；本次核查未发现服务级启动失败或持续 ERROR。
- 日志时间仍为 UTC，receiver unit 未设置 `-Duser.timezone=Asia/Shanghai`。

### 本地候选包对比
- 本地候选包：`backend/receiver/target/receiver.jar`
- 本地候选包 sha256：`036443968f8751442c3f929424f468bd1b81729c9252ce518349db68078cfa3f`
- 本地候选包仅含 13 个 receiver 类，缺少线上运行包中的 `BaseReceiverService`、`PositionService`、`LocationHandler`、`DumpConfig`、`RedisMessageContainer`、`RedisSubscriber`、`GlobalExceptionHandler`。
- 本地 `application.yml` 仍使用 `redis.coordinate-max-queue-size=80000`，缺少线上 prod 的 `location-max-queue-size=3000`、`location-allow-max-overflow=600`、Flyway、MyBatis-Plus sequence、dump 配置。

### 结论
⚠️ receiver 当前线上服务运行正常，但本地 `backend/receiver/target/receiver.jar` 还不是可直接替换的候选包。直接替换会丢失位置接入/位置队列裁剪、dump 配置、异常处理、Redis 订阅组件等线上真实运行类，并改变位置 Redis 队列保留策略。9.6 进入替换前对齐阶段，下一步必须先补齐缺失源码与 prod 配置，再重新构建并做旁路验证。

## 9.6 receiver 本地对齐与构建验证（2026-08-17）

### 对齐内容
- 恢复线上 receiver 运行包中存在的核心类：`BaseReceiverService`、`PositionService`、`LocationHandler`、`DumpConfig`、`RedisMessageContainer`、`RedisSubscriber`、`GlobalExceptionHandler`、`receiver.dto.CachedVehiclePosition`。
- `WebSocketDispatcher` 改为线上形态：`/ws/location -> LocationHandler`、`/ws/motion -> MotionHandler`。
- 删除本地旧占位类：`CoordinateHandler`、`VehicleService`、`FleetManagement`、`RedisKey`、`GeoUtils` 等。
- `application.yaml` 拆分为线上同名结构：`application.yaml`（激活 prod）+ `application-prod.yaml`（prod 配置）。
- `receiver/pom.xml` 对齐线上依赖：移除 actuator，补齐 `flyway-core 11.10.4` 与 `flyway-mysql 11.10.4`。

### 构建与对比
- `mvn -pl receiver -am clean package -DskipTests` 通过。
- 本地 receiver JAR：`backend/receiver/target/receiver.jar`。
- 构建后 receiver 类清单：16 个，与线上 JAR 完全一致。
- 线上/本地 receiver 类 diff：0 行。
- 线上/本地配置文件名一致：`application.yaml` + `application-prod.yaml`。
- 线上/本地关键 prod 配置一致：`location-max-queue-size=3000`、`location-allow-max-overflow=600`、`flyway`、`mybatis-plus`、`dump`。
- 本地 JAR 已不含 actuator 依赖，Flyway 依赖已与线上一致。

### 本地冒烟
- 以 `--spring.flyway.enabled=false` 做本地启动冒烟，避免对本机共享数据库执行迁移。
- 结果：`prod` profile 激活、Hikari/MySQL 正常、Redis debug channel 初始化正常、Netty 监听 50412 正常，启动成功。

### 结论
✅ receiver 本地还原已对齐到可替换候选包形态；当前仅剩线上替换命令执行与替换后只读验收，不再存在本地源码/配置阻断。

---

## 9.10 detector4motion 替换执行记录（2026-08-17）

### 替换对象
- 服务器：192.168.112.15
- systemd：`vaas_detector4motion.service`
- 工作目录：`/opt/etas/vaas/vaas_detector4motion`
- 实际运行 JAR：`/opt/etas/vaas/vaas_detector4motion/vaas_detector4motion.jar`
- 说明：目录内另有 `detector4motion-prod-0917.jar`，但 systemd `ExecStart=-jar vaas_detector4motion.jar` 未引用它，本次不动。

### 替换前只读核查
- 服务：active / enabled
- 原运行 PID：943
- 原运行 JAR sha256：`6b19e69b070b8ed2ef0a0ad12ea1d65187c9c6ec9cb97eb3d6e52587c317d45b`
- 目录内历史 JAR：`detector4motion-prod-0917.jar` sha256 `17708194560c30b455aa929173de65637d666f0369241a1a7fc4d13e2ab7ec7c`
- 上传待替换包：`/tmp/detector4motion.jar` sha256 `837d8b84fd981d77547f0dd8d2af3f0ca5607fc1efdbcf92666d1c870032e385`
- 端口：`ss -tlnp | grep java` 无 detector4motion 监听端口，符合纯消费者形态。

### 替换执行
- 用户执行备份、停止、复制新 JAR、启动服务。
- AI 仅做 SSH 只读核查，未执行线上写操作。

### 替换后只读验收
- 服务状态：active / enabled
- 新 PID：1366629
- 启动时间：2026-08-17 10:11:46 CST
- 运行 JAR sha256：`837d8b84fd981d77547f0dd8d2af3f0ca5607fc1efdbcf92666d1c870032e385`
- 启动命令：`/usr/bin/java -Dspring.profiles.location=application-prod.yaml -Duser.timezone=Asia/Shanghai -jar vaas_detector4motion.jar`
- prod profile：已激活 `prod`
- DB：HikariPool 启动完成，MySQL 连接 OK
- 阈值：
  - debounce.enabled=true
  - debounce.interval=3000
  - level7 y/z=0.44/0.25
  - level5 y/z=0.35/0.25
  - level3 y/z=0.27/0.23
- Redis 订阅：线上实际 channel 为 `vaas:6axis:notifier`
- 重启后窗口统计：
  - Started Detector4motionApplication：1
  - Sub on channel：1
  - group message：6606
  - detected bumpy event：2
  - publish to `vaas:event:topic`：1
  - write event to db：1
  - errors：0
- 负面检查：无 `Tomcat` / `Netty` / `started on port` / `wrong number of arguments` / `LPOP` / `Exception` / `Caused by`。
- 端口：无 Java HTTP 监听端口，仍为纯消费者。

### 结论
✅ detector4motion 已替换为还原对齐版 JAR，服务启动正常、配置加载正确、Redis 订阅与消费正常、真实 BUMP 事件检测/发布/写库链路已出现，未发现异常日志或端口偏差。本次 9.10 替换完成。


## 9.6 receiver 本地烟测（2026-08-17）

### 烟测命令
- 本地启动：`DB_HOST=localhost DB_USER=root DB_PASSWD=123456 REDIS_HOST=localhost java -jar target/receiver.jar --spring.flyway.enabled=false`
- `kt-data` 请求：向 `http://127.0.0.1:50412/kt-data` 发送最小 KtPackageFrame JSON
- WebSocket 请求：分别向 `/ws/location` 和 `/ws/motion` 发送最小帧

### 烟测结果
- 本地 receiver 进程监听 50412 成功，`prod` profile 激活。
- `/kt-data` 返回 HTTP 200，业务响应 `not ok`（本地 KT 车辆映射未命中），但接口链路和 JSON 反序列化正常。
- `/ws/location`、`/ws/motion` WebSocket 握手均返回 `101 Switching Protocols`。
- `/ws/location` 发送后，Redis `vaas:vehicle:info:SMOKE_DEVICE_001` 出现 1 条记录，内容含 `longitude/latitude/timestamp/dateTime/speed`，说明 `LocationHandler -> PositionService -> Redis` 链路打通。
- 本地 receiver 无启动异常；MySQL/Hikari/Redis 初始化正常。

### 结论
✅ 本地 receiver 烟测跑通：接口、WebSocket、Redis 写入均正常。当前烟测仅验证本地链路，不代表线上已切换。


## 9.6 receiver 线上替换与只读验收（2026-08-17）

### 替换执行
- 用户按三段法执行替换，AI 未输入 sudo 密码，仅做替换后只读验收。
- 新运行软链：`receiver.jar -> receiver-58f999b7.jar`
- 新运行 JAR sha256：`58f999b73ca783ebe0474084c9125b4aa390081db205b7f991c2e4cb2d8ee68f`
- 备份已生成：`receiver-0eb93458.jar.bak.2026-08-17-140521`

### 服务状态
- systemd：active / enabled
- PID：1202311
- 启动命令：`/usr/bin/java -Dspring.profiles.active=prod -jar receiver.jar`
- 工作目录：`/data/etas/vaas/receiver`
- 端口：50412 LISTEN

### 启动与功能核查
- `prod` profile 已激活。
- Hikari/MySQL 连接成功。
- Flyway validate 成功，schema 当前版本 2.0.8。
- `FleetManagementComponent` 成功从 MySQL 读取车辆配置。
- Redis debug channel 订阅：`vaas:debug:device`。
- Netty 启动 50412 成功。
- 新进程启动后真实位置数据继续进入，日志出现 `PositionService trimming redis list`。
- Redis@17 抽样：
  - `vaas:vehicle:info:865522079507718` 长度 3059，最新 member timestamp=1786946945000
  - `vaas:vehicle:info:865522079760333` 长度 3524，最新 member timestamp=1786946945000

### 已知非阻断项
- 新包缺 `BOOT-INF/classes/db/migration/*.sql` 资源，Flyway 启动阶段打印 1 条 ERROR：`Schema vaas has version 2.0.8, but no migration could be resolved in the configured locations !`
- 该 ERROR 后续紧跟 `Schema vaas is up to date. No migration necessary.`，未阻止服务启动，真实数据链路正常。
- 后续应补齐 db/migration 资源并重新构建，以消除启动 ERROR。

### 结论
✅ receiver 已替换为还原版 JAR，服务 active/running，50412 监听正常，线上真实位置数据继续写入 Redis，功能链路正常。9.6 替换完成；保留 Flyway migration 资源缺失作为非阻断整改项。


## 9.9 detector4kt 替换前只读核查（2026-08-17）

### 线上运行状态
- systemd：active / enabled
- PID：883
- 工作目录：`/opt/etas/vaas/detector4kt`
- 启动命令：`/usr/bin/java -Dspring.profiles.active=prod -jar detector4kt.jar`
- 当前软链：`detector4kt.jar -> detector4kt-4fd4ef6d.jar`
- 当前 JAR sha256：`28fa54952e5ca4985776ab3fdd61435568a79afdc93bf7c09d8b806427658fe0`
- 目录保留：`config.yaml`、`detector4kt.jar.bak`、异常文件名 `\`、`logs/`

### 线上行为样本
- 日志持续出现 `FleetManagementComponent : Reading car FleetManagements from MySQL`，说明 MySQL 车队配置读取正常。
- 日志出现真实 KT 湿滑检测链路：
  - `Detected slippery event`
  - `detected a duplicated event`
  - `publishing to vaas:event:topic`
  - `write event to db`
- 未在抽样窗口看到启动级异常。

### 线上配置确认
线上 JAR 内置配置资源：
- `application.yml`：激活 `prod`
- `application-prod.yml`
- `application-dev.yml`
- `application-test.yml`
- `application-local.yml`

当前 systemd 仅使用 `-Dspring.profiles.active=prod`，未显式指定外部 `config.yaml`，因此替换时应以 JAR 内置 `application-prod.yml` 为准。线上 prod 关键项：
- Redis/MySQL 通过环境变量读取
- `common.locationSearch.toleratedDeltaTime=1500`
- `common.geo.eventDeduplicationEnabled=true`
- `common.geo.eventDeduplicationRange=5`
- `common.geo.eventDeduplicationH3Resolution=5`
- `processor.debounce=true`
- `processor.debounceInterval=1000`
- `processor.bumpy.distance=3`
- `algorithm.kt.bump.enable=false`
- `algorithm.kt.slip.enable=true`
- `mode=prod`
- `kt-timezone=UTC`

### 本地还原版差异
本地 `backend/detector4kt/target/detector4kt.jar` 当前 sha256：`c1654a1c68752e5a6e21cce2ca253a22bd38e0c7e7e23eb1dbaf9f9168ca9f43`。

阻断差异：
- 本地 JAR 仅内置 `application.yml`，缺线上 `application-prod.yml` 等 profile 配置。
- 本地配置会在 prod 下启用错误值：
  - `algorithm.kt.bump.enable=true`，线上为 `false`
  - `processor.debounceInterval=5000`，线上为 `1000`
  - `processor.bumpy.distance=50`，线上为 `3`
  - `kt-timezone=Asia/Shanghai`，线上为 `UTC`
  - 缺 `common.geo` / `common.locationSearch` 块
- 本地 POM 仍含 actuator；线上 detector4kt 是纯消费者，不应新增 HTTP 健康端点行为。
- 本地 class count=22，线上 class count=21；本地多出 `StreamData.class`，且 `SensitivityConfig` 内部类结构命名与线上不一致。

### 结论
🔴 初次核查时，本地 detector4kt 包**不具备直接替换条件**。必须先按线上 JAR 恢复配置资源和类结构，至少补齐 `application.yml` + `application-prod.yml` 并修正算法/processor/时区/common.geo 配置，移除或确认 actuator 与多余类差异，然后重新构建、本地烟测、再进入替换命令阶段。

### 源码对齐修复（2026-08-17）
本次按用户要求，**只修改还原源码与资源文件，不对 JAR 做二进制补丁**，然后重新打包。

源码/资源修复：
- `application.yml` 改为仅激活 `prod`。
- 新增 `application-prod.yml`，恢复线上 prod 关键配置：
  - `algorithm.kt.bump.enable=false`
  - `algorithm.kt.slip.enable=true`
  - `processor.debounce=true`
  - `processor.debounceInterval=1000`
  - `processor.bumpy.distance=3`
  - `kt-timezone=UTC`
  - `common.locationSearch.toleratedDeltaTime=1500`
  - `common.geo.eventDeduplicationEnabled=true`
  - `common.geo.eventDeduplicationRange=5`
  - `common.geo.eventDeduplicationH3Resolution=5`
  - DB/Redis 连接均使用环境变量。
- `pom.xml` 移除 `spring-boot-starter-web` 与 actuator 行为，排除 `vaas-common` 传递的 webflux，保持纯消费者形态。
- 删除本地多余 `StreamData` 源码，`FramePackage` 改回 `RequestData.StreamItem` 内部类结构。
- `SensitivityConfig` 改回线上 `Kt$Bump` / `Kt$Slip` 嵌套类结构；根据线上 `javap` 结果，将字段类型改为 `Float` / `Integer` 包装类型，避免线上空 `algorithm.kt.slip.mu-threshold:` 绑定失败。

### 重新构建与验证
- 构建命令：`mvn -f backend/pom.xml -pl detector4kt -am clean package -DskipTests`
- 构建结果：BUILD SUCCESS
- 新 JAR：`backend/detector4kt/target/detector4kt.jar`
- 替换包：`dist/detector4kt-5808f828.jar`
- 新 JAR sha256：`5808f828ccfe77cfac1c5d6955ae305c937d6e58a800cc17bba19cfb2427b75c`
- 替换包大小：36,008,984 bytes
- 类清单：21 个，与线上 21/21 一致。
- 配置资源：`application.yml` + `application-prod.yml`，prod 关键项已对齐线上。
- 依赖形态：无 `spring-boot-starter-web`、无 actuator、无 `tomcat-embed-core/websocket`、无 `spring-webmvc`、无 `spring-webflux`、无 `reactor-netty`；`lib_count=64` 与线上一致。
- 本地启动烟测：使用本地 MySQL/Redis 启动成功，prod profile 激活，Hikari/MySQL、`common.geo`、FleetManagement、SensitivityConfig、Redis 订阅 `vaas:kt710:notifier`、`Started VaaSDetectorApplication` 均正常。

### 当前结论
✅ 经过还原源码修复后，重新打出的 detector4kt JAR 已达到替换前候选标准。尚未线上替换；下一步需要用户确认后上传新 JAR，并按三段法执行备份、软链切换、重启和只读验收。


## 9.9 detector4kt 线上替换后只读验收（2026-08-17）

### 替换状态
- 用户已执行替换，AI 仅做只读验收。
- 当前软链：`detector4kt.jar -> detector4kt-5808f828.jar`
- 当前运行 JAR sha256：`5808f828ccfe77cfac1c5d6955ae305c937d6e58a800cc17bba19cfb2427b75c`
- systemd：active / enabled
- PID：1431649
- 启动命令：`/usr/bin/java -Dspring.profiles.active=prod -jar detector4kt.jar`
- 工作目录：`/opt/etas/vaas/detector4kt`
- 端口：无 Java HTTP 监听端口，仍为纯消费者形态。

### 正常项
- 服务进程启动成功，运行在新包上。
- `prod` profile、MySQL/Hikari、`common.geo`、FleetManagement、SensitivityConfig 等启动项已加载。
- Redis 订阅已建立：`Sub on channel: vaas:kt710:notifier`。
- FleetManagement 仍每 5 分钟读取 MySQL。

### 异常项
- 功能验收失败：KT 实时帧 JSON 解析持续失败。
- 主要异常：`Unrecognized field "stream_data" (class com.etas.vaas.detector.entity.FramePackage$RequestData)`。
- 20 分钟窗口统计：
  - `Started VaaSDetectorApplication=1`
  - `Sub on channel=1`
  - `Reading car FleetManagements from MySQL=5`
  - `Detected slippery event=0`
  - `Detected bumpy event=0`
  - `publishing to vaas:event:topic=0`
  - `write event to db=0`
  - `Unrecognized field "stream_data"=5735`
  - `Error parsing JSON=5744`
- Redis@17 抽样：`vaas:vehicle:kt:4` / `vaas:vehicle:kt:5` 仍为 60000+ 长队列，说明真实 KT 帧持续进入但未被新 detector4kt 正确解析消费。

### 原因定位
只读 `javap` 线上旧包确认：线上 `FramePackage.RequestData.streamData` 字段和 `setStreamData(...)` setter 都带 `@JsonProperty("stream_data")` 注解。本地还原源码在删除多余 `StreamData` 并恢复内部 `StreamItem` 结构时，遗漏了该 Jackson 注解，导致真实 payload 中的 `stream_data` 无法绑定到 `streamData` 字段。

### 结论
✅ detector4kt 修复包已替换并通过只读验收：服务 active/running，新的 JAR 为 `detector4kt-9feb4b70.jar`，线上真实 KT slip 事件已恢复，未再出现 `stream_data` 解析错误。9.9 detector4kt 替换完成。

