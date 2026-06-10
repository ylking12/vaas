# VaaS 项目复现 - 任务跟踪总表

> 更新时间: 2026-06-10 (v11) | 大屏+管理后台完整可用 ✅ | 进入收尾阶段

## 进度概要

| Phase | 总任务数 | 完成 | 进行中 | 待开始 | 进度 |
|-------|---------|------|--------|-------|------|
| P1 参考素材提取 | 11 | 10 | 0 | 1 | 91% | ⬅️ 1.10 已取消 |
| P2 后端还原 | 17 | 17 | 0 | 0 | 100% ✅ |
| P3 前端还原 | 6 | 6 | 0 | 0 | 100% ✅ |
| P4 集成验证 | 4 | 3 | 0 | 1 | 75% | ⏳ 含4项子任务
| **合计** | **39** | **37** | **0** | **2** | **95%** | ⏳ Phase 4 收尾中

---

## Phase 1: 参考素材提取

```
Phase 1 - 参考素材提取 (10/11, 1已取消)
├── 1.1  [完成] 完整解压 etas.tar，提取所有后端文件
│     └── ⚠️ 首次解压不完整（仅提取配置文件，缺失JAR和Python算法源码）
│          已重新解压补全，见 LESSONS_LEARNED.md#003
├── 1.1a [完成] ←[分支] 同步新发现资产到 PROJECT_RULES.md（架构/算法/服务表）
├── 1.2  [完成] 大屏前端 Source Map 全量还原到 reference/recovered-src/
├── 1.2a [完成] ←[分支] 验证还原的前端源码完整性——结论：JS逻辑文件100%可用，Vue组件需反编译脚本部分
├── 1.2b [完成] ←[分支] 从编译 JS 中反提取 Vue 组件脚本逻辑——成功提取 dashboard.vue 完整组件 (20 methods, 29 data props)
├── 1.2c [完成] ←[分支] 前端还原验证——结论：JS逻辑/组件结构 ✅，CSS样式/图片 ❌（Source Map 不还原样式），改为代码审查方式验证
├── 1.3  [完成] 安装 CFR 并反编译 receiver.jar — 成功，13个业务类
├── 1.4  [完成] 安装 CFR 并反编译 vaas_backend.jar — 成功，62个业务类（核心算法引擎）
├── 1.5  [完成] 安装 CFR 并反编译 admin/admin.jar — 成功，34个业务类
├── 1.6  [完成] 安装 CFR 并反编译 detector4kt/detector4kt.jar — 成功，15个业务类
├── 1.7  [完成] 安装 CFR 并反编译 vaas_detector4motion/ — 成功，8个业务类
├── 1.8  [完成] 安装 CFR 并反编译 vaas-trajectory-simulator — 成功，18个业务类
├── 1.9  [完成] 提取 Python 源码到 simulator/python/
│      ├── ✅ vaas_6_axis_consumer/ 六轴颠簸算法 (6个核心算法文件)
│      ├── ✅ vaas_script/ 数据处理脚本
│      ├── ✅ vaas_python_venv/scripts/post.py 数据推送脚本
│      ├── ✅ event_simulator/ 事件模拟器 (3个脚本)
│      └── ✅ sse_client.py / utils.py 等辅助脚本
└── 1.10 [取消] 关键业务配置提取 — 配置已全量解压到 _extracted/，关键信息已提炼到 PROJECT_RULES.md
```

---

## Phase 2: 后端还原

