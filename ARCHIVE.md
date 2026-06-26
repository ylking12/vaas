# VaaS 项目复现 — 历史存档

> 已完成迭代的详细修复记录和完整迭代报告。主任务跟踪见 [TASK_TRACKING.md](./TASK_TRACKING.md)。
> 这些记录仅作追溯参考，不反映当前项目状态。

---

## P7-iter.3 时间轴样式复原（2026-06-22）

**目标**：贴齐原版 11-timeline.png + 设计文档 §3.4

**9 处修复**：

| # | 修复点 | 之前 | 之后 |
|---|--------|------|------|
| 1 | 端点文字 | 缺 | "过去23h" / "未来1h" |
| 2 | 刻度格式 | "-23h, -22h" | 动态真实小时 HH:00 |
| 3 | 默认位置 | 13 (-11h) | 24 (Now) |
| 4 | 跑道渐变 | 4 色 | 6 色全光谱 |
| 5 | 滑块图标 | slider-btn.png（AI 图标）| CSS ::before 三条垂直灰线 |
| 6 | 滑块形状 | 直角矩形 | 圆角胶囊（border-radius: 8px）|
| 7 | marks 位置 | margin-top: 2px（压跑道）| 15px（跑道下方，Element Plus 默认）|
| 8 | 外框 | 无 | 黑底圆角浮层（width:70% + 黑底 + 圆角 4px）|
| 9 | text-shadow | 有（早期尝试抗干扰）| 去掉（黑底外框已提供对比度）|

**参照源**：
- 原版 [`docs/dashboard-baseline/screenshots-v2/11-timeline.png`](docs/dashboard-baseline/screenshots-v2/11-timeline.png)（vision 描述深色主题）
- 设计文档 [`docs/plans/2026-06-11-dashboard-redesign.md`](docs/plans/2026-06-11-dashboard-redesign.md) §3.4
- baseline 摘要 [`docs/dashboard-baseline/screenshots-summary.md`](docs/dashboard-baseline/screenshots-summary.md)

**自检结果**：✅ npm run build 通过（4.76s），playwright + vision 验证通过

**演进历程**（用户反馈驱动）：
- v1：黑底外框 + 圆角 + 端点文字 → 用户："外框有点问题"
- v2：去掉外框（透明 bg + 32px）→ 用户："有点意思了，很接近"
- v3：v2 + 加深羽化遮罩 → 用户："不太对不如刚才那版"
- v4-v5：v2 + text-shadow 描边 → 文字仍轻微干扰
- **v6（最终）**：恢复 v1 外框 + 保留后续改进 → vision：「完美对齐原版」

**关键教训**：用户纠正过我对 11-timeline.png 的"无外框"误判——该截图是被裁剪的纯时间轴区域，外层黑底浮层被裁掉了。**判断组件结构必须看完整大屏截图或在线原版，不能仅凭局部裁剪图**。

---

## P7-iter.6 flood 按钮 toggle 语义修复（2026-06-23）

**问题**：选中"路面积水颠簸事件"按钮后地图显示 5 个事件 marker；再次点击取消时 marker 不消失。

**根因**：`toggleLayer` 的 `isSpecial` 分支只处理了"选中→拉数据"，取消分支什么都不做，违反 toggle 按钮标准语义。

**修复**（git b2e401c）：
- `MapView.vue` 新增 `clearEventMarkers()`：清空 marker + 重置状态
- `defineExpose` 暴露方法
- `toggleLayer` 取消分支调 `mapViewRef.value?.clearEventMarkers()`

**保留的语义**：关闭 drawer 不清空地图内容；重新打开 drawer 状态保留

**验证**（playwright 5 阶段）：

| 阶段 | 操作 | event-marker | 结果 |
|------|------|:------------:|:----:|
| 默认进入 | — | 0 | ✅ |
| 打开 drawer（flood 未选）| — | 0 | ✅ |
| 选中 flood | click | 5 | ✅ |
| 取消 flood | click | 0 | ✅ |
| 重新选中 | click | 5 | ✅ |

---

## P7-iter.7 网联车 marker 显示修复 + 位置仿真器（2026-06-23）

**问题**：点"联网车辆"按钮后侧栏计数 = 5 但地图无 marker；仿真器看不出来车在移动。

**根因**（三层）：
1. Redis 5 个 key 的 timestamp 是 2026-06-12，被 `getOnlineVehicles()` 的 60s 检查过滤掉
2. `loadOnlineVehicles()` 只更新 `onlineCount`，**没有**把数据传给地图
3. `toggleVehicles` 没有 timer 持续刷新

