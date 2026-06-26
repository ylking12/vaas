# VaaS 项目复现 — AI 开发规范

## ⚡ 权限与确认

- ✅ **已获完全授权**：所有工具调用不再逐一申请权限
- ⛔ **每项新任务启动前，必须先文字说明**（下一项做什么 + 预计产出），获明确同意后再执行
- 用户回复"继续/开始/确认/开干"等肯定词后开始

---

## ⏳ 会话流程

### 开始时

1. **读 [_CONTEXT_SUMMARY.md](./_CONTEXT_SUMMARY.md)**（精简摘要）
2. 摘要不够用时，按需读取完整文件：
   - [PROJECT_RULES.md](./PROJECT_RULES.md) — 项目全景、架构、约束
   - [TASK_TRACKING.md](./TASK_TRACKING.md) — 任务进度
   - [LESSONS_LEARNED.md](./LESSONS_LEARNED.md) — 问题复盘

### 任务完成后

- 立即更新 [TASK_TRACKING.md](./TASK_TRACKING.md)：改状态、追加分支任务、更新进度概要
- 任务中断（等确认）则保留 `[进行中]`

---

## 🎯 复现基本原则

**定位**：从编译后代码反编译/还原可编译运行的源码，不是重新开发。还原度是第一优先级。

**还原 vs 重写边界**：

| 场景 | 方式 |
|------|------|
| 有 Source Map 的前端 JS / .vue | 100% 还原原始源码 |
| JAR 反编译的 Java 类 | 还原为 .java，包名/类名/方法签名一致 |
| 算法逻辑 | 反编译提取 + 对照详细设计文档验证参数 |
| 无 Source Map 的管理后台 | 提取路由/API/组件骨架，UI 可重写但功能逻辑一致 |
| 明显的 bug | 允许修复，注释说明原因 |
| 缺失泛型、未用 import | 允许补充/清除 |

---

## 🚫 诚信底线（最高优先级）

- ❌ **严禁 Demo 页面/模拟数据/假界面冒充还原产物**
- ❌ **严禁用替代品顶替未完成的工作** — 必须告知用户"未完成"并征求意见
- ❌ **严禁隐瞒代码真实来源** — 交付时说明哪些是还原的、补充的、缺失的
- ❌ 修改原始业务逻辑、算法阈值、判定条件
- ❌ 修改 API 签名（路径、方法、参数名、返回值结构）
- ❌ 修改数据库表结构、字段名、索引
- ❌ 修改 Redis key 命名（`vaas:` 前缀体系）
- ❌ 引入全新中间件
- ❌ 升级 JDK（锁定 Java 17）
- ❌ 混入第三方闭源代码
- ❌ 提交真实密码/API Key/证书

---

## 🏷️ 文件头标注

**Java 文件：**
```java
/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.controller.EventController | STATUS: Restored */
```

**前端 JS/TS 文件：**
```javascript
/** SOURCE: Recovered from source map (app.xxx.js.map) | ORIGINAL: src/views/dashboard.vue | STATUS: Restored */
```

**算法文件（额外标注公式来源）：**
```java
/** ALGORITHM: 路面颠簸分析 | SOURCE: Decompiled from vaas-backend.jar | DOC-REF: 详细设计说明书 §5.3.1 | STATUS: Restored -- doc verified */
```

---

## 📋 一条铁律

**所有待处理事项只记一个地方 — `TASK_TRACKING.md`**。包括计划、问题、待整改项。

- ❌ 禁止记到 `.claude/memory/`、`LESSONS_LEARNED.md` 或口头承诺
- ✅ memory 只存背景知识/决策理由（非待办）
- ✅ `LESSONS_LEARNED.md` 只记已发生的问题复盘

---

## 📊 大屏复原基线

> 位置：[docs/dashboard-baseline/](docs/dashboard-baseline/)
> 用途：原大屏 vs P7 重写版的回归测试依据
> 详见 `PROJECT_RULES.md §四.5`
