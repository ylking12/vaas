# VaaS 项目复现 - 任务跟踪总表

> 更新时间: 2026-08-17 | 已完成任务的详细记录已归档到 [ARCHIVE.md](./ARCHIVE.md)

## 进度概要

| Phase | 总任务数 | 完成 | 进行中 | 待开始 | 进度 |
|-------|---------|------|--------|-------|------|
| P1 参考素材提取 | 9 | 9 | 0 | 0 | 100% | ⬅️ 1.10 已取消（不计入计划总数） |
| P2 后端还原 | 20 | 20 | 0 | 0 | 100% ✅ |
| P3 前端还原 | 6 | 6 | 0 | 0 | 100% ✅ |
| P4 集成验证 | 4 | 4 | 0 | 0 | 100% ✅ |
| P5 算法验证 | 2 | 2 | 0 | 0 | 100% ✅ |
| P6 生产级安全与运维加固 | 15 | 0 | 0 | 15 | 0% | 📋 未开始；不作为本轮还原等价替换的硬前置 |
| P7 大屏重构 | 33 | 33 | 0 | 0 | 100% ✅ |
| P7+ 后续迭代 | 7 | 7 | 0 | 0 | 100% ✅ |
| P8 工程优化 | 16 | 16 | 0 | 0 | 100% ✅ |
| P9 后台服务逐步线上替换 | 17 | 8 | 0 | 9 | 47% | 🔄 核心 Java 服务替换完成；9.8 admin-api、9.11 Python/模拟器 待确认 |
| **合计** | **129** | **105** | **0** | **24** | **81%** | ⏳ 剩余 P6 + P9 |

---

## Phase 1: 参考素材提取 (9/10, 1已取消)

```
Phase 1
├── 1.1  [完成] 完整解压 etas.tar，提取所有后端文件
├── 1.1a [完成] ←[分支] 同步新发现资产到 PROJECT_RULES.md
├── 1.2  [完成] 大屏前端 Source Map 全量还原
├── 1.2a [完成] ←[分支] 验证还原的前端源码完整性
├── 1.2b [完成] ←[分支] 从编译 JS 中反提取 Vue 组件脚本逻辑
├── 1.3  [完成] 安装 CFR 并反编译 receiver.jar — 13个业务类
├── 1.4  [完成] 安装 CFR 并反编译 vaas_backend.jar — 62个业务类
├── 1.5  [完成] 安装 CFR 并反编译 admin/admin.jar — 34个业务类
├── 1.6  [完成] 安装 CFR 并反编译 detector4kt.jar — 15个业务类
├── 1.7  [完成] 安装 CFR 并反编译 vaas_detector4motion/ — 8个业务类
├── 1.8  [完成] 安装 CFR 并反编译 vaas-trajectory-simulator — 18个业务类
├── 1.9  [完成] 提取 Python 源码到 simulator/python/
└── 1.10 [取消] 关键业务配置提取
```

---

## Phase 2: 后端还原 (20/20) ✅

```
Phase 2
  -- receiver (port 50412) --
  ├── 2.1  [完成] 反编译代码整理到 backend/receiver/ (13个业务类)
  ├── 2.1a [完成] 反编译 common-0.0.1-SNAPSHOT.jar (59个公共类)
  ├── 2.2  [完成] pom.xml 还原 (Spring Boot 3.5.3 + WebFlux)
  ├── 2.3  [完成] application.yml + config.yaml 配置重写
  └── 2.4  [完成] 编译验证 & bug修复

  -- vaas-backend (port 50410) --
  ├── 2.5  [完成] 反编译代码整理到 backend/vaas-backend/ (62个业务类)
  ├── 2.5a [完成] 反编译类型问题修复 (~65处编译错误)
  ├── 2.6  [完成] 算法模块提取到 backend/algorithm/
  ├── 2.7  [完成] pom.xml + 多环境配置还原
  └── 2.8  [完成] 编译验证 & bug修复

  -- detector4kt --
  ├── 2.9  [完成] 反编译代码整理到 backend/detector4kt/ (15个业务类)
  ├── 2.10 [完成] pom.xml + config.yaml 还原
  └── 2.11 [完成] 编译验证 & bug修复

  -- detector4motion --
  ├── 2.12 [完成] 反编译代码整理 (8个业务类)
  ├── 2.13 [完成] pom.xml + config.yaml 还原
  └── 2.14 [完成] 编译验证 & bug修复

  -- admin (管理后台API) --
  ├── 2.15 [完成] 反编译代码整理到 backend/admin-api/ (34个业务类)
  ├── 2.16 [完成] pom.xml + 配置还原 + 编译验证 ✅
  ├── 2.17 [完成] 数据库 DDL + Redis 结构定义还原 ✅
  └── 2.18 [完成] 基础设施部署（MySQL + Redis）✅
```

