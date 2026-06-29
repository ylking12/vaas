# VaaS 项目复现 - 任务跟踪总表

> 更新时间: 2026-06-26 | 已完成任务的详细记录已归档到 [ARCHIVE.md](./ARCHIVE.md)

## 进度概要

| Phase | 总任务数 | 完成 | 进行中 | 待开始 | 进度 |
|-------|---------|------|--------|-------|------|
| P1 参考素材提取 | 10 | 9 | 0 | 0 | 90% | ⬅️ 1.10 已取消 |
| P2 后端还原 | 20 | 20 | 0 | 0 | 100% ✅ |
| P3 前端还原 | 6 | 6 | 0 | 0 | 100% ✅ |
| P4 集成验证 | 4 | 4 | 0 | 0 | 100% ✅ |
| P5 算法验证 | 2 | 2 | 0 | 0 | 100% ✅ |
| P6 上线前整改 | 15 | 0 | 0 | 15 | 0% | 📋 已规划 |
| P7 大屏重构 | 37 | 37 | 0 | 0 | 100% ✅ |
| P7+ 后续迭代 | 7 | 7 | 0 | 0 | 100% ✅ |
| P8 工程优化 | 16 | 16 | 0 | 0 | 100% ✅ |
| **合计** | **114** | **99** | **0** | **15** | **87%** | ⏳ 仅剩 P6 |

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

## Phase 6: 上线前整改 (0/15) 📋

```
优先级: 🔴 P0=必须解决  🟠 P1=强烈建议  🟡 P2=建议考虑
当前状态: 全系统HTTP明文/无鉴权/空密码，仅适合开发演示

🔴 P0: 必须解决（6项）
├── 6.1 ⏳ 认证与鉴权 — API/WS 全开放，需 JWT + WS 连接认证 + CORS 白名单
├── 6.2 ⏳ 通信加密 — 全部 HTTP/WS 明文，需 Nginx HTTPS/WSS
├── 6.3 ⏳ 数据库密码 — MySQL 空密码 / Redis 无认证 / admin 依赖 password.txt
├── 6.4 ⏳ 容器化部署 — 手动 java -jar，需 Docker + docker-compose
├── 6.5 ⏳ 配置管理 — 硬编码 .env / localhost，需多环境配置体系
│   └── 📌 已确认：线上大屏原始编译版使用相对路径 `/spring/v1/`（Nginx 反代），非 localhost 直连
└── 6.6 ⏳ Nginx 反代 — 前端无生产部署方式，需统一入口 + HTTPS 终止

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

## 已记录问题 / 已知缺项

1. **[admin 前端还原不完整]** — 无 Source Map，目前反混淆还原了4个核心页面。原始管理后台的系统配置、设备管理、权限管理、数据报表等未还原。（详见 LESSONS_LEARNED.md#005）

2. **[大屏 CSS/图片未还原]** — Source Map 不映射样式和图片资源。当前通过拷贝原始文件补充 static/css 和 images。

3. **[Truelicense 许可证路径硬编码]** — `LicenseCreator.java` 保留 `C:\Users\SOQ2WX\...` 路径，上线前需配置化。

4. ~~[B1 时间轴联动失效] — **已修复**（2026-06-22）~~（详见 ARCHIVE.md）

5. ~~[B2 p7-baseline-capture.js 端口错误] — 8083→8082~~（**未修**，独立 bug）

### git 未提交改动（之前 session 累积）

```
6. backend/vaas-backend/pom.xml — 4 行 diff
7. frontend/dashboard/src/components/Popup.vue — 2 行 diff
8. docs/_generate_pdf.py — 新文件
9. docs/hardware-data-protocol.md — 新文件
10. docs/通勤预警协议.pdf — 新文件
```

---

## 项目文档索引

| 文档 | 位置 | 说明 |
|------|------|------|
| 替换影响分析报告 | [docs/replacement-impact-report.md](./docs/replacement-impact-report.md) | 后端整站替换到线上的全面影响分析（2026-06-26） |

## 待部署事项

> 由用户在适当时机触发部署

```
待部署：大屏替换到线上环境
├── 前置条件：线上 Nginx 已有 SPA fallback 配置
├── [待开始] Step 1：修改 .env VITE_API_BASE 为线上地址
├── [待开始] Step 2：npm run build 构建 dist/
├── [待开始] Step 3：备份线上大屏 → 替换为新的 dist/
└── 风险：无（纯静态文件替换，Nginx 配置不用改）
```
