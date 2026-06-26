# VaaS 项目复现规则

> **项目**：城市级道路状态感知与预警系统（Vehicle as a Sensor, VaaS）
> **原始开发方**：博世智能网联科技有限公司（Bosch）
> **承建方**：江苏天安智联科技股份有限公司
> **监理方**：中邮通建设咨询有限公司
> **落地城市**：江苏省无锡市（一期150辆出租车）
> **系统名称**：车路云一体化道路状态预警平台
> **复现目标**：从编译后代码反编译/还原完整可编译运行的源代码

---

## 〇、项目全景解读

### 0.1 项目概述

这是一个**车路云一体化**项目，通过车载传感器+路侧气象站的融合数据，实时分析路面状态（结冰、积水、湿滑、颠簸），并通过PC5终端向驾驶员预警。

系统通过在150辆出租车上安装车载终端（GPS + KT710 CAN数据采集器），结合路侧气象站数据，在云端进行实时融合分析，识别路面颠簸、湿滑、积水、结冰等危险状态，并通过PC5终端向驾驶员预警。

### 0.2 系统架构

整个后端由 **6 个独立微服务** 组成，通过 Redis + MySQL 协同工作：

```
┌──────────────────────────────────────────────────────────────────┐
│                        前端展示层                                 │
│  ┌─────────────────────┐  ┌──────────────────────────┐          │
│  │  大屏可视化(dashboard)│  │  管理后台(admin)          │          │
│  │  Vue 2 + ECharts    │  │  Vue 3 + Element Plus    │          │
│  │  port 80 (Nginx)    │  │  port 80 (Nginx)         │          │
│  └─────────┬───────────┘  └───────────┬──────────────┘          │
└────────────┼──────────────────────────┼──────────────────────────┘
             │ HTTP/SSE                 │ HTTP
┌────────────┼──────────────────────────┼──────────────────────────┐
│            ▼                          ▼                           │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Spring Boot 微服务层                                    │   │
│  │                                                          │   │
│  │  ┌────────────────┐  ┌──────────────────┐               │   │
│  │  │ receiver       │  │ vaas_backend     │               │   │
│  │  │ port 50412     │  │ port 50410       │               │   │
│  │  │ WebSocket接收  │  │ /spring/v1       │               │   │
│  │  │ GPS/KT710 数据  │  │ 主业务+算法引擎   │               │   │
│  │  └───────┬────────┘  └───────┬──────────┘               │   │
│  │          │                   │                           │   │
│  │  ┌───────┴────────┐  ┌──────┴───────────┐               │   │
│  │  │ detector4kt    │  │ detector4motion  │               │   │
│  │  │ KT710事件检测   │  │ 6轴运动数据检测   │               │   │
│  │  │ 颠簸/湿滑判定   │  │ 颠簸等级判定     │               │   │
│  │  └───────┬────────┘  └───────┬──────────┘               │   │
│  │          │                   │                           │   │
│  │  ┌───────┴──────────────────┴──────────┐                │   │
│  │  │  admin (管理后台API)                  │                │   │
│  │  │  车辆绑定/心跳/日志/配置/设备/权限     │                │   │
│  │  └────────────────────────────────────┘                │   │
│  │                                                          │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │  数据层                                                  │   │
│  │  ┌──────────────┐  ┌──────────────────┐                 │   │
│  │  │  Redis       │  │  MySQL (vaas)    │                 │   │
│  │  │  缓存/队列    │  │  事件持久化       │                 │   │
│  │  │  PubSub通知   │  │  历史数据         │                 │   │
│  │  └──────────────┘  └──────────────────┘                 │   │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
│   ┌─────────────────────────┐  ┌────────────────────────┐       │
│   │ 车端子系统              │  │ 路侧气象站子系统        │       │
│   │ ┌──────────────┐       │  │ ┌──────────────────┐   │       │
│   │ │ GPS + KT710  │       │  │ │ SDK2 (port 2555) │   │       │
│   │ │ CAN总线数据   │       │  │ │ SDK3 (port 2345) │   │       │
│   │ │ WebSocket上传 │       │  │ │ 温湿度/风速/      │   │       │
│   │ │ (WSS mTLS)   │       │  │ │ 路面温度/积水     │   │       │
│   │ └──────────────┘       │  │ └──────────────────┘   │       │
│   └─────────────────────────┘  └────────────────────────┘       │
└──────────────────────────────────────────────────────────────────┘
```

### 0.3 核心算法模块

系统中有**两套独立的颠簸检测算法**（Java + Python），以及其他基于规则的事件判定：

