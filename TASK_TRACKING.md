# VaaS 项目复现 - 任务跟踪总表

> 更新时间: 2026-06-12 (v16) | 全部待处理事项已收拢到此文件

## 进度概要

| Phase | 总任务数 | 完成 | 进行中 | 待开始 | 进度 |
|-------|---------|------|--------|-------|------|
| P1 参考素材提取 | 10 | 9 | 0 | 0 | 90% | ⬅️ 1.10 已取消（剩余 10%） |
| P2 后端还原 | 20 | 20 | 0 | 0 | 100% ✅ | ⬅️ 含 4 项分支任务 |
| P3 前端还原 | 6 | 6 | 0 | 0 | 100% ✅ |
| P4 集成验证 | 4 | 4 | 0 | 0 | 100% ✅ | ⬅️ 4.4 五个子任务全完成 |
| P5 算法验证 | 2 | 2 | 0 | 0 | 100% ✅ |
| P6 上线前整改 | 15 | 0 | 0 | 15 | 0% | 📋 已规划 |
| **P7 大屏重构** | **37** | **37** | **0** | **0** | **100%** | ✅ 首版+2次迭代完成 |
| **P8 工程优化** | **16** | **16** | **0** | **0** | **100%** | ✅ 红伤组+规范组+长线优化全完成 |
| **合计** | **110** | **94** | **0** | **15** | **85%** | ⏳ 仅剩 P6 上线前整改 |

---

## Phase 1: 参考素材提取 (9/10, 1已取消)

```
Phase 1 - 参考素材提取
├── 1.1  [完成] 完整解压 etas.tar，提取所有后端文件
├── 1.1a [完成] ←[分支] 同步新发现资产到 PROJECT_RULES.md
├── 1.2  [完成] 大屏前端 Source Map 全量还原到 reference/recovered-src/
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

## Phase 2: 后端还原 (17/17) ✅

```
Phase 2 - 后端还原
  -- receiver (port 50412) --
  ├── 2.1 [完成] 反编译代码整理到 backend/receiver/ (13个业务类)
  ├── 2.1a [完成] 反编译 common-0.0.1-SNAPSHOT.jar (59个公共类)
  ├── 2.2 [完成] pom.xml 还原 (Spring Boot 3.5.3 + WebFlux)
  ├── 2.3 [完成] application.yml + config.yaml 配置重写
  └── 2.4 [完成] 编译验证 & bug修复

  -- vaas-backend (port 50410) --
  ├── 2.5 [完成] 反编译代码整理到 backend/vaas-backend/ (62个业务类)
  ├── 2.5a [完成] 反编译类型问题修复 (约65处编译错误)
  ├── 2.6 [完成] 算法模块提取到 backend/algorithm/
  ├── 2.7 [完成] pom.xml + 多环境配置还原
  └── 2.8 [完成] 编译验证 & bug修复

  -- detector4kt --
  ├── 2.9 [完成] 反编译代码整理到 backend/detector4kt/ (15个业务类)
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
Phase 3 - 前端还原
├── 3.1 [完成] dashboard - Source Map 还原源码到 frontend/dashboard/
├── 3.2 [完成] dashboard - package.json + Webpack 配置还原
├── 3.3 [完成] dashboard - npm install && 编译验证
├── 3.4 [完成] admin - 从编译JS提取路由/页面/API结构
├── 3.5 [完成] admin - Vue 3 + Vite 项目骨架搭建
└── 3.6 [完成] admin - 从反混淆JS还原3个核心页面
```

---

## Phase 4: 集成验证

```
Phase 4 - 集成验证 (4/4) ✅
├── 4.1 [完成] 后端 6 个微服务联调 — 5个SpringBoot + Python算法全部启动运行 ✅
├── 4.2 [完成] 模拟器数据注入测试 — bump/slip 事件成功写入 MySQL + Redis ✅
├── 4.3 [完成] 前后端全链路联调 — 大屏/管理后台可访问，API代理打通 ✅
└── 4.4 [完成] 功能完整性验证 & 收尾
      ├── 4.4a ✅ 修复大屏API空指针（get-alarm-list FleetManagement null检查）
      ├── 4.4b ✅ 一键启动脚本（start.sh / stop.sh）
      ├── 4.4c ✅ 数据库初始化脚本（init-db.sql + init-db.sh）
      ├── 4.4d ✅ 清理临时文件 + 文档更新
      │     ├── ✅ verify/ 目录已删除
      │     ├── ✅ 旧 Vue 2 项目文件已清理
      │     ├── ✅ PROJECT_RULES.md 已更新（Vue 3 相关信息）
      │     └── ✅ README.md 已创建
      └── 4.4e ✅ 模拟数据注入脚本（inject-data.sh，支持Redis/WebSocket双模式）