**修复**：
- 字段归一化 + `mapViewRef.value?.addVehicleMarkers(vehicles)` 打通数据
- `toggleVehicles` 开启时启动 `vehicleTimer`（5s 间隔），关闭时清除
- `onBeforeUnmount` 清理 timer

**新增仿真器**（`simulator/python/vehicle_location_simulator.py`）：
- 每 5s 给 5 个 key 推一次位置
- 模拟 detector4kt 推送语义：`RPUSH` + `LTRIM -1 -1`
- 步长 30m/5s，5 辆车各绑定一个轴向 + 初始方向

**为什么不是 mock**：仿真器只往 Redis 写数据，**不动任何业务代码**。

**验证**（playwright，`/location` API lng/lat 比对）：

| 阶段 | 期望 | 实际 |
|------|:----:|:----:|
| `/location` API | 5 | 5 ✅ |
| 点"联网车辆" → marker | 5 | 5 ✅ |
| 取消 → marker 消失 | 0 | 0 ✅ |
| 12s 内位置移动 | 是 | 是 ✅ |

**关键教训**：离线 timestamp 会被业务逻辑踢出"在线"→ 仿真器必须写入实时时间戳。

---

## P7-iter.8 原版 PNG 图标替换（2026-06-24）

**问题**：P7-iter.2.2 时用了内嵌 SVG 自绘图标，与原版视觉差异较大。

**调研发现**：
- 原版 dashboard JS 引用了 ~16 个 marker PNG
- PNG 全部被 webpack url-loader inline 成 base64 嵌入 JS 模块定义中
- 5 个大尺寸背景图走 file-loader，存在 `www/img/`

**实施**：

1. **提取脚本** [scripts/extract-original-icons.js](scripts/extract-original-icons.js)：
   - 按 module key 切段，每段独立匹配 eval 内容（规避 regex 跨段错位）
   - 同时处理 base64 inline + file-loader 路径
   - 29/29 图标全部提取成功

2. **提取产物** → [frontend/dashboard/src/assets/img/](frontend/dashboard/src/assets/img/)：
   - 车辆 ×2、event ×9、roadside ×3、layer ×6、其它 ×9

3. **改造 MapView.vue**：
   - 删除内嵌 SVG 字符串和脉冲动画 CSS
   - 改用 `import` 引入原版 PNG
   - 4 类 marker 按 type/level 映射对应图标

4. **新增 vite 别名** `@/`，沿用原版 Vue 2 import 风格

**验证**（playwright）：

| marker | 数量 | 原版尺寸 | 结果 |
|--------|:----:|:---------:|:----:|
| 车辆 | 5/5 | 18×40 | ✅ |
| 气象站 | 5/5 | 80×80 | ✅ |
| 降水点 | 5/5 | 80×80 | ✅ |
| 构建 | — | — | ✅ 2.78s |

**已知差异**：
- 气象站固定用 DSC211 图标（后续 API 返回 type 字段可自动切换）
- 颠簸 Level 映射规则按算法阈值推测，未严格交叉验证

**关键教训**：`document.createElement` 创建的 DOM，Vue scoped CSS 不作用 → marker img 尺寸必须走 inline style。

---

## B1 时间轴联动失效修复详情（2026-06-22）

**问题**：拖动时间轴，地图事件标记不刷新（始终显示过去 23h 所有事件）。

**根因**：
- `loadMapEvents()` 调接口传 `{}` 空 body，后端 `hour` 默认为 0（查全部）
- `watch(sliderValue)` 错误地触发了 `loadAlarmList()` 而不是 `loadMapEvents()`
- 最初基于设计文档 §2.2 字面意思错误推断联动方向为"告警列表"

**修复**：
```js
// watch(sliderValue) 正确方向：
watch(sliderValue, () => loadMapEvents()) // 联动地图事件标记
// hour 计算：
const hour = Math.max(0, Math.abs(24 - sliderValue))
// sliderValue=24(Now) → hour=0 → 后端查 [now-23h, now]
// sliderValue=1(-23h) → hour=23 → 后端查 [now-23h, now-22h]
```

**4 个 commits**：
- 94cdfeb Revert 错误方向
- 3d244ac 正确方向修复
- 877515e 移除空数据守卫（否则旧标记不清除）

**教训**：设计文档 §2.2 第 68 行"联动刷新事件数据和告警列表"有歧义：正确理解是"事件数据"= 地图事件标记（§3.5），告警列表不联动。**凭文档字面意思推断功能方向不可靠，必须和用户实际意图核对**。