| 算法名称 | 实现语言 | 部署位置 | 输入数据 | 输出 |
|---------|---------|---------|---------|------|
| 六轴传感器颠簸检测 | Python | vaas_6_axis_consumer | 三轴加速度(ax/ay/az)、角速度(wx) | bumpy_event（基于峰谷值分析） |
| KT710颠簸检测 | Java | detector4kt | 方向盘转角、制动压力、轮速、车速 | 颠簸事件（基于阈值判定） |
| 运动数据颠簸检测 | Java | detector4motion | 6轴运动数据 | 颠簸等级 Level 3/5/7 |
| 路面干湿状态识别 | Java | vaas_backend | wetFlag、湿度、降雨量 | 干燥/潮湿/积水/结冰 |
| 路面结冰判定 | Java | vaas_backend | roadConditions=7 | ice_event |
| 路面湿滑判定 | Java | vaas_backend | roadConditions=3 | wet_event |
| 附着系数计算 | Java | vaas_backend | KT710摩擦系数、车速 | μ值（低附着事件 < 0.62） |
| 积水判定 | Java | vaas_backend | pondingDepth > 1mm | water_event |
| 气象事件识别 | Java | vaas_backend | 风速/降雨量/温度 | 大风/暴雨/高温预警 |

#### 六轴传感器颠簸算法详情 (Python)

- **类**: `BumpyIdentificationBasedSensor` (wit_bumpy_algorithm.py)
- **算法**: 基于 scipy.signal 的峰谷值差分分析
- **滑动窗口**: 7个采样点
- **判定条件**: ay峰谷差 > 0.2 且 az峰谷差 > 0.25 且 (ay峰谷数+az峰谷数) >= 3
- **去零速逻辑**: 角速度(wx)=0 连续超过3次则清空缓存

#### detector4motion 颠簸等级参数

| 等级 | yAmplitude | zAmplitude |
|------|-----------|-----------|
| Level 3 | 0.14 | 0.175 |
| Level 5 | 0.16 | 0.20 |
| Level 7 | 0.20 | 0.25 |

#### detector4kt 算法参数

| 参数 | 值 | 说明 |
|------|-----|------|
| bump.steer-ratio-diff-lv1 | 0.1 | 方向盘转角比差分LV1 |
| bump.steer-ratio-diff-lv2 | 0.05 | 方向盘转角比差分LV2 |
| bump.mean-break-pressure-threshold | 1 | 平均制动压力阈值 |
| bump.sum-speed-ratio-threshold | 3 | 轮速比阈值 |
| bump.correlation-threshold | 0.8 | 相关性阈值 |
| bump.mean-speed-threshold | 9.7 | 平均车速阈值 |
| slip.speed-threshold | 1.389 m/s | 湿滑速度阈值 |
| slip.mu-threshold | 0.51 | 湿滑摩擦系数阈值 |

### 0.4 数据流

1. **车端** → GPS定位 + KT710传感器数据 → WebSocket → 后端
2. **气象站** → TCP Socket → 后端SDK接入 → 统一格式转换
3. **后端** → Redis缓存 → 算法引擎判定事件 → MySQL持久化
4. **事件推送** → SSE (Server-Sent Events) → 前端实时更新 / 外部平台
5. **预警推送** → WebSocket/PC5 → 车载终端声光预警

### 0.5 项目材料文档清单

| 文档 | 主要内容 |
|------|---------|
| 01-项目实施方案与计划 | 项目组织、里程碑、实施计划 |
| 02-需求规格说明书 | 功能需求、性能指标、接口需求 |
| 03-概要设计说明书 | 总体架构、子系统划分 |
| 04-详细设计说明书 | 各模块详细设计、算法逻辑、数据库 |
| 05-数据库设计说明书 | event表、气象数据表、Redis数据结构 |
| 06-用户手册 | 操作指南 |
| 07-维护手册 | 运维管理 |
| 11-系统框架图 | 系统架构图 |
| 12-接口设计说明 | 外部API、数据格式 |
| 00-软件功能清单 | 完整的功能模块与工作量 |

### 0.6 关键API接口

| 接口 | 方法 | 用途 |
|------|------|------|
| `/spring/v1/stream_data` | SSE | 流式数据推送 |
| `/spring/v1/post_simulated_event` | POST | 模拟事件注入 |
| `/spring/v1/post_simulated_event_to_database` | POST | 模拟事件写库 |
| `/spring/v1/get_weather` | GET | 获取天气数据 |
| `/spring/v1/external/getEventSummary/{startTime}/{endTime}` | GET | 外部获取事件列表 |
| `/ws/kt` | WebSocket | KT710数据接收 |
| `/ws/motion` | WebSocket | 运动数据接收 |
| `/ws/location` | WebSocket | 定位数据接收 |
| `/admin/login` | POST | 管理后台登录 |
| `/admin/list` | POST | 获取车辆绑定列表 |
| `/admin/heartbeat` | GET | 获取设备心跳 |
| `/admin/db-name` | GET | 获取数据库名 |
| `/user/login` | POST | 用户登录(SaToken) |

### 0.7 管理后台页面结构（admin/）