---

## Phase 3: 前端还原 (6/6) ✅

```
Phase 3
├── 3.1 [完成] dashboard - Source Map 还原源码到 frontend/dashboard/
├── 3.2 [完成] dashboard - package.json + Webpack 配置还原
├── 3.3 [完成] dashboard - npm install && 编译验证
├── 3.4 [完成] admin - 从编译JS提取路由/页面/API结构
├── 3.5 [完成] admin - Vue 3 + Vite 项目骨架搭建
└── 3.6 [完成] admin - 从反混淆JS还原3个核心页面
```

---

## Phase 4: 集成验证 (4/4) ✅

```
Phase 4
├── 4.1 [完成] 后端 6 个微服务联调 — 5个SpringBoot + Python算法全部启动运行 ✅
├── 4.2 [完成] 模拟器数据注入测试 — bump/slip 事件成功写入 MySQL + Redis ✅
├── 4.3 [完成] 前后端全链路联调 — 大屏/管理后台可访问，API代理打通 ✅
└── 4.4 [完成] 功能完整性验证 & 收尾
      ├── 4.4a ✅ 修复大屏API空指针
      ├── 4.4b ✅ 一键启动脚本（start.sh / stop.sh）
      ├── 4.4c ✅ 数据库初始化脚本（init-db.sql + init-db.sh）
      ├── 4.4d ✅ 清理临时文件 + 文档更新
      └── 4.4e ✅ 模拟数据注入脚本（inject-data.sh）
```

---

## Phase 5: 算法验证 (2/2) ✅

```
Phase 5

  验证目标：确保反编译还原的算法逻辑与原始JAR行为一致
  涉及算法：
  ├── detector4kt/BumpyProcessor — KT710颠簸检测
  │    5条件：轮速差>1, 转向比差值>=3, 平均制动压力<1, 相关系数<0.5, 平均车速<9.7
  ├── detector4kt/SlipperyProcessor — KT710湿滑检测
  ├── detector4motion/BumpyProcessor4Motion — 6轴运动颠簸检测
  │    7帧滑动窗口，极值分析(波峰波谷)，Level 3/5/7 阈值分级
  └── Python wit_bumpy_algorithm — 六轴传感器颠簸（scipy信号分析，与Java逻辑等价）

├── 5.1 [完成] 算法单元测试 ✅
│     ├── BumpyProcessorTest：7/7 ✅
│     ├── BumpyProcessor4MotionTest：10/10 ✅
│     └── Python 交叉验证：9/9 ✅
└── 5.2 [完成] 原始JAR黑盒对比测试 ✅
      ├── BumpyProcessor 字节码对比：完全一致 ✅
      └── BumpyProcessor4Motion 字节码对比：完全一致 ✅
```

---

## Phase 6: 生产级安全与运维加固 (0/15) 📋

> **阶段定位**：Phase 6 不是“还原代码等价替换”的技术前置，而是正式生产环境的安全、配置、运维和治理加固阶段。
>
> **与 Phase 9 的关系**：Phase 9 已按“先核对线上行为，再逐项替换还原包”的方式完成了核心 Java 服务替换；这不代表 Phase 6 已完成，也不代表当前系统已经达到生产级安全加固标准。Phase 6 的 15 项目前仍全部未完成，后续必须单独推进并验收。
>
> **当前风险结论**：核心业务链路已经运行还原源码构建的服务，但仍存在明文通信、鉴权不足、凭据治理、监控告警、容灾和运维规范等生产风险。当前状态应描述为“功能替换已完成，生产加固未完成”，不能描述为“生产上线整改完成”。

