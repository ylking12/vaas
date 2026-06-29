# VaaS 后端整站替换影响分析报告

> 生成日期：2026-06-26
> 场景：**线上大屏不动，将所有后端服务替换为 vaas-reproduction 复原版**

---

## 目录

1. [场景定义](#1-场景定义)
2. [影响总览](#2-影响总览)
3. [🔴 阻断性影响（5项）](#3-阻断性影响)
4. [🟠 高风险影响（8项）](#4-高风险影响)
5. [🟡 中风险影响（6项）](#5-中风险影响)
6. [🔵 低风险影响（7项）](#6-低风险影响)
7. [⚪ 已验证无影响（5项）](#7-已验证无影响)
8. [替换检查清单](#8-替换检查清单)
9. [灰度替换步骤](#9-灰度替换步骤)
10. [附：后端服务全景](#10-后端服务全景)

---

## 1. 场景定义

| 项 | 内容 |
|------|------|
| **不动** | 线上大屏前端（已部署的 Vue SPA dist/）、Nginx 配置、域名、CDN |
| **替换** | 5 个后端微服务：receiver / detector4kt / detector4motion / vaas-backend / admin-api |
| **来源** | vaas-reproduction 项目，从线上 JAR 反编译复原的源码编译产物 |
| **基准** | 反编译自同一套线上 JAR，API 签名、算法逻辑字节码已验证一致 |

---

## 2. 影响总览

| 等级 | 含义 | 数量 | 关键编号 |
|------|------|------|----------|
| 🔴 **阻断性** | 不处理则服务不可用 | 5 | R1–R5 |
| 🟠 **高风险** | 数据或功能异常，需适配 | 8 | H1–H8 |
| 🟡 **中风险** | 功能性影响但可绕行 | 6 | M1–M6 |
| 🔵 **低风险** | 建议优化，不影响运行 | 7 | L1–L7 |
| ⚪ **无影响** | 已验证不需处理 | 5 | S1–S5 |

---

## 3. 阻断性影响

### 🔴 R1 — TrueLicense 证书验证缺失

| 项目 | 内容 |
|------|------|
| **服务** | vaas-backend |
| **文件** | `backend/vaas-backend/src/.../license/LicenseVerifier.java` |
| **问题** | `KeyStoreParam publicKeyStoreParam = null; // placeholder`，项目中无 `.lic` / `.store` / `.jks` 证书文件 |
| **后果** | 线上 vaas-backend 若启用了 license 校验 → 服务启动失败 |
| **处理** | 获取线上环境的 `license.lic` + 密钥库文件，放入 classpath 或配置正确路径 |

### 🔴 R2 — MySQL 连接配置

| 项目 | 内容 |
|------|------|
| **涉及** | 全部 5 个微服务 |
| **当前值** | `jdbc:mysql://${DB_HOST:localhost}:3306/vaas`，user=`root`，password=`空` |
| **问题** | 线上若密码非空、数据库名非 `vaas`、或要求 SSL 连接，则连不上 |
| **数据库名** | `vaas` 硬编码在 JDBC URL 中，不能通过环境变量改 |
| **处理** | 设环境变量 `DB_HOST` / `DB_USER` / `DB_PASSWD`；或直接改 application.yml |

### 🔴 R3 — Redis 连接配置

| 项目 | 内容 |
|------|------|
| **涉及** | 全部 5 个微服务 |
| **当前值** | `${REDIS_HOST:localhost}`:6379, database 0, 无密码, lettuce pool max-active=16 |
| **问题** | 线上若有 Redis 密码、或用了不同 db index、或需要 TLS，则连不上 |
| **处理** | 设环境变量 `REDIS_HOST`；检查 Redis 密码和 database index 配置 |

### 🔴 R4 — 气象站传感器 ID 硬编码

| 项目 | 内容 |
|------|------|
| **服务** | vaas-backend |
| **文件** | `vaas-backend/target/classes/application.yml` sensors.road-ids |
| **当前值** | 5 组传感器 ID（Station/RoadCondition/Atmospheric 三字段）均硬编码 |
| **后果** | 线上若传感器设备 ID 不同，所有路侧数据（积水/结冰/路面温度）读取不到或错位 |
| **处理** | 替换为线上实际的传感器 ID 映射 |

### 🔴 R5 — 大屏前端 API 基地址

| 项目 | 内容 |
|------|------|
| **复原版当前值** | `VITE_API_BASE=http://localhost:50410/spring/v1` |
| **线上原始值** | `baseUrl: "/spring/v1/"`（相对路径，见 `前端代码/www/js/src_views_dashboard_vue.68d9af79.js`） |
| **存在位置** | `frontend/dashboard/.env` + `src/api/index.js` fallback + `DashboardPage.vue` fallback |
| **问题** | 线上大屏使用**相对路径** `/spring/v1/`，通过 Nginx 反代访问后端（同源）。复原版默认是 `localhost:50410` 直连，替换大屏时必须改为相对路径，否则跨域请求失败 |
| **处理** | 构建前将 `.env` 中的 `VITE_API_BASE` 改为 `/spring/v1/`（相对路径），然后 `npm run build` |

---

## 4. 高风险影响

### 🟠 H1 — receiver WebSocket 端口/路径与车端绑定

| 项目 | 内容 |
|------|------|
| **服务** | receiver |
| **当前值** | 端口 50412，WS 路径 `/ws/kt` `/ws/motion` `/ws/location` |
| **风险** | 线上 OBU 设备可能配了不同的端口或路径（或加了 `?token=` 鉴权参数） |
| **处理** | 确认线上 OBU 连接配置；若不同则修改 receiver 配置 |

### 🟠 H2 — WebSocket 无鉴权

| 项目 | 内容 |
|------|------|
| **服务** | receiver |
| **当前值** | 所有 WebSocket 端点完全开放，无 token/签名校验 |
| **后果** | 若线上 OBU 连接带鉴权参数，你的 receiver 不会校验；若线上本来无鉴权则无影响 |
| **处理** | 确认线上是否有 WS 鉴权机制，如有则需补充实现 |

### 🟠 H3 — CORS 全开

| 项目 | 内容 |
|------|------|
| **涉及** | vaas-backend 全部 Controller |
| **当前值** | 所有 Controller 使用 `@CrossOrigin`（无参数 = 允许所有来源） |
| **后果** | 任意第三方网站可以跨域请求 API，存在数据泄露风险 |
| **处理** | 替换为 `@CrossOrigin(origins = "线上域名")` 或在 Nginx 层统一管控 |

### 🟠 H4 — admin-api 密码文件

| 项目 | 内容 |
|------|------|
| **服务** | admin-api |
| **当前值** | `config/password.txt` 用 BCrypt 存 admin 密码 |
| **风险** | 线上若不存在此文件、路径不同、或用 LDAP/OAuth/SSO 等其他认证方式 |
| **处理** | 确认线上认证方式；复制正确的密码文件 |

### 🟠 H5 — 无 JVM 参数（稳定性风险）

| 项目 | 内容 |
|------|------|
| **涉及** | 全部 5 个微服务 |
| **当前值** | 裸 `java -jar`，无 `-Xmx` `-Xms` GC 策略 `-Dspring.profiles.active` |
| **后果** | 服务可能 OOM；GC 行为不可预测；无法区分环境配置 |
| **处理** | 添加 JVM 参数（参考：`-Xmx1024m -Xms512m -XX:+UseG1GC`） |

### 🟠 H6 — Swagger API 文档暴露

| 项目 | 内容 |
|------|------|
| **服务** | vaas-backend |
| **路径** | `/spring/v1/swagger-ui.html` |
| **后果** | 线上环境可直接查看所有 API 定义，属于信息泄露 |
| **处理** | 生产 profile 加 `springdoc.api-docs.enabled=false` |

### 🟠 H7 — 启动健康检查不完善

| 项目 | 内容 |
|------|------|
| **涉及** | receiver / detector4kt / detector4motion / admin-api |
| **当前值** | `start.sh` 对这些服务仅通过 30 秒超时等待，无实际 HTTP 健康检查 |
| **后果** | 服务进程在但业务不可用时，启动脚本误判为成功 |
| **处理** | 为每个服务添加专用健康检查（或统一用 `/actuator/health`） |

### 🟠 H8 — 路网图图片不可用

| 项目 | 内容 |
|------|------|
| **涉及** | 前端大屏 MapView.vue |
| **URL 模式** | `/road_network_image/{dir}/{num}.webp?v={version}` |
| **当前状态** | 复原版 vaas-backend 未提供此静态资源服务，Nginx proxy 也未配 |
| **后果** | 大屏路网图层（干湿/温度/摩擦系数）全部空白 |
| **处理** | 将线上路网图图片目录映射到 Nginx 静态资源路径，或配置代理规则 |

---

## 5. 中风险影响

### 🟡 M1 — 算法参数与实际路况偏差

| 项目 | 内容 |
|------|------|
| **涉及** | detector4kt / detector4motion |
| **当前值** | 颠簸/湿滑/Motion 检测阈值基于**无锡**路况调试 |
| **后果** | 若部署在不同城市（路面材质/车速特征不同），可能误报或漏报 |
| **处理** | 上线后观察 24–48 小时事件数据，必要时调整阈值 |

### 🟡 M2 — receiver GPS 区域边界硬编码

| 项目 | 内容 |
|------|------|
| **当前值** | 经度 120.314–120.597，纬度 31.442–31.739（无锡范围） |
| **后果** | 线上若在其他城市，该区域外的 GPS 坐标被丢弃，车辆位置数据丢失 |
| **处理** | 修改为线上实际城市范围，或放宽边界 |

### 🟡 M3 — 前端 `isTrue` 字段永远为 false

| 项目 | 内容 |
|------|------|
| **涉及** | 前端 MapView.vue |
| **问题** | 后端 `OnlineVehicle` 无 `isTrue` 字段，所有车辆 `v.isTrue` 为 falsy |
| **后果** | 所有车辆 marker 都使用 `car.png` 而非 `car_true.png`，无视觉区分 |
| **处理** | 低优先级的视觉差异，不影响功能。可后续优化 |

### 🟡 M4 — 高德地图 Key/Secret

| 项目 | 内容 |
|------|------|
| **当前值** | `VITE_MAP_KEY=ba8f650d9f48ac56556e2858bc1499ad` |
| **风险** | 线上若用不同高德开发者账户、或此 Key 设了域名白名单、或额度不够 |
| **处理** | 确认线上使用的 Key；如构建前端需用正确的 Key |

### 🟡 M5 — 降雨数据降级位置硬编码

| 项目 | 内容 |
|------|------|
| **当前值** | 5 个硬编码气象站位置（如 `文惠路与锦绣路 [120.35,31.55]`） |
| **后果** | 非无锡部署时，降级显示的降雨点位置完全不对 |
| **处理** | 确认线上 `getRainPoints` 是否正常返回数据（降级只是兜底） |

### 🟡 M6 — ice / low-attachment 事件永远为空

| 项目 | 内容 |
|------|------|
| **服务** | vaas-backend |
| **当前状态** | `ice` 和 `low-attachment` 两个查询硬编码返回空 `new ArrayList<>()` |
| **后果** | 线上若这两个事件类型有数据，替换后消失 |
| **处理** | 检查线上数据库是否有这类事件记录，如有需排查原始代码逻辑 |

---

## 6. 低风险影响

### 🔵 L1 — actuator 端点不全

当前只暴露了 `health` 和 `info`，未暴露 `metrics` / `prometheus`。线上若已有监控体系依赖这些端点需补充。

### 🔵 L2 — 日志路径和格式差异

日志写到 `logs/{service}.log`，纯文本格式。线上若使用 ELK/Loki 并期望 JSON 格式或不同路径需调整。

### 🔵 L3 — 管理后台路由接口返回空

```java
@GetMapping("/get-routes") return new ArrayList<>();
```
线上管理后台若有动态菜单/路由数据，替换后侧边栏菜单可能不显示。

### 🔵 L4 — 启动顺序强依赖

必须按 `MySQL→Redis→receiver→detector4kt→detector4motion→vaas-backend→admin-api` 顺序启动。容器化部署时需解决依赖等待问题。

### 🔵 L5 — 健康检查路径确认

- vaas-backend: 专用检查 `/spring/v1/get_weather`
- 其他服务: 需用 Spring Boot Actuator `/actuator/health`

如果线上负载均衡器配置了不同的健康检查路径，需要同步修改。

### 🔵 L6 — 事件类型名称确认

前端硬编码的事件类型字符串：`bump` / `slip` / `ponding` / `ice` / `low-attachment`。后端反编译确认一致，但替换后建议手动抽查一条事件记录验证。

### 🔵 L7 — actuator info 端点无实际信息

`info` 端点默认无自定义信息，线上若依赖 `info` 返回版本/构建信息需补充配置。

---

## 7. 已验证无影响

以下部分已通过反编译 + 字节码对比 + 单元测试确认完全一致，**无需处理**：

| 编号 | 项目 | 验证方式 |
|------|------|---------|
| S1 | REST API 路径/方法/参数签名/返回结构 | 反编译自同一 JAR |
| S2 | 数据库表结构（6 张表字段/索引/DDL） | 从原始 JAR 提取 DDL |
| S3 | Redis Key 命名（`vaas:` 前缀体系） | 字节码反编译确认 |
| S4 | 算法阈值（BumpyProcessor/SlipperyProcessor/BumpyProcessor4Motion） | P5 阶段字节码完全一致 ✅ |
| S5 | SSE 推送格式/事件类型 | 反编译确认与线上一致 |

---

## 8. 替换检查清单

### 前置确认（在线下操作前必须回答）

- [ ] 线上 vaas-backend 是否开启了 TrueLicense 校验？证书文件在哪？
- [ ] 线上 MySQL 连接串、用户名、密码是什么？
- [ ] 线上 Redis 地址、端口、密码、db index 是什么？
- [ ] 线上大屏前端通过什么方式访问后端（直连 / Nginx 反代）？`VITE_API_BASE` 是什么？
  - ✅ **已确认**：原始大屏使用相对路径 `/spring/v1/`，通过 Nginx 反代访问后端
- [ ] 线上 OBU（车载设备）连接 receiver 的 WebSocket 地址/端口/路径是什么？
- [ ] 线上 OBU 连接时是否需要 token 或其他鉴权？
- [ ] 线上大屏的路网图图片（`/road_network_image/`）存放在什么位置？
- [ ] 线上气象站的传感器 ID 是什么？（与配置中的 5 组是否一致）
- [ ] 线上管理员后台的认证方式是什么？（密码文件 / LDAP / OAuth）
- [ ] 线上系统部署在哪个城市？GPS 区域边界是否匹配？

### 配置修改

- [ ] 修改 5 个服务的 `application.yml` 中的 MySQL 连接
- [ ] 修改 5 个服务的 `application.yml` 中的 Redis 连接
- [ ] 确认 vaas-backend 中传感器 ID 与线上一致
- [ ] 确认 receiver 中 GPS 区域边界与线上一致
- [ ] 确认 `config/password.txt` 存在且密码正确
- [ ] 关闭生产环境的 Swagger（`springdoc.api-docs.enabled=false`）
- [ ] 收紧 CORS（`@CrossOrigin(origins = "线上域名")` 或 Nginx 层管控）
- [ ] 添加 JVM 参数（`-Xmx1024m -Xms512m -XX:+UseG1GC`）
- [ ] 确认 `start.sh` 中健康检查路径与线上负载均衡配置一致

### License 处理

- [ ] 从线上获取 `license.lic` 证书文件
- [ ] 从线上获取 `publicCerts.store` / `privateKeys.store` 密钥库文件
- [ ] 确认 `STORE_PASSWORD` / `KEY_PASSWORD` 与线上一致
- [ ] 修改 `LicenseVerifier.java` 中的文件路径配置

---

## 9. 灰度替换步骤

```
Day 1: 准备
├── 备份线上全部 JAR + 配置文件
├── 按上述清单修改配置
└── 在预发布环境全量跑通

Day 2: 灰度 — 替换 vaas-backend + admin-api（不动数据接入层）
├── 停线上 vaas-backend → 启动你的 vaas-backend
├── 停线上 admin-api → 启动你的 admin-api
├── 验证：大屏 API 正常返回
├── 验证：管理后台能登录
└── 观察 2-4 小时无异常

Day 3: 全量 — 替换数据接入层
├── 停线上 receiver → 启动你的 receiver
├── 停线上 detector4kt → 启动你的 detector4kt
├── 停线上 detector4motion → 启动你的 detector4motion
├── 验证：OBU 数据正常入库
├── 验证：事件检测正确
├── 验证：SSE 实时推送正常
└── 观察 4-8 小时无异常

Day 4: 验收
├── 全链路数据流确认
├── 比对替换前后 24h 事件数量/类型分布（如有数据）
├── 检查日志无异常错误
└── 监控内存/CPU 使用基线
```

---

## 10. 后端服务全景

### 服务列表

| 服务 | 端口 | 上下文路径 | 职责 | 启动依赖 |
|------|:----:|:----------:|------|---------|
| receiver | 50412 | 无 | WebSocket 数据接入网关 | MySQL → Redis |
| detector4kt | 50413 | 无 | KT710 事件检测（颠簸/湿滑） | receiver |
| detector4motion | 50414 | 无 | 六轴运动颠簸检测 | receiver |
| vaas-backend | 50410 | `/spring/v1` | 核心业务 API + SSE 推送 | detector4kt/motion |
| admin-api | 50415 | `/admin` `/user` | 管理后台 REST API | vaas-backend |

### 数据流

```
车端OBU ──WebSocket──▶ receiver ──Redis队列──▶ detector4kt (KT710分析)
                                                 └── detector4motion (六轴分析)
                                                       │
                                                       ▼ Redis PubSub
                                                  vaas-backend ──SSE──▶ 大屏
                                                       │
                                                       ▼ MySQL
                                                  admin-api ◀──HTTP── 管理后台
```

### 基础设施

| 中间件 | 端口 | 用途 |
|--------|:----:|------|
| MySQL | 3306 | 数据库 `vaas`，6 张表 |
| Redis | 6379 | db 0，队列(ZSet) + 缓存 + PubSub |

---

> **更新记录**：本文档由 vaas-reproduction 项目基于代码审计和反编译分析生成，覆盖所有已知影响面。如有新的发现请补充更新。