```

---

## Phase 5: 算法验证

```
Phase 5 - 算法验证 (2/2) ✅

  验证目标：确保反编译还原的算法逻辑与原始JAR行为一致
  涉及算法：
  ├── detector4kt/BumpyProcessor — KT710颠簸检测
  │    5条件：轮速差>1, 转向比差值>=3, 平均制动压力<1, 相关系数<0.5, 平均车速<9.7
  ├── detector4kt/SlipperyProcessor — KT710湿滑检测（待补充分析）
  ├── detector4motion/BumpyProcessor4Motion — 6轴运动颠簸检测
  │    7帧滑动窗口，极值分析(波峰波谷)，Level 3/5/7 阈值分级
  └── Python wit_bumpy_algorithm — 六轴传感器颠簸（scipy信号分析，与Java逻辑等价）

  原始JAR位置: reference/decompiled-jar/opt/etas/vaas/

├── 5.1 [完成] 算法单元测试 ✅
│     ├── BumpyProcessorTest（detector4kt）：7/7 ✅ 验证5条件判定、转向比计算、轮速差
│     ├── BumpyProcessor4MotionTest（detector4motion）：10/10 ✅ 验证极值分析、Level分级、az过滤
│     └── Python 交叉验证（wit_bumpy_algorithm.py）：9/9 ✅ 验证 Python/Java 逻辑等价
│
└── 5.2 [完成] 原始JAR黑盒对比测试 ✅
      ├── BumpyProcessor 字节码对比：完全一致 ✅
      └── BumpyProcessor4Motion 字节码对比：完全一致 ✅
```

---

## Phase 6: 上线前整改

```
Phase 6 - 上线前整改 (0/15) 📋 已规划，待启动

  优先级: 🔴 P0=必须解决才能上线  🟠 P1=强烈建议  🟡 P2=建议考虑
  当前状态: 全系统HTTP明文/无鉴权/空密码，仅适合开发演示
============================================================

🔴 P0: 必须解决（6项）
├── 6.1 [待开始] 认证与鉴权
│     ├── 所有 API 无鉴权保护：Controller 全部 @CrossOrigin 开放
│     ├── WebSocket 端点 /ws/kt /ws/motion /ws/location 无需认证
│     ├── admin-api Sa-Token 未启用
│     ├── 大屏 API 完全公开（车辆位置、事件数据）
│     ├── 需做：启用 JWT 鉴权、WebSocket 连接认证、CORS 白名单
│     └── 原始系统使用 WSS mTLS 双向证书认证
│
├── 6.2 [待开始] 通信加密
│     ├── 全部 HTTP/WS 明文传输，无 SSL/TLS
│     ├── 需做：Nginx 反代 + HTTPS/WSS，证书管理
│     └── 原始系统使用 wss://sris-wuxi.bosch-mobility-solutions.cn
│
├── 6.3 [待开始] 数据库密码与凭证
│     ├── 5个微服务的 MySQL 密码全部为空 ${DB_PASSWD:}
│     ├── Redis 无密码认证
│     ├── admin 登录密码依赖 config/password.txt
│     └── 需做：设置强密码、环境变量注入、专有用户按需授权
│
├── 6.4 [待开始] 基础设施与部署
│     ├── 6个微服务靠手动 java -jar 启动，无容器化
│     ├── 需做：Dockerfile、docker-compose、健康检查、CI/CD
│     └── 生产推荐 K8s 编排
│
├── 6.5 [待开始] 配置管理
│     ├── 开发期硬编码（.env 指向 localhost、vue proxy、许可证路径）
│     ├── 无多环境配置体系
│     └── 需做：spring.profiles.active、前端构建注入、配置中心
│
└── 6.6 [待开始] Nginx 反向代理 & 前端部署
      ├── 前端依赖 Vue CLI devServer 代理，无生产部署方式
      ├── 需做：Nginx 统一入口、静态资源托管、HTTPS 终止
      └── 大屏和管理后台的路由分发