```text
优先级: 🔴 P0=必须解决  🟠 P1=强烈建议  🟡 P2=建议考虑
当前状态: 0/15；以下项目均未完成，不因 Phase 9 已替换服务而自动视为完成

🔴 P0: 必须解决（6项）
├── 6.1 ⏳ 认证与鉴权 — API/WS 全开放，需 JWT + WS 连接认证 + CORS 白名单
├── 6.2 ⏳ 通信加密 — 全部 HTTP/WS 明文，需 Nginx HTTPS/WSS
├── 6.3 ⏳ 数据库密码 — MySQL / Redis 凭据治理与 admin 密码文件整改
├── 6.4 ⏳ 容器化部署 — 手动 java -jar，需 Docker + docker-compose
├── 6.5 ⏳ 配置管理 — 硬编码 .env / localhost，需多环境配置体系
│   └── 📌 已确认：线上大屏原始编译版使用相对路径 `/spring/v1/`（Nginx 反代），非 localhost 直连
└── 6.6 ⏳ Nginx 反代 — 统一入口、HTTPS 终止和生产部署规范

🟠 P1: 强烈建议（4项）
├── 6.7 ⏳ 高可用与容灾 — 单点 MySQL/Redis/微服务，缺主从/Sentinel
├── 6.8 ⏳ OBU 协议适配层 — 硬编码 KT710 协议，需 ProtocolAdapter 接口层
├── 6.9 ⏳ WebSocket 安全加固 — 连接数/队列/频率限制
└── 6.10 ⏳ 外部依赖管理 — 地图 API Key / 气象站 SDK / OCR / TrueLicense

🟡 P2: 建议考虑（5项）
├── 6.11 ⏳ 监控与告警 — Prometheus + Micrometer + Grafana
├── 6.12 ⏳ 数据生命周期 — 事件保留策略 / Redis ZSet 清理 / MySQL 归档
├── 6.13 ⏳ 日志体系 — JSON 结构化 / 级别配置 / 审计日志 / 轮转策略
├── 6.14 ⏳ 文档与应急预案 — 部署/运维手册 + 宕机/数据丢失预案
└── 6.15 ⏳ 合规与法律 — GPS 轨迹敏感数据 / 数据最小化 / 开源合规
```

---

## Phase 7: 大屏重构（Vue 3 + 高德地图）

```
Phase 7 (33/33) ✅ 首版完成 + 迭代
  说明：Source Map 还原的 CSS 和 render 函数无法正常集成，改用 Vue 3 + Vite + Element Plus + 高德地图全新构建。后端 API 全部复用，功能 100% 覆盖。
  文档: docs/plans/2026-06-11-dashboard-redesign.md

  P1 - 项目骨架 (4) ✅
  ├── 1.1 ✅ 创建 Vue 3 + Vite 项目    ├── 1.2 ✅ 路由 + 环境变量
  ├── 1.3 ✅ Axios + API 封装            └── 1.4 ✅ Pinia 状态管理

  P2 - 主布局 + 地图 (6) ✅
  ├── 2.1 ✅ 主布局 DashboardPage        ├── 2.2 ✅ 顶栏 TopBar
  ├── 2.3 ✅ 高德地图初始化              ├── 2.4 ✅ 车辆位置标记
  ├── 2.5 ✅ 事件标记 + 气象站标记        └── 2.6 ✅ 标记弹窗

  P3 - 左侧图层面板 (4) ✅
  ├── 3.1 ✅ 面板折叠/展开                ├── 3.2 ✅ 实时车队数据
  ├── 3.3 ✅ 路网状态                     └── 3.4 ✅ 实时气象数据

  P4 - 内容面板 (6) ✅
  ├── 4.1 ✅ DrawerContainer              ├── 4.2 ✅ 图层面板复用
  ├── 4.3 ✅ 历史24h路面状态 (ECharts)     ├── 4.4 ✅ 道路实时路况
  ├── 4.5 ✅ 告警列表 (含导出Excel)        └── 4.6 ✅ 服务统计

  P5 - 时间轴 + 实时通信 (4) ✅
  ├── 5.1 ✅ 时间轴 TimeSlider            ├── 5.2 ✅ SSE 实时接入
  ├── 5.3 ✅ 定时刷新 15min               └── 5.4 ✅ 时间轴联动 API

  P6 - 集成联调 (4) ✅
  ├── 6.1 ✅ 高德地图暗色主题             ├── 6.2 ✅ 全链路数据联调
  ├── 6.3 ✅ 异常处理                     └── 6.4 ✅ 兼容性 + 性能

  P7-iter.1 - 数据接入 (1) ✅
  └── 7-iter.1 ✅ 联网车辆数 + 降水量数值接入

  P7-iter.2 - 视觉与重构 (4) ✅
  ├── 7-iter.2.1 ✅ 弹窗组件替换 Popup.vue (4种类型 + Teleport)
  ├── 7-iter.2.2 ✅ 地图标记内嵌 SVG + 脉冲动画
  ├── 7-iter.2.3 ✅ LayerPanel 抽取公共组件
  └── 7-iter.2.4 ✅ 细节调整 (S图标→SVG九宫格、彩虹→棕金渐变、抽屉100%→88%)
```