```
Phase 2 - 后端还原 (17/17)

  -- receiver 服务 (port 50412) --
  ├── 2.1  [完成] receiver - 反编译代码整理合并到 backend/receiver/ (13个业务类)
  ├── 2.1a [完成] ←[分支] 反编译 common-0.0.1-SNAPSHOT.jar 并创建 vaas-common 模块 (59个公共类)
  ├── 2.2  [完成] receiver - pom.xml 还原 (Spring Boot 3.5.3 + WebFlux)
  ├── 2.3  [完成] receiver - application.yml + config.yaml 配置重写
  └── 2.4  [完成] receiver - 编译验证 & bug修复

  -- vaas-backend 服务 (port 50410, /spring/v1) --
  ├── 2.5  [完成] vaas-backend - 反编译代码整理合并到 backend/vaas-backend/ (62个业务类)
  ├── 2.5a [完成] ←[分支] 反编译类型问题修复 (约65处编译错误)
  ├── 2.6  [待开始] vaas-backend - 算法模块提取到 backend/algorithm/
  ├── 2.7  [待开始] vaas-backend - pom.xml + 多环境配置还原
  └── 2.8  [待开始] vaas-backend - 编译验证 & bug修复

  -- detector4kt 服务 (KT710事件检测) --
  ├── 2.9  [完成] detector4kt - 反编译代码整理到 backend/detector4kt/ (15个业务类)
  ├── 2.10 [完成] detector4kt - pom.xml + config.yaml 还原
  └── 2.11 [完成] detector4kt - 编译验证 & bug修复

  -- detector4motion 服务 (6轴运动检测) --
  ├── 2.12 [完成] detector4motion - 反编译代码整理到 backend/detector4motion/ (8个业务类)
  ├── 2.13 [完成] detector4motion - pom.xml + config.yaml 还原
  └── 2.14 [完成] detector4motion - 编译验证 & bug修复

  -- admin 服务 (管理后台API) --
  ├── 2.15 [完成] admin - 反编译代码整理到 backend/admin-api/ (34个业务类)
  ├── 2.16 [完成] admin - pom.xml + 配置还原 + 编译验证 ✅
  │     └── 主要问题: AdminService LambdaQueryWrapper原始类型、OcrService阿里云SDK、HeartbeatComponent符号缺失

  -- 数据层 --
  ├── 2.17 [完成] 数据库 DDL + Redis 结构定义还原 ✅
  └── 2.18 [完成] 基础设施部署（MySQL + Redis 安装、建表、配置连接）✅

  -- 集成验证节点 --
     ↑ Phase 2 全部完成后可进入服务启动验证阶段
```

---

## Phase 3: 前端还原

```
Phase 3 - 前端还原 (6/6) ✅ 全部完成
├── 3.1 [完成] dashboard - Source Map 还原源码整理到 frontend/dashboard/
├── 3.2 [完成] dashboard - package.json + Webpack 配置还原
├── 3.3 [完成] dashboard - npm install && 编译验证 (13MB)
├── 3.4 [完成] admin - 从编译JS提取路由/页面/API结构
├── 3.5 [完成] admin - Vue 3 + Vite 项目骨架搭建
└── 3.6 [完成] admin - 从反混淆JS还原3个核心页面（车辆绑定/心跳/日志）✅
```

---

## Phase 4: 集成验证

```
Phase 4 - 集成验证 (3/4) ⏳
├── 4.1 [完成] 后端 6 个微服务联调 — 5个SpringBoot + Python算法全部启动运行 ✅
├── 4.2 [完成] 模拟器数据注入测试 — bump/slip 事件成功写入 MySQL + Redis ✅
├── 4.3 [完成] 前后端全链路联调 — 大屏/管理后台可访问，API代理打通 ✅
└── 4.4 [待开始] 功能完整性验证 & 收尾
      ├── 4.4a 修复大屏部分API 400错误（get-alarm-list等参数适配）
      ├── 4.4b 一键启动脚本（start.sh）
      ├── 4.4c 数据库初始化脚本（含初始数据）
      ├── 4.4d 清理临时文件，整理项目文档
      └── 4.4e 模拟数据注入脚本 — 通过WebSocket持续发送KT710数据，验证全链路
```

---

## 分支任务记录

> 执行过程中产生的分支任务在此处追加

| 编号 | 父任务 | 说明 | 状态 | 创建日期 |
|------|--------|------|------|---------|
| 1.1a | 1.1 | 同步新发现资产到 PROJECT_RULES.md（架构/算法/服务表） | 完成 | 2026-06-09 |
| 1.1b | 1.1 | 【补全】重新解压 etas.tar（首次解压不完整），补充JAR和Python算法源码 | 完成 | 2026-06-09 |

---

## 状态图例

| 标记 | 含义 |
|------|------|
| [待开始] | 任务尚未启动 |
| [进行中] | 任务正在执行 |
| [暂停] | 任务因依赖/阻塞暂停 |
| [完成] | 任务已完成 |
| [取消] | 任务不再需要 |
| [分支] | 执行中产生的分支子任务 |