🟠 P1: 强烈建议（4项）
├── 6.7 [待开始] 高可用与容灾
│     ├── 所有组件单点（单 MySQL / 单 Redis / 单实例微服务）
│     ├── 需做：MySQL主从、Redis Sentinel、多副本、熔断配置
│     └── Redis AOF/RDB 持久化未配置
│
├── 6.8 [待开始] OBU 协议适配层
│     ├── receiver 硬编码 KT710 协议，换厂商需改代码
│     ├── 需做：ProtocolAdapter 接口层、标准化数据模型、配置化切换
│     └── 3个端点：/ws/kt(KT710 CAN) /ws/motion(6轴) /ws/location(GPS)
│
├── 6.9 [待开始] WebSocket 安全加固
│     ├── Redis 队列有容量(80000)但无溢出保护
│     ├── 需做：连接数限制、队列降级策略、心跳检测、频率限制
│     └── 消息频率限制防恶意冲刷
│
└── 6.10 [待开始] 外部依赖管理
      ├── 地图服务 API Key 配置（高德/百度）
      ├── 气象站 SDK 对接（SDK2:2555 / SDK3:2345）
      ├── OcrService 涉及阿里云 OSS
      ├── TrueLicense 许可证管理
      └── 所有外部依赖的故障切换预案

🟡 P2: 建议考虑（5项）
├── 6.11 [待开始] 监控与告警
│     ├── Prometheus + Micrometer 暴露指标
│     ├── 业务监控（事件处理量/延迟/错误率）
│     ├── Redis/MySQL 性能监控
│     └── Grafana 仪表盘 + 告警规则
│
├── 6.12 [待开始] 数据生命周期
│     ├── 事件数据保留策略
│     ├── Redis ZSet 持续增长清理
│     └── MySQL 归档/分表 + 备份恢复策略
│
├── 6.13 [待开始] 日志体系
│     ├── JSON 结构化日志
│     ├── 日志级别按环境配置
│     ├── 关键操作审计日志
│     └── 日志轮转和保留策略
│
├── 6.14 [待开始] 文档与应急预案
│     ├── 部署手册（环境、启动、配置）
│     ├── 运维手册（日志、故障处理、重启）
│     ├── 应急预案（宕机、数据丢失、外部依赖故障）
│     └── API 文档
│
└── 6.15 [待开始] 合规与法律
      ├── 150辆出租车GPS轨迹属于敏感数据
      ├── 数据最小化原则
      ├── PC5 预警信息的法律边界
      └── 开源依赖合规检查