### P7+ 后续迭代 — 7/7 已完成 ✅（详见 [ARCHIVE.md](./ARCHIVE.md)）

```
├── 🎨 大屏样式调优
│   ├── ✅ P7-iter.3 时间轴样式复原（9处修复）
│   ├── ✅ P7-iter.4 drawer 88% 宽半透明黑
│   ├── ✅ P7-iter.5 方案 A' — hover/click 直接打开 drawer
│   ├── ✅ P7-iter.6 flood toggle 取消清空 marker
│   ├── ✅ P7-iter.7 网联车 marker 修复 + 位置仿真器
│   └── ✅ P7-iter.8 原版 PNG 图标替换（29个）
├── 🔧 功能补齐
│   └── ✅ B1 时间轴联动失效 bug 修复
└── 📊 数据监控看板 / 微服务验证可视化（预留）
```

### 演示辅助 - 常驻颠簸点位保活 ✅ (2026-07-23)

> 用户需求：演示/验收时大屏需一直展示一个固定颠簸点位。**非原版还原产物**，为新增演示辅助代码，来源已标注。

- **文件**：[DemoBumpEventKeepAlive.java](backend/vaas-backend/src/main/java/com/etas/vaas/backend/cron/DemoBumpEventKeepAlive.java)（vaas-backend/cron，`@Component`）
- **配置**：[application.yml](backend/vaas-backend/src/main/resources/application.yml) `demo.bump.*`（enabled/lng/lat/road-name/level）
- **机制（方案 A）**：启动时向 Redis ZSet(`bumpEventKey`) 注入一条 bump 事件，每 6h 对同一 member 重新 ZADD 仅刷新 score，对抗 `CleanEventZSet` 的 24h 清理；member 运行期间固定（展示首次注入/启动时间），重启时按固定 eventId 清理旧 member 防累积
- **点位参数**：经度 120.379123 / 纬度 31.585633 / 团结路庄桥路 / level 不传
- **红线自查**：直接 `addToZSet` 不走 `persistEvent`（不脏 MySQL）；不调 `publishEvent`（不影响 SSE）；不改前端、不改原版业务逻辑/Redis key 命名/API 签名/表结构
- **生效方式**：重启 vaas-backend 后，刷新大屏页面即可看到点位（score=now 落在默认窗口 [now-23h, now] 内）
- **关闭**：生产环境设 `demo.bump.keepalive.enabled=false`
- **编译验证**：vaas-backend 模块编译通过 ✅（DemoBumpEventKeepAlive.class 已生成）
- **线上部署包**（2026-07-23）：`dist/vaas-backend-20260723.tar.gz`（sha256 `c4096714...`，66MB），含 `vaas_backend.jar`（sha256 `8609c1cd...`）+ `README-deploy.md`
  - 部署目标：192.168.112.15（`vaas_backend`，`/opt/etas/vaas/vaas_backend/`），只换 jar，外部配置+unit 不动
  - **保活已开启**（用户确认）：部署后往生产 Redis 注入常驻颠簸点位（120.379123, 31.585633, 团结路庄桥路，eventId=`DEMO_BUMP_KEEPALIVE`）
  - 验证：MethodParameters=11（-parameters 在）、本地启动 + API 验证注入成功
  - 回滚/清理：见 README-deploy.md（回滚旧 jar + 调 `/delete-event` 删假事件）
  - 状态：**已部署到 15**（2026-07-23 15:29:38 注入成功，PID 1801439，日志确认 lng/lat/roadName 正确）
