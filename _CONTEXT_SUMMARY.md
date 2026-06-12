# VaaS 项目上下文摘要（精简版）

> 启动时先读此摘要，完整版见对应源文件

## 项目定位
城市级道路状态感知与预警系统。车端传感器+路侧气象站 → 云端融合分析 → PC5终端预警。
落地无锡（150辆出租车）。复现目标：从编译后代码还原可编译运行的源码。

## 当前状态（Phase 7 执行中）
| Phase | 进度 | 说明 |
|-------|------|------|
| P1~P3 | ✅ 完成 | 素材提取、后端还原、前端还原 |
| P4 | 🟡 75% | 集成验证（4.4a已完，4.4b~4.4e待做） |
| P5 | 📋 待启动 | 算法验证 |
| P6 | 📋 待启动 | 上线前整改 |
| **P7** | **🔄 执行中** | **大屏重构（Vue 3 + 高德地图，32个任务，当前 P1 骨架完成）** |

## 微服务
| 服务 | 端口 | 说明 |
|------|------|------|
| receiver | 50412 | WebSocket 数据接入（/ws/kt /ws/motion /ws/location）|
| vaas-backend | 50410 | 核心业务+算法（/spring/v1）|
| detector4kt | - | KT710事件检测（颠簸/湿滑）|
| detector4motion | - | 6轴运动检测（颠簸）|
| admin-api | 50415 | 管理后台API |
| MySQL | 3306 | 持久化 |
| Redis | 6379 | 缓存/队列/PubSub |

## 核心 API（大屏用）
- POST `/get-alarm-list` {hour} → 告警列表
- POST `/get_real_time_sensor_data` {road_name} → 气象站数据
- POST `/get_last24h_data_plot` {road_name, data_title} → 24h图表
- POST `/get_covered_range` → [面积, 道路长度, 里程]
- GET `/get_weather` → 天气
- POST `/get-event-summary` → 事件统计
- SSE `/stream_data` → 实时事件（bump/slip/ponding/ice/low_attachment）

## 数据模型
- 车辆事件：eventType/deviceId/plateNumber/eventTimestamp/longitude/latitude/roadName/level
- 气象站：airTemperature/roadSurfaceTemperature/relativeHumidity/waterLayerThickness
- 天气：temp/humidity/windSpeed/text/icon

## 目录结构
```
├── backend/          # 5个微服务 + vaas-common
├── frontend/
│   ├── dashboard/    # Vue 3 + Vite（新重构）
│   └── admin/        # Vue 3 + Vite（管理后台）
├── reference/        # 反编译/还原的原始素材
│   ├── decompiled-jar/
│   └── recovered-src/
├── simulator/python/ # Python 算法
└── docs/plans/       # 设计文档和计划
```

## 关键配置
- 高德地图 Key: ba8f650d9f48ac56556e2858bc1499ad
- 安全密钥: 8e1fb2869d1e0e5307d7694bb588a671
- API base: http://localhost:50410/spring/v1
- 地图中心: 无锡 (120.45, 31.59), zoom=12

## 绝对红线
- 严禁写 Demo 页面/假数据冒充还原产物（见 hard-boundary-no-fake-code.md）

> 完整版: PROJECT_RULES.md | TASK_TRACKING.md | LESSONS_LEARNED.md