```

---

## Phase 7: 大屏重构（Vue 3 + 高德地图重写）

> 📌 注：大屏上线运行后仍需持续迭代——样式细节调整、功能完善、微服务验证结果可视化

```
Phase 7 - 大屏重构 (33/33) ✅ 首版完成 + 1 次迭代

  说明：因 Source Map 还原的 CSS 和 render 函数无法正常集成，
       改用 Vue 3 + Vite + Element Plus + 高德地图 全新构建。
       后端 API 全部复用，功能 100% 覆盖。
  设计文档: docs/plans/2026-06-11-dashboard-redesign.md
  实施计划: docs/plans/2026-06-11-dashboard-plan.md

  P1 - 项目骨架 (4) ✅
  ├── 1.1 ✅ 创建 Vue 3 + Vite 项目
  ├── 1.2 ✅ 配置路由 + 环境变量
  ├── 1.3 ✅ 配置 Axios + API 封装
  └── 1.4 ✅ 配置 Pinia 状态管理

  P2 - 主布局 + 地图 (6) ✅
  ├── 2.1 ✅ 主布局组件 DashboardPage
  ├── 2.2 ✅ 顶栏组件 TopBar
  ├── 2.3 ✅ 高德地图加载与初始化
  ├── 2.4 ✅ 车辆位置标记
  ├── 2.5 ✅ 事件标记 + 气象站标记
  └── 2.6 ✅ 标记弹窗（弹框确认）

  P3 - 左侧图层面板 (4) ✅
  ├── 3.1 ✅ 面板折叠/展开交互
  ├── 3.2 ✅ 实时车队数据模块（已对接地图）
  ├── 3.3 ✅ 路网状态模块（单选+积水颠簸共存，已对接地图）
  └── 3.4 ✅ 实时气象数据模块（查看气象设备定位）

  P4 - 内容面板 (6) ✅
  ├── 4.1 ✅ 内容面板 DrawerContainer
  ├── 4.2 ✅ 图层面板复用
  ├── 4.3 ✅ 历史24h路面状态（ECharts）
  ├── 4.4 ✅ 道路实时路况模块
  ├── 4.5 ✅ 告警列表 AlarmList（含导出Excel）
  └── 4.6 ✅ 服务统计 ServiceStats

  P5 - 时间轴 + 实时通信 (4) ✅
  ├── 5.1 ✅ 时间轴组件 TimeSlider（过去23h~未来1h）
  ├── 5.2 ✅ SSE 实时接入（EventSource 连接）
  ├── 5.3 ✅ 定时刷新逻辑（15分钟）
  └── 5.4 ✅ 时间轴联动（API hour 参数适配）

  P6 - 集成联调 (4) ✅
  ├── 6.1 ✅ 高德地图 Key 配置验证（暗色风格地图）
  ├── 6.2 ✅ 全链路数据联调（Redis 注入 → API → 前端展示）
  ├── 6.3 ✅ 异常处理（API 错误捕获 + 空数据降级）
  └── 6.4 ✅ 兼容性 + 性能（暗色主题适配）

  P7-iter.1 - 数据接入小项 (1) ✅
  └── 7-iter.1 ✅ 联网车辆数（/location）+ 降水量数值（/get_weather.precip）接入
                    - 修正设计文档 §2.2 中"车队分类切换"为误描述（原版无此功能）

  P7-iter.2 - 视觉与重构 (4) ✅
  ├── 7-iter.2.1 ✅ 弹窗组件替换：新增 components/Popup.vue，3 个 ElMessageBox 全部改为自定义 Popup
  │                    - 4 种类型（info/success/warning/danger）+ Teleport + Esc 关闭 + 暗色主题
  ├── 7-iter.2.2 ✅ 地图标记重做：内嵌 SVG 车辆/事件/气象站图标，事件加脉冲动画
  ├── 7-iter.2.3 ✅ LayerPanel 抽组件：消除左栏+抽屉左栏重复代码，新增 components/LayerPanel.vue
  └── 7-iter.2.4 ✅ 细节调整：S 图标字符→SVG 九宫格、彩虹 ribbon→棕/金渐变、抽屉 70%→100% 全屏