- **v1 bug（2026-07-23）**：演示事件未设 deviceId（null），getAlarmList 调 `FleetManagementComponent.getDeviceId2CarMap()`（ConcurrentHashMap，不允许 null key）.get(null) 抛 NPE，告警列表 500 全空。get-last-24h-bump-event 不受影响（不查 deviceId）
- **v2 修复**：`DemoBumpEventKeepAlive.buildMemberJson` 补 `deviceId="DEMO_DEVICE"`（占位 imei），本地验证 get-alarm-list 返回 200 + 告警正常
- **v2 部署包**：`dist/vaas-backend-20260723.tar.gz`（tar sha256 `e491a24b...`），含 `vaas_backend.jar`（sha256 `e2ec5012...`），README 标注 v2 修复版
- **v2 状态**：**已部署到 15**（2026-07-23 15:57:22 重启，PID 1807317；get-alarm-list 恢复 HTTP 200，演示点位+真实告警均正常；-parameters 200；cleanupOldDemoMember 已自动清 v1 旧假事件）

---

### 新增功能 - 采集车上报排行 ✅ (2026-08-05)

> 用户需求：大屏展示每辆采集车当天上报的颠簸/湿滑点位数量。**非原版还原产物**，新增功能，来源已标注。

- **后端**：
  - [VehicleStatService.java](backend/vaas-backend/src/main/java/com/etas/vaas/backend/service/web/VehicleStatService.java)（新增 `@Service`）：按 event 表 `event_time` 日期 + `source_type` in(kt710,motionSensor) 查车辆事件，`groupingBy(sourceId)` + 映射 imei->车牌（FleetManagementComponent）+ 脱敏，按 totalCount 降序
  - [VehicleEventCountResponse.java](backend/vaas-backend/src/main/java/com/etas/vaas/backend/dto/response/VehicleEventCountResponse.java)（新增 DTO：plate/bumpCount/slipCount/totalCount，去掉积水列）
  - [EventCountByVehicleRequest.java](backend/vaas-backend/src/main/java/com/etas/vaas/backend/dto/request/EventCountByVehicleRequest.java)（新增请求 DTO：date）
  - [EventController.java](backend/vaas-backend/src/main/java/com/etas/vaas/backend/controller/web/EventController.java)：新增 `POST /get-event-count-by-vehicle`
- **前端**：
  - [api/index.js](frontend/dashboard/src/api/index.js)：新增 `getEventCountByVehicle(date)`
  - [DashboardPage.vue](frontend/dashboard/src/views/DashboardPage.vue)：`drawer-right` 统计区底部新增"采集车上报排行"面板（el-date-picker 选日期 + el-table 排行：车牌/颠簸/湿滑/合计），抽屉打开时加载、日期切换重查
