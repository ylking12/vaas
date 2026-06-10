# VaaS 项目复现 - AI 开发规范

## 权限与工作节点控制

- ✅ **已获得用户完全授权**：所有工具调用（Bash、文件读写、网络请求等）不再逐一申请权限
- ⛔ **但每项新任务启动前，必须先向用户确认**：说明"下一项要做的事"和"预计产出"，获得用户明确同意后再执行
- 任务粒度的把握：一项任务 = 一个可独立交付的工作单元（如"反编译 receiver.jar"、"Source Map 还原大屏前端"）
- 确认方式：直接以文字描述（不是工具调用）向用户说明下一步计划，用户回复确认后开始执行

### 任务确认的话术模板

```
【下一项任务】<任务名称>
【目标】<要达成什么>
【操作内容】<具体做哪些事>
【预计产物】<完成后会产出什么>
【风险/注意】<如果有的话>
请确认是否开始？
```

用户说"继续/开始/确认/开干"等肯定性回复后，再开始执行。

---

## 分支任务自动记录规则

以下情况**自动创建分支任务**记录到 TASK_TRACKING.md，不需要等用户提醒：

| 触发场景 | 示例 | 记录方式 |
|---------|------|---------|
| **信息发现**：任务执行中发现新资产/新信息，需要更新项目规则或任务清单 | 解压后发现新JAR → 更新 PROJECT_RULES.md | 父任务编号 + a/b/c 后缀 |
| **验证派生**：任务完成后需要验证成果是否可用 | Source Map还原后 → 搭建项目验证 | 父任务编号 + a/b/c 后缀 |
| **阻塞派生**：当前任务被阻塞，需要先完成另一项前置任务 | 反编译缺少工具 → 先安装工具 | 父任务编号 + a/b/c 后缀 |
| **优化修复**：执行中发现可优化或需修复的问题 | 配置文件缺参数 → 补充参数 | 父任务编号 + a/b/c 后缀 |
| **用户要求**：用户主动提出的额外要求 | 用户要求验证方式 → 记录为分支任务 | 用户提出时立即记录 |

**自动记录格式**：
```
编号规则: 父任务编号 + 小写字母 (1.1 → 1.1a → 1.1b → 1.1c)
状态: 创建时标记为 [进行中] 或 [待开始]
位置: 紧跟父任务下方
```

**例外**：不影响任务清单的纯工具操作（如安装 npm 包、创建临时文件）不记分支任务。

---

## 每次会话开始的强制操作

每次启动工作前，必须先阅读以下三个文件：

**1️⃣ [PROJECT_RULES.md](./PROJECT_RULES.md)** — 项目全景了解
- 项目全景解读（架构、算法、数据流、API接口、页面结构）
- 当前阶段的复现策略和优先级
- 代码质量约束和规范
- 目录结构和工作流

**2️⃣ [TASK_TRACKING.md](./TASK_TRACKING.md)** — 当前任务进度
- 查看当前进行到哪个任务
- 确认上一个任务的完成状态
- 明确接下来该做什么

**3️⃣ [LESSONS_LEARNED.md](./LESSONS_LEARNED.md)** — 问题与复盘
- 查看已记录的历史问题和根因
- 检查预防措施清单
- 避免同类问题再次发生

若以上文件有更新，必须重新读取后再继续工作。

## 每次任务完成后的强制操作

每完成一项任务（无论主任务还是分支任务），必须立即更新 [TASK_TRACKING.md](./TASK_TRACKING.md)：
- 将已完成任务的状态改为 `[完成]`
- 如果产生了分支任务，追加到"分支任务记录"区
- 更新顶部的进度概要（完成数/总数 + 百分比）
- 更新时间戳

**例外情况**：如果任务执行中途被中断（如需要等待用户确认），则保留 `[进行中]` 状态。

---

## 一、复现基本原则

### 1.1 定位
本项目的目标是从编译后的代码反编译/还原出可编译运行的完整源代码，不是重新开发，也不是创新。**还原度是第一优先级**。

### 1.2 还原 vs 重写 的边界

| 场景 | 处理方式 |
|------|---------|
| 有Source Map的前端JS逻辑 | 100%还原为原始源码 |
| 有Source Map的.vue组件 | 还原组件结构，render函数保留（Vue 2编译产物） |
| JAR反编译的Java类 | 还原为.java源码，保持包名/类名/方法签名一致 |
| 算法逻辑 | 反编译提取 + 对照详细设计文档验证参数 |
| 无Source Map的管理后台 | 提取路由/API/组件骨架，UI层可重写但功能逻辑一致 |
| 明显的NPE/bug | 允许修复，但需注释说明修改内容 |
| 缺失的泛型类型 | 补充，不影响运行时行为 |
| import优化 | 允许，消除未使用的导入 |