| 路由 | 页面 | 后端API | 说明 |
|------|------|---------|------|
| `/welcome` | 首页 | - | 系统概览（统计卡片） |
| `/vaas/car-mapping` | 车辆绑定管理 | `POST /admin/list` `POST /admin/add` `PUT /admin/update` `DELETE /admin/delete` | 车辆与设备绑定CRUD |
| `/vaas/heartbeat` | 心跳管理 | `GET /admin/heartbeat` | 设备在线状态监控 |
| `/vaas/log` | 动态日志 | `GET /admin/log` | Websocket实时推送日志 |
| `/vaas/system-config` | 系统配置 | - | 平台配置参数 |
| `/vaas/device` | 设备管理 | - | 设备在线情况 |
| `/vaas/role` | 角色/权限管理 | - | 多租户管理 |
| `/vaas/report` | 数据报表 | `GET /admin/db-name` | 项目数据统计 |
| `/error/403` | 403页面 | - | 无权限 |
| `/error/404` | 404页面 | - | 页面不存在 |
| `/error/500` | 500页面 | - | 服务器错误 |

---

## 一、核心原则

### 1.1 复现策略

| 模块 | 策略 | 优先级 | 说明 |
|------|------|--------|------|
| 大屏前端 (www/) | Source Map 还原 | P0 | 已验证可还原原始源码，JS逻辑文件100%还原 |
| 管理后台 (admin/) | 反混淆 + 结构提取 + 重写 | P1 | 无Source Map，需提取路由/API/组件结构后参考重写 |
| 后端 Receiver | JAR 反编译 (CFR) | P0 | Spring Boot jar，反编译成熟度高 |
| 后端 VaaS-Backend | JAR 反编译 (CFR) | P0 | 主业务服务，含核心算法 |
| 算法模块 | 从后端提取独立 | P0 | 颠簸/干湿/附着系数/气象事件，独立为算法工程 |
| Python 模拟器 | 直接使用源码 | P0 | 已有完整源码 |
| 轨迹模拟器 | JAR 反编译 | P1 | Java轨迹数据模拟 |

### 1.2 技术栈锁定

```
后端运行时:  JDK 17 (修正：字节码确认 major version 61 = Java 17，非 JDK 8)
后端框架:   Spring Boot 3.5.3 + Maven (修正：import jakarta.* 非 javax.*)
后端模式:   receiver 使用 WebFlux（响应式）；其余服务使用 Spring MVC
数据库:     MySQL 9.6 (开发环境) / MySQL 5.7+ (生产) / Redis 8.8 (开发) / Redis 6.x (生产)
权限框架:   Sa-Token (cn.dev33.satoken)，管理后台认证
前端大屏:   Vue 3 + Vite + Element Plus + ECharts + 高德地图 JS API
前端管理台:  Vue 3 + Vite 5.9.0 + Element Plus
地图:       高德地图 / 百度地图 API
气象站SDK:  NetDeviceSDKP3 (厂商提供，不还原，已创建桩接口)
OCR识别:    阿里云 OCR (com.aliyun.ocr_api20210707，已创建桩接口)
许可证:     TrueLicense (de.schlichtherle.truelicense)
H3地理索引: com.uber.h3 (4.1.1)
EasyExcel:  com.alibaba.easyexcel (报表导出)
```

### 1.3 还原质量红线

- **后端**: 反编译代码必须能通过编译，功能逻辑与原始 JAR 一致
- **算法**: 阈值参数、计算公式必须与详细设计文档核对一致
- **前端大屏**: 还原后功能完整，JS 逻辑与原始一致，UI 像素级匹配
- **前端管理台**: 功能完整、路由结构一致、API 调用一致
- **所有还原代码**不能混入第三方闭源代码

---

## 二、目录结构规范

```
vaas-reproduction/
├── PROJECT_RULES.md              # 本文件 - 项目规则与全景解读
├── CLAUDE.md                     # AI 辅助开发规则 (每次启动读取)
│
├── frontend/
│   ├── dashboard/                # 大屏可视化 (Vue 3 + Vite)
│   │   └── src/
│   │       ├── views/            # 页面组件 (DashboardPage.vue)
│   │       ├── components/       # 组件 (MapView/SensorChart/...)
│   │       ├── router/           # 路由
│   │       ├── stores/           # Pinia 状态管理
│   │       ├── api/              # Axios API 封装
│   │       └── assets/           # 静态资源
│   │
│   └── admin/                    # 管理后台 (Vue 3 + Vite)
│       └── src/
│           ├── views/            # 页面组件
│           ├── components/       # 通用组件
│           ├── router/           # 路由
│           ├── store/            # 状态管理
│           ├── utils/            # 工具函数
│           └── assets/           # 静态资源
│
├── backend/
│   ├── receiver/                 # 数据接收服务 (Spring Boot)
│   │   └── src/main/java/com/etas/vaas/receiver/
│   │       ├── controller/       # REST & SSE 接口
│   │       ├── service/          # 业务逻辑
│   │       ├── websocket/        # WebSocket 处理
│   │       ├── config/           # 配置类
│   │       ├── model/            # 数据模型
│   │       └── repository/       # 数据访问
│   │
│   ├── vaas-backend/             # 主后端服务 (Spring Boot)
│   │   └── src/main/java/com/bosch/cs/rcs/
│   │       ├── controller/       # REST 接口
│   │       ├── service/          # 业务逻辑
│   │       ├── algorithm/        # 算法引擎
│   │       ├── websocket/        # WebSocket 处理
│   │       ├── config/           # 配置类
│   │       ├── model/            # 数据模型
│   │       └── repository/       # 数据访问
│   │
│   └── algorithm/                # 独立算法模块
│       └── src/main/java/com/etas/vaas/algorithm/
│           ├── bump/             # 路面颠簸检测
│           ├── slip/             # 路面湿滑检测
│           ├── ice/              # 路面结冰检测
│           ├── ponding/          # 路面积水检测
│           ├── friction/         # 附着系数计算
│           └── weather/          # 气象事件识别
│
├── simulator/
│   ├── python/                   # Python 事件模拟器
│   └── jmeter/                   # JMeter 压力测试
│
├── reference/                    # 还原参考素材 (只读，不修改)
│   ├── recovered-src/            # Source Map 还原出的源码
│   ├── decompiled-jar/           # JAR 反编译出的 Java 源码
│   └── docs-extract/             # 文档关键提取物
│
└── docs/                         # 项目文档
```