- **决策**：积水(PONDING)是气象站上报非采集车，去掉积水列，只统计车辆颠簸/湿滑
- **验证**：后端编译 + package 通过；本地 `/get-event-count-by-vehicle` 返回 HTTP 200（本地 event 表无数据返回 `[]`，API 跑通）；前端 `npm run build` 通过
- **生产时区修复（2026-08-05）**：`VehicleStatService` 的"今天"改用 `ZoneId.of("Asia/Shanghai")`（15 服务器 UTC 时区，否则边界时段查错日期）；已重新 package，jar sha256 `c8e258e4...`
- **重新打包（2026-08-12）**：后端 `dist/vaas-backend-20260812.tar.gz`（tar sha256 `d902340c...`，jar sha256 `f1fa62aa...`）；前端轻量包 `dist/dashboard-frontend-20260812.tar.gz`（sha256 `40e528cb...`，12MB，排除大型 roadNetImg/road_network_image，沿用线上既有路网静态资源）；前端全量包 `dist/dashboard-frontend-20260812-full.tar.gz`（sha256 `f56c3a06...`，1.0G，包含完整 www/、roadNetImg/、road_network_image/）
- **数据一致性**：SQL 逻辑与 SSH 17 实证一致（2026-08-05 当天 10 辆车、bump 110、slip 1）
- **红线自查**：不改原版业务逻辑/算法/API/表结构/Redis key；复用 DailyReportService 范式 + FleetManagementComponent；新增文件标注"非原版还原产物"
- **状态**：代码完成 + 本地验证通过 + 部署包已生成，**待用户自行部署**（后端 jar->15，前端构建产物->18）

---

## Phase 8: 工程优化 (16/16) ✅

```
Phase 8 (16/16) ✅ 全部完成

  O1~O4 — 红伤组
  ├── 8.1 ✅ start.sh JDK 自适应（环境变量→系统→macOS java_home→Linux）
  ├── 8.2 ✅ status.sh — PID + 端口 + HTTP 健康检查
  ├── 8.3 ✅ logs.sh — 日志列表 + 实时跟踪 (-f) + 行数控制 (-n)
  └── 8.4 ✅ restart.sh — 单服务重启 + 强制 kill + --no-start

  O5~O9 — 工程规范组
  ├── 8.5 ✅ .gitignore 补全
  ├── 8.6 ✅ pre-commit hook（TASK_TRACKING.md 同步检查）
  ├── 8.7 ✅ CHANGELOG.md（v0.1.0 → v0.15.0）
  ├── 8.8 ✅ verify.sh（npm build + mvn compile + pytest）
  └── 8.9 ✅ README Mermaid 架构图

  O10~O16 — 长线优化
  ├── 8.10 ✅ vaas-backend OpenAPI/Swagger
  ├── 8.11 ✅ inject-data.sh 使用文档
  ├── 8.12 ✅ stop.sh -f 强制 kill
  ├── 8.13 ✅ start.sh 端口冲突检测
  ├── 8.14 ✅ CLAUDE.md / PROJECT_RULES.md 查重
  ├── 8.15 ✅ LICENSE (MIT)
  └── 8.16 ✅ Actuator 健康检查（5个微服务）
```

