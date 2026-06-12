# Changelog

All notable changes to the VaaS Reproduction project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- 8.10 后端 OpenAPI/Swagger 文档 (vaas-backend)
- 8.16 后端 Actuator 健康检查端点 (5 个微服务)

## [0.15.0] - 2026-06-12

### Added
- 8.5 .gitignore 补全（admin dist/、vim/IDEA 临时文件、pid.lock）
- 8.6 pre-commit hook（自动检查 TASK_TRACKING.md 同步）
- 8.7 CHANGELOG.md（本文件）
- 8.8 verify.sh 一键验证脚本
- 8.9 README 系统架构图（Mermaid 流程图）
- 8.11 inject-data.sh 使用文档（脚本头部注释）
- 8.12 stop.sh 强制 kill 选项 (`-f`/`--force`)
- 8.13 start.sh 端口冲突检测
- 8.15 LICENSE 文件（MIT）
- P8-iter.1 联网车辆数 + 降水量数值接入
- status.sh / logs.sh / restart.sh 三个工具脚本
- start.sh JDK 路径自适应（移除硬编码）

### Changed
- README.md 脚本章节重新整理（脚本表 + 常用示例）

## [0.14.0] - 2026-06-12

### Added
- 7-iter.1 大屏数据接入：联网车辆数 (`/location`) + 降水量 (`/get_weather.precip`)
- `.num` 数值高亮 CSS（金色加粗）

### Fixed
- 修正 TASK_TRACKING.md 中 P4 4.4 主任务状态（[待开始]→[完成]）
- 修正 P5 章节标题（(0/2)→(2/2)）
- 修正 P2 实际任务数（17→20）
- 修正 P7 任务数（32→33，加 7-iter.1）

## [0.13.0] - 2026-06-12

### Added
- 大屏样式首版（Vue 3 + Element Plus + 高德地图暗色主题）
- 棕/金色调全局配色
- 养护建议列表（颠簸/湿滑/积水路段）
- 时间轴联动刷新
- 事件删除 API 调用
- 图层叠加效果（圆形覆盖层）
- 注入更多测试事件（13 颠簸 + 7 湿滑 + 3 积水）

## [0.12.0] - 2026-06-11

### Added
- P7 大屏重构首版（32 个任务，6 个子阶段：骨架/布局/面板/抽屉/时间轴/联调）

## [0.11.0] - 2026-06-10

### Added
- 5.1 算法单元测试（BumpyProcessor / BumpyProcessor4Motion / Python 交叉验证）
- 5.2 原始 JAR 黑盒对比测试
- 4.4 集成验证收尾（API 空指针修复、一键启动脚本、数据库初始化、文档更新、模拟数据注入）

## [0.10.0] - 2026-06-10

### Added
- 4.1-4.3 集成验证：6 个微服务联调 + 模拟器数据注入 + 前后端全链路联调
- 3.4-3.6 管理后台还原（4 个核心页面，约 119 行）

## [0.9.0] - 2026-06-09

### Added
- 3.1-3.3 大屏前端 Source Map 还原（20+ methods、29 data、7 子组件）
- P2 后端还原（5 个 Spring Boot + Python 算法 + vaas-common 共 20 个任务）

## [0.1.0] - 2026-06-09

### Added
- 项目启动
- P1 参考素材提取（etas.tar 完整解压 + 6 个 JAR 反编译 + Python 源码提取）
- 项目规范文档（CLAUDE.md / PROJECT_RULES.md）

[Unreleased]: https://github.com/example/vaas-reproduction/compare/v0.15.0...HEAD
[0.15.0]: https://github.com/example/vaas-reproduction/compare/v0.14.0...v0.15.0
[0.14.0]: https://github.com/example/vaas-reproduction/compare/v0.13.0...v0.14.0
[0.13.0]: https://github.com/example/vaas-reproduction/compare/v0.12.0...v0.13.0
[0.12.0]: https://github.com/example/vaas-reproduction/compare/v0.11.0...v0.12.0
[0.11.0]: https://github.com/example/vaas-reproduction/compare/v0.10.0...v0.11.0
[0.10.0]: https://github.com/example/vaas-reproduction/compare/v0.9.0...v0.10.0
[0.9.0]: https://github.com/example/vaas-reproduction/compare/v0.1.0...v0.9.0
[0.1.0]: https://github.com/example/vaas-reproduction/releases/tag/v0.1.0