---

## 三、还原工作流

### 3.1 工作顺序

Phase 进度详见 [TASK_TRACKING.md](./TASK_TRACKING.md)。概要：

| Phase | 状态 | 说明 |
|-------|------|------|
| P1 素材提取 | ✅ 完成 | Source Map 还原 / JAR 反编译 / Python 提取 |
| P2 后端还原 | ✅ 完成 | 5 个 Spring Boot 微服务 + vaas-common + 算法模块 |
| P3 前端还原 | ✅ 完成 | dashboard（Vue 3 重写）+ admin（反混淆还原）|
| P4 集成验证 | ✅ 完成 | 微服务联调 / 模拟器注入 / 全链路打通 |
| P5 算法验证 | ✅ 完成 | 26 项单元测试 + 字节码黑盒对比 |
| P6 上线整改 | 📋 待启动 | 15 项（认证/加密/密码/容器化/配置/Nginx 等）|
| P7 大屏重构 | ✅ 完成 | Vue 3 + 高德地图替换 + 8 次迭代 |
| P8 工程优化 | ✅ 完成 | 16 项（脚本/规范/OpenAPI/Actuator）|

> 文件头标注规范已移至 [CLAUDE.md](./CLAUDE.md#%EF%B8%8F-%E6%96%87%E4%BB%B6%E5%A4%B4%E6%A0%87%E6%B3%A8) §🏷️

---

## 四、代码质量约束

### 4.1 通用约束

- **禁止**: 修改原始业务逻辑、算法参数、API 签名
- **允许**: 修正明显的 bug（如 NPE、资源未关闭）、补充缺失的泛型、优化 import
- **允许**: 补充单元测试（不存在的则新建）
- **允许**: 为反编译代码补充类型注解 @Override、@Deprecated 等
- **必须**: 还原后的代码包名、类名与原始一致

### 4.2 后端约束

```
Java 版本: 17 (source/target) ✅ 已验证：所有 JAR major version 61
构建工具: Maven (pom.xml 从原始 POM 反编译获取)
禁止使用: Java 21+ 特性
Spring Boot 版本: 3.5.3 (与原始一致)
数据库: 仅使用 MySQL + Redis，不引入新中间件
Redis模式: receiver/vaas-backend/detector4kt/admin-api 使用单机；detector4motion 原为Cluster
```

### 4.3 前端约束

```
大屏 (dashboard):
  - Vue 3 + Vite（P7 重构，Source Map 还原的 Vue 2 render/CSS 无法集成）
  - Element Plus + ECharts + 高德地图
  - 后端 API 100% 复用原始接口

管理后台 (admin):
  - Vue 3 + Vite (重写时选型)
  - Element Plus
  - 路由/API/功能与原始一致
```

### 4.4 基础设施与部署

```
开发环境:
  MySQL 9.6 (brew) — root@localhost:3306/vaas (无密码)
  Redis 8.8 (brew) — localhost:6379

服务端口:
  receiver:       50412  WebSocket 数据接入
  vaas-backend:   50410  核心业务 /spring/v1
  detector4kt:    50413  KT710 事件检测
  detector4motion:50414  六轴颠簸检测
  admin-api:      50415  管理后台 API

前端地址:
  大屏: http://localhost:8082 (Vue 3 + Vite)
  管理后台: http://localhost:8081 (Vue 3 + Element Plus, admin/123456)

后端启动: cd backend && java -jar <module>/target/<module>.jar
前端启动: npx vite --port 8081 (admin) / npx vite --port 8082 (dashboard)
Python:   cd simulator/python && python3 main_6axis.py

构建:
  mvn clean package -DskipTests (全量构建)
  mvn clean package -pl <module> -am (单个服务)
```

### 4.6 已发现问题记录

详见 LESSONS_LEARNED.md，当前重要遗留问题：

| 问题 | 影响 | 状态 |
|------|------|------|
| 气象站 SDK 缺失 | 路侧气象站数据无法接入 | ⏳ 需联系厂商 |
| 阿里云 OCR 未配置 | 车牌拍照识别不可用 | ⏳ 需购买服务 |
| 大屏部分 API 400 | get-alarm-list 等接口参数不匹配 | ⏳ 待修复 |
| Redis 单机 vs Cluster | 仅影响 detector4motion 原始配置 | ✅ 已适配单机 |

### 4.7 配置管理

所有环境敏感配置（数据库连接、Redis、API Key 等）使用：
1. Spring 的 `application-{profile}.yml` + 环境变量
2. 提供 `application-dev.yml` 开发环境默认值
3. 不提交真实密码/密钥到代码库

### 4.8 Phase 完成审计清单

> 每个 Phase 标记为 100% 完成前，必须逐项完成以下审计。任何一项"否"需先修复再标完成。

```
Phase {N} 完成审计
├── [ ] PROJECT_RULES.md 准确性
│     ├── §〇.2 系统架构：服务列表、端口、依赖关系与代码一致？
│     ├── §〇.5 文档清单：目录结构与实际文件一致？
│     ├── §〇.6 API 接口：Controller 改动已反映？
│     └── §〇.7 管理后台页面：前端结构变化已反映？
├── [ ] README.md 准确性
│     ├── 服务端口表与 pom.xml 一致？
│     ├── 脚本工具章节覆盖所有 scripts/ 下的脚本？
│     └── 系统架构图 Mermaid 与实际数据流一致？
├── [ ] CLAUDE.md 规则合理性 — 强制操作步骤、复现原则是否仍适用？
├── [ ] _CONTEXT_SUMMARY.md 同步更新 — Phase 进度、目录结构、配置与当前一致？
├── [ ] CHANGELOG.md 已更新 — 本 Phase 关键改动已记录
├── [ ] pre-commit hook 规则覆盖 — 本 Phase 涉及改动类型有对应规则？
└── [ ] 文档 vs 代码一致性（自动检查项）
      ├── backend/*Controller.java 改动 → PROJECT_RULES §〇.6
      ├── backend/*/pom.xml 改动 → 端口表（PROJECT_RULES + README）
      ├── backend/*/application.yml 改动 → 端口表
      ├── frontend/dashboard/src/ 目录变化 → PROJECT_RULES §〇.5
      └── backend/{新服务名}/ 增删 → 架构图 + 端口表
```

### 4.9 pre-commit hook 规则

| # | 触发 | 提醒文档 |
|---|------|---------|
| 1 | frontend/dashboard/src/ 或 backend/*/src/ 改动 | TASK_TRACKING.md 进度概要 |
| 2 | scripts/*.sh 改动 | README.md 脚本工具 |
| 3 | docs/plans/ 新增 | TASK_TRACKING.md 引用 |
| 4 | PROJECT_RULES.md / CLAUDE.md 改动 | 团队同步 |
| 5 | backend/**/controller/ 改动 | PROJECT_RULES.md §〇.6 API |
| 6 | backend/*/pom.xml 改动 | PROJECT_RULES.md + README.md 端口表 |
| 7 | frontend/dashboard/src/ 目录结构变化 | PROJECT_RULES.md §〇.5 + §〇.7 |
| 8 | backend/{service}/ 增删 | PROJECT_RULES.md + README.md 架构图 |
| 9 | backend/*/application.yml 改动 | PROJECT_RULES.md + README.md |

---

## 四.5、大屏复原基线（回归测试依据）

> **位置**：[docs/dashboard-baseline/](docs/dashboard-baseline/)
> **首版时间**：2026-06-18（11 项核心功能对齐 + 1 项 P0 修复）
> **目的**：作为后续大屏改动的回归测试基线，避免功能回退

### 4.5.1 何时重跑基线

| 触发场景 | 重跑命令 |
|---------|---------|
| 大屏组件代码改动 | `cd docs/dashboard-baseline && NODE_PATH=/opt/homebrew/lib/node_modules node p7-baseline-capture.js` |
| 大屏样式调整 | 同上（重点对比 `screenshots-analysis.json` 主色）|
| 后端 API 路径/字段变更 | 重跑原大屏 + P7 baseline，对比 `api-fields.md` |
| 主题色/视觉规范调整 | 对比 `screenshots-summary.md` + `design-tokens.md` |
| 完整回归 | 跑原大屏 + P7 baseline + 写新 diff 报告 |

### 4.5.2 关键产物

| 文件 | 用途 |
|------|------|
| `VERIFICATION_REPORT.md` | ⭐ 验证报告（功能对齐 + P0 修复记录）|
| `full-ui-inventory.md` | 完整 UI 元素清单（面板/标题/API）|
| `api-fields.md` | 13 个 API 字段定义 |
| `design-tokens.md` | 视觉规范（颜色/字体/间距）|
| `screenshots-summary.md` | 7 张原大屏截图 + 主题分析 |
| `interaction-state-machine.md` | 交互状态机图 |
| `p7-baseline/diff-report-v2.md` | P7 vs 原大屏差异（已修复后）|
| `baseline-capture-v2.js` | 原大屏深度探测脚本（可重跑）|
| `p7-baseline/p7-baseline-capture.js` | P7 同步探测脚本（可重跑）|

### 4.5.3 核心 API 清单（与原大屏对齐的 13 个端点）

| API | 用途 |
|-----|------|
| `/spring/v1/get-alarm-list` | 告警列表 |
| `/spring/v1/get-event-summary` | 事件汇总 |
| `/spring/v1/get_real_time_sensor_data` | 实时传感器 |
| `/spring/v1/get_last24h_data_plot` | 24h 数据图 |
| `/spring/v1/get_covered_range` | 覆盖范围 |
| `/spring/v1/get_weather` | 天气 |
| `/spring/v1/get-rain-points` | 雨点 |
| `/spring/v1/location` | 车辆位置 |
| `/spring/v1/get-last-24h-bump-event` | 24h 颠簸 |
| `/spring/v1/get-last-24h-slip-event` | 24h 湿滑 |
| `/spring/v1/get-last-24h-ponding-event` | 24h 积水 |
| `/spring/v1/get-last-24h-ice-event` | 24h 结冰 |
| `/spring/v1/get-last-24h-low-attachment-event` | 24h 低附着 |

### 4.5.4 已修复的差异（避免回退）

| 修复点 | 文件位置 | 说明 |
|--------|---------|------|
| **5 种 24h 事件补全** | `frontend/dashboard/src/views/DashboardPage.vue` 的 `loadMapEvents()` | 补全 ponding/ice/low-attachment，原只支持 bump/slip |
| **Drawer 主题色** `#1A1A1A` → `#090909` | 同上 `.drawer-grid` / `.el-drawer` / `.el-select-dropdown` | 与原大屏 #090909 对齐 |
| **Popup 背景色** `#1A1A1A` → `#090909` | `frontend/dashboard/src/components/Popup.vue` | 弹窗背景对齐 |

### 4.5.5 5 个气象站下拉（必须保持完整）

```
[
  { key: 1, name: '文惠路与锦绣路' },
  { key: 2, name: '先锋中路与新锡路' },
  { key: 3, name: '机场路-泰山路' },
  { key: 4, name: '高浪路-兴梁道' },
  { key: 5, name: '运河西路' }
]
```

### 4.5.6 探测脚本经验（避免再次踩坑）

> ⚠️ **关键经验**：探测 P7 大屏时**不能**只跑一次默认状态，必须按以下步骤：

1. ⏰ **必须等 drawer 展开**：点击"实时数据"后等 **5 秒**（drawer 有打开动画 + ECharts 异步加载）
2. 🌲 **必须抓 drawer 内部 DOM**：用 `document.querySelector('.el-drawer').querySelectorAll('*')`，不能只靠 `document.body` 的 TreeWalker（drawer 渲染在 portal）
3. 📊 **不能凭文件大小判断页面已展开**（drawer 后页面反而变小，因为地图被遮挡）
4. 🎨 **canvas/SVG 元素单独分类**（amap / echarts / 原生 canvas 各算一类）
5. 📋 **把 drawer 元素合并到 all-texts.json**（否则 drawer 内容永远看不到）

### 4.5.7 重跑检查清单

- [ ] 启动 5 个后端服务（receiver / vaas-backend / detector4kt / detector4motion / admin-api）
- [ ] 启动 P7 Vite dev server（默认 8083）
- [ ] 确认端口 50410/50412/50413/50414/50415 + 8083 都在 LISTEN
- [ ] 跑 `p7-baseline-capture.js`（约 3 分钟）
- [ ] 对比 `p7-baseline/api-responses.json` 上一版（应 ≥ 13 个端点）
- [ ] 对比 `p7-baseline/screenshots/` 主色（应与 baseline 主色一致）
- [ ] 如有差异，写新 diff 报告到 `p7-baseline/diff-report-v3.md`

---

## 五、参考文档映射

| 设计文档 | 对应模块 | 关键作用 |
|---------|---------|---------|
| 02-需求规格说明书 | 全系统 | 功能列表、性能指标、接口定义 |
| 03-概要设计说明书 | 全系统 | 架构设计、子系统划分 |
| 04-详细设计说明书 | 全系统 | 算法逻辑、API 设计、数据库设计 |
| 05-数据库设计说明书 | 后端 | event表、气象数据表、Redis结构 |
| 12-接口设计说明 | 后端 | 外部接口规范、数据格式 |
| 00-软件功能清单 | 全系统 | 功能模块清单、工时评估 |

---

## 六、原始代码结构参考

### 6.1 前端编译后代码结构

```
原始路径: 前端代码/www/
├── index.html                    # 大屏可视化入口 (Vue 2 + Webpack)
├── car/                          # 车辆图标资源 (24个PNG)
├── css/                          # CSS样式 (含FontAwesome/QWeather)
├── js/                           # Vue 2 打包JS (+ .js.map source map)
│   ├── app.*.js                  # 主应用入口
│   ├── chunk-vendors-*.js        # 第三方依赖
│   └── *.js.map                  # Source Map 文件 (可用于还原)

原始路径: 前端代码/www/admin/
├── index.html                    # VaaS车辆后台入口
├── platform-config.json          # 平台配置 (V5.9.0, Vue 3)
├── static/js/                    # Vue 3 打包JS (无source map)
│   ├── index-DgRB5vEa.js         # 主入口 (~2.3MB)
│   ├── frame-*.js                # 框架页面
│   ├── hook-*.js                 # 钩子/工具
│   ├── DynamicLog-*.js           # 动态日志页面
│   └── ...
└── static/css/                   # 样式文件
```

### 6.2 后端代码结构

```
原始路径: 后端代码和算法/etas.tar/opt/etas/vaas/
├── receiver/                         # 数据接收服务 (Spring Boot)
│   ├── receiver.jar                  # JAR主程序 (40MB)
│   ├── config.yaml                   # Spring配置 (Redis/MySQL/WebSocket)
│   ├── start.sh / start_local.sh     # 启动脚本
│   └── logs/                         # 运行日志
│
├── vaas_backend/                     # 主后端服务 (Spring Boot) port 50410
│   ├── vaas_backend.jar              # JAR主程序
│   ├── vaas_backend.jar.bak          # 备份
│   ├── application.yml               # Spring配置 (激活prod)
│   ├── application-prod.yaml         # 生产配置 (context-path: /spring/v1)
│   ├── application-test.yml          # 测试配置
│   └── logback-spring.xml            # 日志配置 (包名: com.bosch.cs.rcs)
│
├── detector4kt/                      # KT710数据检测服务 (Spring Boot)
│   ├── detector4kt-4fd4ef6d.jar      # JAR主程序
│   ├── config.yaml                   # 颠簸/湿滑算法参数配置
│   ├── start.sh / start_local.sh     # 启动脚本
│   └── 包名: com.etas.vaas.detector  # 从日志配置推断
│
├── vaas_detector4motion/             # 运动数据检测服务 (Spring Boot)
│   ├── vaas_detector4motion.jar      # JAR主程序
│   ├── detector4motion-prod-0917.jar # 生产JAR (带日期版本)
│   ├── config.yaml                   # 6轴算法参数配置
│   ├── application.yml/prod/test     # Spring配置
│   ├── start_local.sh                # 本地启动脚本
│   └── 包名: com.etas.vaas.detector4motion
│
├── admin/                            # 管理后台API服务 (Spring Boot)
│   └── admin.jar                     # JAR主程序
│
├── vaas_6_axis_consumer-venv/        # 六轴传感器算法 (Python 3.8)
│   └── vaas_6_axis_consumer/
│       ├── main_6axis.py             # 主入口
│       ├── configuration.yml         # 配置文件
│       ├── algorithm_6axis/
│       │   ├── wit_bumpy_algorithm.py      # 颠簸识别算法 (核心)
│       │   ├── wit_data_process_service.py # 数据处理服务
│       │   ├── wit_mysql_operation.py      # MySQL操作
│       │   ├── wit_redis_operation.py      # Redis操作
│       │   └── wit_utils.py                # 工具函数
│       ├── wit/
│       │   ├── wit_decode_algorithm.py     # WIT解码算法
│       │   └── wit_decode_func.py          # WIT解码函数
│       ├── database.py               # 数据库连接
│       ├── utils.py                  # 工具函数
│       ├── test.py                   # 测试脚本
│       └── readme.md                 # 说明文档
│
├── vaas_vehicle_simulator/           # 车辆轨迹模拟器 (Spring Boot)
│   ├── vaas-trajectory-simulator-0.0.1-SNAPSHOT.jar
│   ├── 5条坐标JSON (各约116KB)
│   └── application-dev.yml           # Redis: 192.168.112.17:6379
│
├── vaas_simulator/                   # 事件模拟器 (Python + Java)
│   ├── vaas_event_simulator/         # Python模拟 (50条车辆路径)
│   └── vaas_trajectory_simulator_java/ # Java轨迹模拟
│
├── vaas_script/                      # 数据脚本工具
│   ├── data_process8.py              # MySQL→JSON 数据导出脚本
│   └── event_data.json               # 事件数据导出样本
│
├── mysql_dump/                       # (空) 数据库备份目录
│   └── vaas_database_dump.sql        # 空文件，无实际数据
│
├── vaas_python_venv/                 # Python虚拟环境 (3.8)
│   └── scripts/post.py               # HTTP数据推送脚本
│
├── sse_client.py                     # SSE客户端工具
└── vaas_jmeter/                      # JMeter压力测试
    └── test.jmx                      # 测试计划
```

### 6.3 全部微服务一览

| 服务 | JAR文件 | 端口 | 包名 | 职责 |
|------|---------|------|------|------|
| receiver | receiver.jar | 50412 | com.etas.vaas.receiver.* | WebSocket接收车辆GPS/KT710数据，写入Redis |
| vaas-backend | vaas_backend.jar | 50410 | com.etas.vaas.backend.* | 主业务+算法引擎，/spring/v1，REST/SSE接口 |
| detector4kt | detector4kt.jar | 50413 | com.etas.vaas.detector.* | 从Redis消费KT710数据，检测颠簸/湿滑事件 |
| detector4motion | vaas_detector4motion.jar | 50414 | com.etas.vaas.detector4motion | 消费6轴运动数据，判定颠簸等级 |
| admin-api | admin.jar | 50415 | com.etas.vaas.admin.* | 管理后台API（车辆绑定/心跳/日志/配置） |
| 6axis-consumer | (Python) | - | algorithm_6axis | Python颠簸算法，基于scipy信号处理 |

### 6.4 关键Java包结构

| 包名 | 所属服务 | 用途 |
|------|---------|------|
| `com.etas.vaas.receiver.*` | receiver | 数据接收服务 |
| `com.etas.vaas.common.component` | receiver/公共 | 公共组件 |
| `com.etas.vaas.receiver.controller` | receiver | REST Controller |
| `com.etas.vaas.receiver.service` | receiver | 业务服务层 |
| `com.etas.vaas.receiver.websocket` | receiver | WebSocket处理 |
| `com.bosch.cs.rcs` | vaas_backend | 主后端业务 |
| `com.etas.vaas.detector.*` | detector4kt | KT710事件检测 |
| `com.etas.vaas.detector.event` | detector4kt | 事件处理 |
| `com.etas.vaas.detector.common` | detector4kt | 公共组件 |
| `com.etas.vaas.detector.component` | detector4kt | 组件 |
| `com.etas.vaas.detector4motion.*` | detector4motion | 运动数据检测 |

---

### 6.5 传感器与气象站配置

从 vaas_backed application-prod.yaml 中提取的传感器部署信息：

| 传感器ID | 类型 | 位置 | 所属路段 |
|---------|------|------|---------|
| 15500257 | RoadCondition | 文惠路与锦绣路 | 路段1 |
| 21098126 | Station | 文惠路与锦绣路 | 路段1 |
| 21098575 | Atmospheric | 文惠路与锦绣路 | 路段1 |
| 15500542 | RoadCondition | 贡湖大道与金城路口 | 路段2 |
| 21113177 | Station | 贡湖大道与金城路口 | 路段2 |
| 21118286 | Atmospheric | 贡湖大道与金城路口 | 路段2 |
| 15500543 | RoadCondition | 机场路-泰山路 | 路段3 |
| 21113179 | Station | 机场路-泰山路 | 路段3 |
| 21118289 | Atmospheric | 机场路-泰山路 | 路段3 |
| 15500545 | RoadCondition | 高浪路-兴梁道 | 路段4 |
| 21113178 | Station | 高浪路-兴梁道 | 路段4 |
| 21118288 | Atmospheric | 高浪路-兴梁道 | 路段4 |
| 15500544 | RoadCondition | 运河西路 | 路段5 |
| 21113183 | Station | 运河西路 | 路段5 |
| 21118287 | Atmospheric | 运河西路 | 路段5 |

气象站通过 SDK2 (port 2555) 和 SDK3 (port 2345) 接入。

### 6.6 事件判定阈值（从配置反推）

| 事件类型 | 判定字段 | 条件 | 配置值 |
|---------|---------|------|-------|
| water_event (积水) | pondingDepth | GreatAndEq | 1mm |
| low_attachment_event (低附着) | waterLayerThickness | LessAndEq | 0.62 |
| ice_event (结冰) | roadConditions | Eq(=) | 7 |
| wet_event (湿滑) | roadConditions | Eq(=) | 3 |

### 6.7 Redis 配置补充

receiver 和 vaas_backend 使用单机 Redis (6379)；
vaas_detector4motion 使用 Redis Cluster (7000/7001)。

---

## 七、术语表

| 术语 | 全称 | 说明 |
|------|------|------|
| VaaS | Vehicle as a Sensor | 车辆即传感器 - 项目代号 |
| KT710 | - | CAN总线数据采集模块，获取车速/轮速/加速度/雨刮等 |
| RCPS | Road Condition Perception System | 道路状况感知系统 |
| SSE | Server-Sent Events | 服务端主动事件推送协议 |
| PC5 | - | 智能网联车短距离直连通信终端 |
| C-V2X | Cellular Vehicle-to-Everything | 蜂窝车联网通信 |
| wetFlag | - | KT710 提供的路面湿滑标志位 |
| H3 | H3 Hexagonal Hierarchical Grid | Uber 开源六边形地理空间索引 |
| RCS | Road Condition Sensing | 道路状态感知（Bosch内部项目名） |