验证结果详见 [ARCHIVE.md](./ARCHIVE.md#p8-验证结果)。

---

## Phase 9: 后台服务逐步线上替换 (8/17, 0进行中) 🔄

> 详细操作日志、命令与回滚记录见 [docs/phase9-replacement-log.md](./docs/phase9-replacement-log.md)

**当前状态概览**
- 已完成：9.1 线上后台服务拓扑盘点；9.2 线上配置与运行参数备份；9.3 替换准入关系补记；9.4 线上接口与数据行为基线采样；9.6 receiver；9.7 vaas-backend（15 已换，18 已停未换）；9.9 detector4kt；9.10 detector4motion
- 进行中：无
- 待确认：9.8 admin-api；9.11 Python / trajectory-simulator 分类确认
- 待开始：9.5 回滚预案与演练；9.12-9.17 部署、联动、数据一致性、灰度、回归、收尾
- 说明：Phase 9 的目标是“还原代码等价替换”，不是完成 Phase 6 的生产级安全加固。Backend@18 仅停止未换 JAR；Python/模拟器当前只确认存在相关脚本或工具，未确认其为独立线上生产服务。

**替换范围**
- 15：vaas-backend、detector4motion、detector4kt
- 16：receiver
- 18：Backend 已停止未换；Nginx/OpenResty 与大屏不纳入 JAR 替换
- 17：Redis / MySQL 为中间件，不替换

**Phase 9 明细（精简版）**

| 编号 | 状态 | 事项 | 当前结论 / 下一步 |
|------|------|------|------------------|
| 9.1 | 完成 | 线上后台服务拓扑盘点 | 4 台服务器、5 个原后台服务、Nginx/Redis/MySQL 拓扑已确认 |
| 9.2 | 完成 | 线上配置与运行参数备份 | receiver、detector4kt、detector4motion 的 unit/env/JAR/config 已采集 |
| 9.3 | 完成 | 替换准入关系补记 | 已明确：本轮按“还原等价替换”推进，Phase 6 生产加固未完成且需后续单独验收 |
| 9.4 | 完成 | 线上接口与数据行为基线采样 | 大屏 API、Redis key、MySQL 表、日志格式已有替换前基线 |
| 9.5 | 待开始 | 回滚预案与演练 | 已执行服务级备份和回滚路径设计，但还未形成统一演练记录 |
| 9.6 | 完成 | receiver@16 替换 | 已替换为 `receiver-58f999b7.jar`，服务和真实数据链路正常；Flyway 迁移资源缺失为非阻断待整改 |
| 9.7 | 完成 | vaas-backend 替换 | Backend@15 已替换并通过线上回归；Backend@18 已停止未换，后续如重启需先评估 |
| 9.8 | 待确认 | admin-api 替换 | 后端源码已还原编译，但管理后台前端不完整；替换前需定义可验收范围 |
| 9.9 | 完成 | detector4kt@15 替换 | 已替换为 `detector4kt-9feb4b70.jar`，`stream_data` 兼容修复后服务和真实 slip 检测正常 |
| 9.10 | 完成 | detector4motion@15 替换 | 已替换为还原包，纯消费者形态、Redis 消费、BUMP 检测和写库正常 |
| 9.11 | 待确认 | Python / trajectory-simulator 分类 | 目前仅确认存在脚本/工具范围，未确认是独立线上生产服务；下一步只读盘点 |
| 9.12 | 待开始 | 统一启动/停止/健康检查脚本适配线上 | 需基于线上真实 systemd/JAR 名称修订 |
| 9.13 | 待开始 | Nginx 反代与前后端联动确认 | 需确认 `/spring/v1/`、SSE、静态大屏、可选 `/admin/` 路径 |
| 9.14 | 待开始 | MySQL / Redis 数据一致性与容量检查 | 需做替换后长期数据量、key 类型、事件数量对照 |
| 9.15 | 待开始 | 全链路灰度/分批切换 | 核心 Java 链路已逐服务替换；仍需形成正式灰度/切换记录 |
| 9.16 | 待开始 | 生产回归验收 | 需按大屏、事件检测、联网车辆、告警、历史图表、SSE、管理后台形成验收表 |
| 9.17 | 待开始 | 替换收尾与文档归档 | 汇总版本包、回滚包、环境变量、运维命令和剩余风险 |

## 已记录问题 / 已知缺项

> 历史修复与现场记录已归档到 [ARCHIVE.md](./ARCHIVE.md)。本节只保留当前仍需处理或确认的项。

1. **[admin 前端还原不完整]** — 无 Source Map，系统配置/设备管理/权限管理/数据报表等仍未完整还原。
2. **[receiver 新包缺迁移资源]** — `db/migration/*.sql` 资源缺失，Flyway 启动仍会提示 warning；服务本身已可用，后续再补齐资源。
3. **[Python / trajectory-simulator 待核]** — 先确认它是否是独立线上生产服务；当前按测试工具/辅助脚本处理。
4. **[Backend@18 停机未换]** — 18 号机上的原版 jar 仍保留且服务已停止，若恢复启动需先重新评估替换。


## 项目文档索引

| 文档 | 位置 | 说明 |
|------|------|------|
| 替换影响分析报告 | [docs/replacement-impact-report.md](./docs/replacement-impact-report.md) | 后端整站替换到线上的全面影响分析（2026-06-26） |

## 待部署事项

> 大屏线上替换已完成，原待部署清单已归档到 [ARCHIVE.md](./ARCHIVE.md)。
> 当前无独立待部署项；新的部署/替换目标统一纳入 Phase 9 或后续任务。