### 1.3 绝对禁止的行为
- ❌ 修改原始业务逻辑、算法阈值、判定条件
- ❌ 修改API签名（路径、请求方法、参数名、返回值结构）
- ❌ 修改数据库表结构、字段名、索引设计
- ❌ 修改Redis key命名规范（`vaas:` 前缀体系）
- ❌ 引入全新的技术组件或中间件
- ❌ 升级JDK版本（锁定Java 8）
- ❌ 混入第三方闭源代码到还原产物中
- ❌ 提交真实密码/API Key/证书到代码库

---

## 二、代码还原规范

### 2.1 文件头标注格式

每个反编译/还原的文件，头部必须有标准注释：

**Java文件：**
```java
/**
 * SOURCE: Decompiled from receiver.jar (Spring Boot)
 * ORIGINAL: com.etas.vaas.receiver.controller.EventController
 * STATUS: Restored - compile verified
 * DATE: 2026-06-09
 */
```

**前端JS文件：**
```javascript
/**
 * SOURCE: Recovered from source map (app.xxx.js.map)
 * ORIGINAL: src/views/dashboard.vue
 * STATUS: Restored
 */
```

**算法文件**（额外标注）：
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
 * 
 * STATUS: Restored - doc verified
 */
```

### 2.2 还原代码的目录放置规则

| 操作 | 放入目录 |
|------|---------|
| Source Map 还原出的原始前端源码 | `reference/recovered-src/` |
| JAR 反编译出的原始 Java 源码 | `reference/decompiled-jar/` |
| 从参考源码整理出的可编译代码 | `frontend/dashboard/` 或 `backend/` |
| 从文档提取的关键信息 | `reference/docs-extract/` |

**关键规则**：`reference/` 下的文件是只读的，是"证据"；`frontend/` 和 `backend/` 下的文件是可编译的"产物"。先提取到 reference，再整理到对应模块。

### 2.3 算法还原特别规范

1. 反编译出算法类后，先整体阅读理解逻辑
2. 打开详细设计说明书对应章节，逐条核对公式和参数
3. 参数差异以反编译结果为准，设计文档作为参考
4. 算法类需要补充单元测试，覆盖正常值和边界值
5. 每个算法类独立一个包，避免耦合

### 2.4 处理参考素材时的约束

**反编译 JAR：**
- 使用 CFR (推荐) 或 FernFlower/Procyon
- 反编译输出到 `reference/decompiled-jar/` 下对应的子目录
- 保留原始目录结构（包名层级）
- 反编译失败的文件记录日志

**Source Map 还原：**
- 使用 `reverse-sourcemap` 工具
- 输出到 `reference/recovered-src/`
- 保留原始目录结构

**提取 Python 脚本：**
- 从 `etas.tar` 中提取 `opt/etas/vaas/*.py` 文件
- 直接复制到 `simulator/python/`
- 这些是原始源码，无需反编译

---

## 三、工作流程规范

### 3.1 每个Phase的执行模板

```
1. 【前置】读取 PROJECT_RULES.md + CLAUDE.md
2. 【执行】按照当前Phase计划执行任务
3. 【验证】每个文件还原后检查完整性
4. 【记录】更新 todo list 进度
5. 【提交】阶段性完成时输出汇总说明
```

### 3.2 多步工作的记录要求

涉及多个文件的操作（如批量反编译、批量Source Map还原）：
- 先列出完整的文件清单
- 按模块分批处理
- 每批完成后检查产物完整性
- 避免一次性处理过多文件导致遗漏

### 3.3 遇到问题时的处理

| 问题 | 处理方式 |
|------|---------|
| JAR反编译部分类失败 | 尝试不同反编译器（CFR/JD-GUI/Procyon） |
| Source Map映射不完整 | 保留已恢复的部分，标注缺失的文件 |
| 设计文档与代码不一致 | 以代码为准，注释中标注差异 |
| 无法确定原始逻辑 | 搜索日志文件中的调用链路辅助判断 |
| 第三方SDK/JAR无法反编译 | 保留原始jar引用，只还原业务代码 |

---

## 四、沟通与记录规范

### 4.1 进度记录
- 使用 TodoWrite 维护当前阶段的 todo list
- 每个 Phase 完成后输出汇总说明
- 关键决策（如某个模块放弃还原改用重写）需要记录原因

### 4.2 文件操作日志
- 大规模文件操作（批量反编译、批量复制）后，列出操作摘要
- 文件移动/重命名后，提供新旧路径对照

### 4.3 异常报告
- 反编译失败的文件列表
- Source Map 不完整的文件列表
- 与设计文档不一致的发现

---

## 五、各模块技术细节

### 5.1 后端 - receiver 服务

| 项目 | 内容 |
|------|------|
| 包名 | com.etas.vaas.receiver |
| 端口 | 50412 |
| WebSocket端点 | /ws/kt, /ws/motion, /ws/location |
| 数据库 | MySQL (vaas) + Redis |
| 配置方式 | application.yml + 环境变量 |
| 数据缓存 | Redis Hash/List/PubSub，key前缀 `vaas:` |
| 地理范围 | 无锡市：经度 [120.31, 120.60]，纬度 [31.44, 31.74] |

### 5.2 后端 - vaas-backend 服务

| 项目 | 内容 |
|------|------|
| 包名 | com.bosch.cs.rcs |
| 作用 | 核心业务 + 算法引擎 |
| 算法 | 颠簸检测、干湿识别、附着系数、气象事件 |
| 数据源 | Redis（消费receiver写入的数据） |
| 事件推送 | SSE (Server-Sent Events) |

### 5.3 前端 - 大屏 (dashboard)

| 项目 | 内容 |
|------|------|
| 框架 | Vue 2 + Webpack |
| UI库 | Element UI + ECharts |
| 地图 | 高德/百度地图 API |
| 路由 | 单页 `/dashboard`，`/` 跳转到 `/dashboard` |
| 状态管理 | Vuex (selectBur时间选择器, sensorData传感器数据) |
| HTTP | Axios (baseURL 来自 VUE_APP_URL 环境变量) |
| 组件结构 | road-map(地图) + layer(图层) + road-info(路况) + alarm(告警) + rcs-service(服务) + event(事件弹窗) |

### 5.4 前端 - 管理后台 (admin)

| 项目 | 内容 |
|------|------|
| 框架 | Vue 3 + Vite (v5.9.0) |
| UI库 | Element Plus |
| 页面 | 首页、车辆绑定、心跳管理、动态日志、系统配置、设备管理、权限管理、数据报表、异常页面 |

### 5.5 Redis 数据结构速查

| Key模式 | 类型 | 用途 |
|---------|------|------|
| `vaas:vehicle:info:<deviceId>` | List | 车辆历史坐标（经纬度,速度,时间戳） |
| `vaas:bump:counter` | Hash | 颠簸事件计数器 |
| `vaas:slip:counter` | Hash | 湿滑事件计数器 |
| `vaas:ponding:counter` | Hash | 积水事件计数器 |
| `vaas:ice:counter` | Hash | 结冰事件计数器 |
| `vaas:low-attachment:counter` | Hash | 低附着事件计数器 |
| `vaas:bump:event` | String(JSON) | 24h内颠簸事件 |
| `vaas:slip:event` | String(JSON) | 24h内湿滑事件 |
| `vaas:ice:event` | String(JSON) | 24h内结冰事件 |
| `vaas:ponding:event` | String(JSON) | 24h内积水事件 |
| `vaas:low-attachment:event` | String(JSON) | 24h内低附着事件 |
| `vaas:road:segment:coordinates` | Geo | 路段坐标 |
| `vaas:road:segment:map` | Hash | 路段ID→名称映射 |
| `vaas:event:topic` | PubSub | 事件推送主题 |
| `vaas:kt710:notifier` | PubSub | KT710通知主题 |
| `vaas:vehicle:kt` | List | KT710原始数据队列 |
| `Wsensor_<sensorId>_last24h_measurement` | String(JSON) | 气象站实时数据 |

### 5.6 核心事件类型

| eventType | 含义 | 说明 |
|-----------|------|------|
| bumpy / bump | 路面颠簸 | Z轴加速度异常 |
| slip / wet | 路面湿滑 | wetFlag 或气象数据判断 |
| ponding | 路面积水 | 降雨+排水不畅 |
| ice | 路面结冰 | 低温+高湿度 |
| low-attachment | 低附着系数 | μ值 < 0.2 |

---

## 六、质量验收标准

### 每阶段完成时的验证

| 阶段 | 验证项 |
|------|--------|
| 素材提取完成 | 文件完整、无遗漏、目录结构清晰 |
| 后端还原完成 | 可 mvn compile 通过、关键配置齐全 |
| 算法模块完成 | 单元测试覆盖正常值和边界值、与文档一致 |
| 大屏还原完成 | 路由正确、API调用正常、组件渲染无报错 |
| 管理后台完成 | 页面路由一致、CRUD功能完整 |
| 集成完成 | 模拟器数据注入 → 后端处理 → 前端展示 全链路跑通 |