---

## Drawer 弹框遮盖修复详情（2026-06-23）

**依据**（直接访问原版 https://vaas.wx-iov.com:444 抓 DOM）：
- drawer 宽 = 1690/1920 = **88%**（不是 100% 全屏）
- 背景 = `rgba(0,0,0,0.6)` 半透明黑（不是实心）
- 右侧留 230px 地图可见
- 三栏宽 422/634/634（不是 240/flex:1/260）

**修复**：
- `el-drawer: direction ltr→rtl, size 100%→88%, :modal='false'`
- `.el-drawer background: #090909 → rgba(0,0,0,0.6)`
- 三栏宽对齐

**教训**：
- vision 看 baseline 截图估算的"60-70% 宽"不准（分辨率/缩放误差）
- 设计文档 §2.1/§3.3 的"全屏"描述错误，原版实际 88% 宽
- **playwright 直接访问原版获取 DOM 数据是最权威参照**

---

## 方案 A' / A'' 交互演变（2026-06-23）

### A'：hover/click 都打开 drawer

用户反馈：左侧"实时数据"按钮同时出现"小弹窗 + 大弹窗"。要求移除小弹窗，hover/click 都直接打开大弹窗。

**修改**：
- `@mouseenter='panelExpanded = true'` → `@mouseenter='openDrawer'`
- 删除 `@mouseleave` + `panelExpanded` ref + `togglePanel` 函数
- 删除 `.expanded` 相关 CSS

### A''：hover 不响应，click toggle

用户反馈："实时数据应该通过点击控制弹框显示隐藏，而不是鼠标移入"

**修改**：
- `@mouseenter='openDrawer'` → 删掉（hover 不触发）
- `@click='openDrawer'` → `@click='toggleDrawer'`
- `toggleDrawer()`：切换 `drawerVisible`

**最终行为**：

| 操作 | drawer |
|------|:------:|
| 默认 | 无 |
| hover left-panel | 无（不响应）|
| click "实时数据"(1st) | 打开 |
| click "实时数据"(2nd) | 关闭 |

**原始差异**：原版是 hover 展开 288px 小弹窗，A'' 是 click toggle 1690 drawer。按用户明确要求执行。

---

## P7 修复记录（2026-06-12）

**样式修复**：
- ✅ 全局配色：蓝色调 → 棕/金色调
- ✅ 抽屉背景：#0a1628 → #1a1a1a
- ✅ 图层选中：蓝色半透明 → 棕→金渐变
- ✅ 统计数值：蓝色 → 金色 #FFF6DA
- ✅ 表格/按钮：蓝色系 → 棕色系

**功能修复**：
- ✅ 养护建议列表（颠簸/湿滑/积水路段）
- ✅ 时间轴联动刷新
- ✅ 事件删除调 API
- ✅ 图层叠加效果（圆形覆盖层）
- ✅ 数据接入：联网车辆数 + 降水量

**数据修复**：注入更多测试事件（13 颠簸 + 7 湿滑 + 3 积水）

---

## P7-iter.1 数据接入小项（2026-06-12）

**问题**：实时车队数据只显示"联网车辆"文字，无数值；实时气象数据只显示"降水量"文字，无数值。

**后端支持**（无需改动）：
- `/location` 返回的 key 数量 = 在线车辆数
- `/get_weather.precip` = 降水量

**实施**：
- `api/index.js` 新增 `getOnlineVehicles()`
- `DashboardPage.vue` 新增 `onlineCount` + `precipText` + 15min 定时器
- 4 处模板同步显示数值

**取消项**：
- ❌ 车队分类切换（全部/出租车/巴士）—— 用户实地观察原版大屏确认**无此功能**，设计文档 §2.2 描述有误

---

## P8 验证结果

- ✅ 5 个新/改脚本语法全部通过 (`bash -n`)
- ✅ 3 个新脚本已 `chmod +x`
- ✅ pre-commit hook 已写入 `.githooks/`
- ✅ 5 个后端微服务 `mvn compile -DskipTests` 全部通过
- ✅ OpenAPI / Actuator 集成无编译错误

**部署 pre-commit hook**（项目根目录执行一次）：
```bash
git config core.hooksPath .githooks
```

**可访问**（启动后）：
- OpenAPI JSON: `http://localhost:50410/v3/api-docs`
- Swagger UI: `http://localhost:50410/swagger-ui.html`
- 健康检查: `http://localhost:50410/actuator/health`