```

---

## P7+ 后续迭代（大屏持续完善）

```
P7 后续任务 - 待启动
├── 🎨 大屏样式调优 — 持续根据原版系统调整视觉细节
├── 🧪 微服务验证可视化 — 所有微服务（receiver/detector4kt/detector4motion/
│   vaas-backend/admin-api/Python算法）的验证结果需要在大屏上展示
│   - 服务健康状态
│   - 算法验证结果
│   - 数据链路监控
├── 🔧 功能补齐 — 根据使用反馈持续完善交互细节
└── 📊 数据监控看板 — 实时数据流入/事件处理/算法判定情况的展示
```

---

## Phase 8: 工程优化（脚本与工具链）

> 调研发现 16 个可优化点，已全部完成

```
Phase 8 - 工程优化 (16/16) ✅ 全部完成

  O1 - JDK 路径自适应 (1) ✅
  └── 8.1 ✅ start.sh JDK 检测逻辑改造：环境变量→系统 java→macOS java_home→Linux 路径
                移除硬编码 /Users/yelinshan/tools/jdks/... 路径

  O2 - 状态检查 (1) ✅
  └── 8.2 ✅ 新增 scripts/status.sh：基础设施 + 5 微服务 + dashboard 一键状态查询
                支持单服务查询，含 PID+端口+HTTP 健康检查

  O3 - 日志查看 (1) ✅
  └── 8.3 ✅ 新增 scripts/logs.sh：日志列表 + 单服务查看 + -f 实时跟踪 + -n 行数控制
                支持 -h 帮助，参数风格与 tail 一致

  O4 - 一键重启 (1) ✅
  └── 8.4 ✅ 新增 scripts/restart.sh：stop + start 一条龙
                支持单服务、-f 强制 kill、--no-start 仅停止

  O5~O9 - 工程规范组 (5) ✅
  ├── 8.5  ✅ .gitignore 补全：admin dist/、vim/IDEA 临时文件、pid.lock
  ├── 8.6  ✅ pre-commit hook：自动检查 TASK_TRACKING.md 同步
  ├── 8.7  ✅ CHANGELOG.md：建立版本变更追溯（v0.1.0 → v0.15.0）
  ├── 8.8  ✅ verify.sh：一键跑 npm build + mvn compile + pytest
  └── 8.9  ✅ README 系统架构图：Mermaid 流程图 + 时序图

  O10~O16 - 长线优化 (7) ✅
  ├── 8.10 ✅ vaas-backend OpenAPI/Swagger：springdoc-openapi + @OpenAPIDefinition
  ├── 8.11 ✅ inject-data.sh 使用文档：完整 35 行头部注释
  ├── 8.12 ✅ stop.sh 强制 kill 选项：-f / --force
  ├── 8.13 ✅ start.sh 端口冲突检测：lsof 检测 + 提示占用进程
  ├── 8.14 ✅ CLAUDE.md / PROJECT_RULES.md 查重：结论是不重叠，提案文档 docs/refactor-claude-rules-proposal.md
  ├── 8.15 ✅ LICENSE 文件：MIT
  └── 8.16 ✅ Actuator 健康检查：5 个微服务全加依赖，application.yml 暴露 /actuator/health
```

### P8 验证结果

- ✅ 5 个新/改脚本语法全部通过 (`bash -n`)
- ✅ 3 个新脚本 (`status.sh` `logs.sh` `restart.sh` `verify.sh`) 已 `chmod +x`
- ✅ pre-commit hook 已 `chmod +x` 写入 `.githooks/`
- ✅ 5 个后端微服务 `mvn compile -DskipTests` 全部通过
- ✅ OpenAPI / Actuator 集成无编译错误

### P8 集成建议

部署 pre-commit hook（项目根目录执行一次）:
```bash
git config core.hooksPath .githooks
```

部署后启动服务可访问:
- OpenAPI JSON: `http://localhost:50410/v3/api-docs`
- Swagger UI: `http://localhost:50410/swagger-ui.html`
- 健康检查: `http://localhost:50410/actuator/health`（及其他 4 个服务）

---

## 已记录问题 / 已知缺项

```
1. [admin 前端还原不完整] — 管理后台无 Source Map，目前仅通过 js-beautify
   反混淆后还原了4个核心页面（首页/车辆绑定/心跳管理/动态日志）共约119行代码。
   原始管理后台还包含：系统配置、设备管理、权限管理、数据报表等页面未还原。
   详见 LESSONS_LEARNED.md#005

2. [大屏 CSS/图片未还原] — Source Map 不映射样式和图片资源。
   当前大屏通过拷贝原始文件补充 static/css 和 images。
   如需修改样式，需基于 frontend/dashboard/src/ 重新开发样式层。

3. [Truelicense 许可证路径硬编码] — LicenseCreator.java 中保留
   C:\Users\SOQ2WX\... 路径，上线前需配置化。
```

---

## 状态图例

| 标记 | 含义 |
|------|------|
| [待开始] | 任务尚未启动 |
| [进行中] | 任务正在执行 |
| [暂停] | 任务因依赖/阻塞暂停 |
| [完成] | 任务已完成 |
| [取消] | 任务不再需要 |

---

## Phase 完成审计清单（P8 新增）

