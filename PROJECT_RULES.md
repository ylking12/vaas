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

```
Phase 1: 参考素材提取 ✅ 已完成
  ├── Source Map → 大屏前端完整源码    ← ✅ 已完成
  ├── JAR → 反编译后端源码             ← ✅ 已完成
  ├── Python 脚本直接复制              ← ✅ 已完成
  └── 文档关键信息提取                  ← [取消] 配置已全量解压
  ↓
Phase 2: 后端还原 ✅ 已完成
  ├── receiver 服务 (50412)            ← ✅ 已还原
  ├── vaas-backend 主服务 (50410)      ← ✅ 已还原
  ├── detector4kt 事件检测 (50413)     ← ✅ 已还原
  ├── detector4motion 颠簸检测 (50414) ← ✅ 已还原
  ├── admin-api 管理后台 (50415)       ← ✅ 已还原
  └── vaas-common 公共库               ← ✅ 已还原
  ↓
Phase 3: 前端还原 ✅ 已完成
  ├── dashboard 大屏 (Vue 3 + Vite)    ← ⚠️ 重写：Source Map 还原的 CSS/render
  │                                       函数无法正常集成，改用 Vue 3 + Element
  │                                       Plus + 高德地图 全新构建（详见
  │                                       LESSONS_LEARNED.md#006，后端 API
  │                                       全部复用，功能 100% 覆盖）
  └── admin 管理后台 (Vue 3 + Vite)    ← ✅ 已还原，从混淆JS反提取结构重建
  ↓
Phase 4: 集成验证 ✅ 已完成
  ├── 6 个微服务联调                   ← ✅ 全部启动运行
  ├── 模拟器数据注入测试               ← ✅ bump/slip 事件成功写入 MySQL+Redis
  ├── 前后端全链路联调                 ← ✅ 大屏/管理后台可访问，API 代理打通
  └── 功能完整性验证 & 收尾            ← ✅ 4.4a~4.4e 全部完成
  ↓
Phase 5: 算法验证 ✅ 已完成
  ├── 算法单元测试                     ← ✅ BumpyProcessor / BumpyProcessor4Motion
  │                                       / Python 交叉验证 共 26/26 通过
  └── 原始 JAR 黑盒对比测试           ← ✅ 字节码对比完全一致
  ↓
Phase 6: 上线前整改 📋 已规划（0/15）
  ├── 🔴 P0 必做（6 项）：认证鉴权 / HTTPS 加密 / 数据库密码 / 容器化
  │     / 配置管理 / Nginx 反代
  ├── 🟠 P1 强烈建议（4 项）：高可用 / OBU 协议适配 / WS 加固 / 外部依赖
  └── 🟡 P2 建议考虑（5 项）：监控告警 / 数据生命周期 / 日志 / 文档 / 合规
  ↓
Phase 7: 大屏重构 ✅ 已完成（33/33）
  ├── 改用 Vue 3 + Vite + Element Plus + 高德地图重写
  ├── 6 个子阶段：骨架 / 布局+地图 / 左侧面板 / 内容面板 / 时间轴 / 集成联调
  └── P7-iter.1 数据接入：联网车辆数 + 降水量数值
  ↓
Phase 8: 工程优化 ✅ 已完成（16/16）
  ├── O1~O4 红伤组：start.sh JDK 自适应、status/logs/restart 新脚本
  ├── O5~O9 工程规范：.gitignore、pre-commit hook、CHANGELOG、verify.sh、README 架构图
  └── O10~O16 长线优化：OpenAPI、Actuator、LICENSE、查重提案等
```

### 3.2 源代码引用规范

反编译/还原出的代码，每文件头部须注明来源:

```java
/**
 * SOURCE: Decompiled from receiver.jar (Spring Boot)
 * ORIGINAL: com.etas.vaas.receiver.controller.EventController
 * STATUS: Restored - compile verified
 */
```

### 3.3 算法还原特别规范

算法文件头部须注明公式来源和参数依据:

```java
/**
 * ALGORITHM: 路面颠簸分析
 * SOURCE: Decompiled from vaas-backend.jar
 * DOC-REF: 详细设计说明书 §5.3.1
 * 
 * 逻辑说明:
 * 1. 提取 Z 轴加速度（垂直方向）
 * 2. 使用 5s 滑动窗口计算方差、峰度、标准差
 * 3. 结合车速标准化计算"颠簸得分"
 * 4. 映射为颠簸等级 Level 0~5
 */
```

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
  - Vue 2 + Webpack (与原始一致)
  - Element UI + ECharts
  - 禁止升级到 Vue 3 (保持兼容性)

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
  receiver:       50412
  vaas-backend:   50410 (/spring/v1 上下文)
  detector4kt:    50413
  detector4motion:50414
  admin-api:      50415

启动方式:
  Java服务: java -jar <module>/target/<module>.jar
  Python算法: cd simulator/python && python3 main_6axis.py

构建:
  mvn clean package -DskipTests (全量构建)
  mvn clean package -pl <module> -am (单个服务)
```

### 4.5 部署与访问

```
本地开发环境（当前）:
  MySQL 9.6 (brew) — root@localhost:3306/vaas (无密码)
  Redis 8.8 (brew) — localhost:6379

服务端口:
  receiver:       50412  (WebSocket 数据接收)
  vaas-backend:   50410  (核心业务+算法引擎, /spring/v1)
  detector4kt:    50413  (KT710事件检测)
  detector4motion:50414  (六轴颠簸检测)
  admin-api:      50415  (管理后台API)
  六轴算法(Python):  —   (Redis消费者)

前端地址:
  大屏可视化:  http://localhost:8083  (原始编译文件+API代理)
  管理后台:    http://localhost:8081  (Vue 3 + Element Plus, admin/123456)

启动方式:
  # 后端服务
  cd backend && java -jar <module>/target/<module>.jar

  # 前端
  cd frontend/admin && npx vite --port 8081          # 管理后台
  node /tmp/dashboard-proxy.js                        # 大屏(代理)

  # Python
  cd simulator/python && python3 main_6axis.py        # 六轴算法
  cd simulator/python && bash run_weather.sh           # 天气更新
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
