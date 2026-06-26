# VaaS 项目上下文摘要

> 启动时先读此摘要。完整参考见 `PROJECT_RULES.md` | `TASK_TRACKING.md`

## 项目定位
城市级道路状态感知与预警系统。车端传感器 + 路侧气象站 → 云端融合分析 → PC5 终端预警。
落地无锡（150 辆出租车）。复现目标：从编译后代码还原可编译运行的源码。

## 当前状态（仅剩 P6 待启动）

| Phase | 进度 | 说明 |
|-------|:----:|------|
| P1 素材提取 | ✅ 100% | Source Map 还原 / JAR 反编译 / Python 提取 |
| P2 后端还原 | ✅ 100% | 5 个 Spring Boot 微服务 + 算法模块 |
| P3 前端还原 | ✅ 100% | dashboard（Vue 3）+ admin（反混淆还原）|
| P4 集成验证 | ✅ 100% | 微服务联调 / 模拟器注入 / 全链路打通 |
| P5 算法验证 | ✅ 100% | 26 项单元测试 + 字节码黑盒对比 |
| **P6 上线整改** | **📋 0%** | **15 项（认证/加密/密码/容器化/配置/Nginx）** |
| P7 大屏重构 | ✅ 100% | Vue 3 + 高德地图 + 8 次迭代 |
| P8 工程优化 | ✅ 100% | 16 项（脚本/规范/OpenAPI/Actuator） |
| **合计 (114 项)** | **87%** | **完成 99 项，仅剩 P6 待启动** |

## 微服务

| 服务 | 端口 | 说明 |
|------|:----:|------|
| receiver | 50412 | WebSocket 数据接入（/ws/kt /ws/motion /ws/location）|
| vaas-backend | 50410 | 核心业务 + 算法引擎（/spring/v1）|
| detector4kt | 50413 | KT710 事件检测（颠簸/湿滑）|
| detector4motion | 50414 | 6 轴运动检测（颠簸）|
| admin-api | 50415 | 管理后台 API |
| MySQL | 3306 | 持久化 |
| Redis | 6379 | 缓存 / 队列 / PubSub |

## 核心 API（大屏用）

| 方法 | 路径 | 用途 |
|------|------|------|
| POST | `/get-alarm-list` | 告警列表 |
| POST | `/get_real_time_sensor_data` | 气象站数据 |
| POST | `/get_last24h_data_plot` | 24h 图表 |
| POST | `/get_covered_range` | 覆盖范围 |
| GET | `/get_weather` | 天气 |
| POST | `/get-event-summary` | 事件统计 |
| SSE | `/stream_data` | 实时事件推送 |

## 目录结构

```
├── CLAUDE.md               # AI 工作规则（每次启动读）
├── PROJECT_RULES.md         # 项目全景/架构/约束（按需查阅）
├── TASK_TRACKING.md         # 任务进度（按需查阅）
├── ARCHIVE.md               # 历史迭代记录（回溯用）
├── LESSONS_LEARNED.md       # 问题复盘
│
├── backend/                 # 5 个微服务 + 算法模块
├── frontend/
│   ├── dashboard/           # 大屏 (Vue 3 + Vite + 高德地图)
│   └── admin/               # 管理后台 (Vue 3 + Vite)
├── simulator/python/        # Python 算法
├── reference/               # 反编译/还原原始素材（只读）
│   ├── decompiled-jar/
│   └── recovered-src/
├── docs/                    # 文档 + 大屏基线
└── scripts/                 # 运维脚本
```

## 关键配置

- 高德地图 Key: `ba8f650d9f48ac56556e2858bc1499ad`
- API base: `http://localhost:50410/spring/v1`
- 前端地址：大屏 8082 / 管理后台 8081
- 地图中心：无锡 (120.45, 31.59), zoom=12

## 绝对红线

- ❌ 严禁 Demo 页面/假数据冒充还原产物（见 `CLAUDE.md §🚫`）