> **强制执行**：每个 Phase 标记为 100% 完成前，必须完成以下审计。任何一项"否"都需要修复后才能标完成。

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
├── [ ] CLAUDE.md 规则合理性
│     ├── 强制操作步骤是否需要更新？
│     ├── 复现原则是否仍适用？
│     └── 与 PROJECT_RULES.md 是否仍互补不重叠？
├── [ ] CHANGELOG.md 已更新
│     └── 本 Phase 的关键改动已记录在 Unreleased 或新版本号？
├── [ ] pre-commit hook 规则覆盖
│     └── 本 Phase 涉及的改动类型都有对应 hook 规则？
└── [ ] 文档 vs 代码一致性（自动检查项）
      ├── backend/*/src/main/java/*Controller.java 改动 → PROJECT_RULES §〇.6
      ├── backend/*/pom.xml 改动 → 端口表（PROJECT_RULES + README）
      ├── backend/*/application.yml 改动 → 端口表
      ├── frontend/dashboard/src/ 目录变化 → PROJECT_RULES §〇.5
      └── backend/{新服务名}/ 增删 → 架构图 + 端口表
```

### pre-commit hook 当前规则（9 条）

| # | 触发 | 提醒文档 | 章节 |
|---|------|---------|------|
| 1 | frontend/dashboard/src/ 或 backend/*/src/ 改动 | TASK_TRACKING.md | 进度概要 |
| 2 | scripts/*.sh 改动 | README.md | 脚本工具 |
| 3 | docs/plans/ 新增 | TASK_TRACKING.md | 引用 |
| 4 | PROJECT_RULES.md / CLAUDE.md 改动 | 团队同步 | - |
| 5 | backend/**/controller/ 改动 | PROJECT_RULES.md | §〇.6 API |
| 6 | backend/*/pom.xml 改动 | PROJECT_RULES.md + README.md | §〇.2 + 端口表 |
| 7 | frontend/dashboard/src/ 目录结构变化 | PROJECT_RULES.md | §〇.5 + §〇.7 |
| 8 | backend/{service}/ 增删 | PROJECT_RULES.md + README.md | 架构图 + 端口表 |
| 9 | backend/*/application.yml 改动 | PROJECT_RULES.md + README.md | §〇.2 + 端口表 |

**审计机制**：当 P{N} 标完成时，AI 必须**逐项对照**此清单并报告结果。如发现不一致，必须先修复文档再标完成。

---

## P7 修复记录 (2026-06-12)

```
样式修复:
├── ✅ 全局配色：蓝色调 → 棕/金色调
├── ✅ 抽屉背景：#0a1628 → #1a1a1a
├── ✅ 图层选中：蓝色半透明 → 棕→金渐变
├── ✅ 统计数值：蓝色 → 金色 #FFF6DA
├── ✅ 表格/按钮：蓝色系 → 棕色系

功能修复:
├── ✅ 养护建议列表（颠簸/湿滑/积水路段）
├── ✅ 时间轴联动刷新
├── ✅ 事件删除调 API
├── ✅ 图层叠加效果（圆形覆盖层）
└── ✅ 数据接入：联网车辆数（/location）+ 降水量（/get_weather.precip）

数据修复:
└── ✅ 注入更多测试事件（13 颠簸 +7 湿滑 +3 积水）
```

## P7 迭代详情：数据接入小项

**任务**：[P7-iter.1] 联网车辆数 + 降水量数值接入

**问题点**：
- "实时车队数据"模块只显示"联网车辆"文字，无实时数字
- "实时气象数据"模块只显示"降水量"文字，无数值

**后端支持**：
- `/location` 返回 `Map<deviceId, OnlineVehicle>` —— Object.keys 长度即车辆数
- `/get_weather` 返回的 `Weather.precip` 字段（Float）即降水量

**实施**（无后端改动）：
- [api/index.js](frontend/dashboard/src/api/index.js) - 新增 `getOnlineVehicles()` 方法
- [DashboardPage.vue](frontend/dashboard/src/views/DashboardPage.vue)：
  - 新增 `onlineCount` + `precipText` 状态
  - 新增 `loadOnlineVehicles()` 函数
  - `loadCoveredData()` 增强：读 `precip` 字段并格式化为 "X.X mm"
  - onMounted + 15min 定时器接入 `loadOnlineVehicles()`
  - 4 处模板（左侧面板 + 抽屉内 × 2 模块）同步显示数值
  - `.num` CSS 数值高亮（金色加粗）

**取消项**：
- ❌ 车队分类切换（全部/出租车/巴士）—— 用户实地观察原版大屏确认**无此功能**，设计文档 §2.2 描述有误
- 已知缺项：若日后需要分类，需后端扩展 OnlineVehicle VO 增加 dataType 字段

**构建验证**：✅ `npm run build` 通过，2.60s，无错误
