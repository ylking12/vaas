## 1. 元素统计对比

| 类型 | 原大屏 | P7 |
|------|--------|----|
| amap | 9 | 7 |
| svg | 0 | 1 |
| canvas | 1 | 1 |
| interactive | 3 | 4 |

## 2. API 端点对比

| API | 原大屏 | P7 |
|-----|--------|----|
| `/spring/v1/get-alarm-list` | ✅ | ✅ |
| `/spring/v1/get-event-summary` | ✅ | ✅ |
| `/spring/v1/get-last-24h-bump-event` | ✅ | ✅ |
| `/spring/v1/get-last-24h-ice-event` | ✅ | ❌ |
| `/spring/v1/get-last-24h-low-attachment-event` | ✅ | ❌ |
| `/spring/v1/get-last-24h-ponding-event` | ✅ | ❌ |
| `/spring/v1/get-last-24h-slip-event` | ✅ | ✅ |
| `/spring/v1/get-rain-points` | ✅ | ❌ |
| `/spring/v1/get_covered_range` | ✅ | ✅ |
| `/spring/v1/get_last24h_data_plot` | ✅ | ✅ |
| `/spring/v1/get_real_time_sensor_data` | ✅ | ✅ |
| `/spring/v1/get_weather` | ✅ | ✅ |
| `/spring/v1/location` | ✅ | ✅ |
| `https://jsapi.amap.com/web/init?key=ba8f650d9f48ac56556e2858bc1499ad` | ❌ | ✅ |
| `https://jsapi.amap.com/web_map/get_tile?key=ba8f650d9f48ac56556e2858bc1499ad` | ❌ | ✅ |

## 3. 文本差异（前 30）

### 原大屏有但 P7 缺失（关键功能文本）

- ❌ `机场路`
- ❌ `高浪路`
- ❌ `运河西路`
- ❌ `附着系数`
- ❌ `路面温度`
- ❌ `干湿状态`
- ❌ `空气温度`
- ❌ `相对湿度`
- ❌ `导出`
- ❌ `道路实时路况`
- ❌ `积水`

### P7 有但原大屏没有（多余）

- ➕ `0.0 mm`
- ➕ `1h`
- ➕ `13`

### 共有文本（功能对齐）

- ✅ `Now`
- ✅ `© 2026 AutoNavi`
- ✅ `实时数据`
- ✅ `实时气象数据`
- ✅ `实时车队数据`
- ✅ `恶劣天气道路路面状态感知与预测系统`
- ✅ `未来1h`
- ✅ `查看气象设备`
- ✅ `联网车辆`
- ✅ `路网状态`
- ✅ `路面干湿状态图层`
- ✅ `路面温度状态图层`
- ✅ `路面积水颠簸事件`
- ✅ `路面附着系数图层`
- ✅ `过去23h`
- ✅ `返回`
- ✅ `降水量`

## 4. 视觉规范对比

| 属性 | 原大屏 | P7 |
|------|--------|----|
| font | `12px / 13.8px "Noto Sans SC"` | `16px "Noto Sans SC", "PingFang SC", "Microsoft YaHei", sans-` |
| fontSize | `12px` | `16px` |
| fontFamily | `"Noto Sans SC"` | `"Noto Sans SC", "PingFang SC", "Microsoft YaHei", sans-serif` |
| color | `rgb(0, 0, 0)` | `rgb(0, 0, 0)` |
| background | `rgba(0, 0, 0, 0)` | `rgba(0, 0, 0, 0)` |
| lineHeight | `13.8px` | `` |

## 5. 截图主题色对比（默认状态）

| 状态 | 原大屏 | P7 |
|------|--------|----|
| 默认（未加载数据）| #F1F1F1（浅色）| #313131（深色）|
| 数据加载后 | #171717（深色）| #2B2B2B（深色）|
| 内容面板 | #090909（更深）| #1A1A1A（深色）|

**结论：P7 是深色主题，原大屏默认也是深色（仅初始未加载时浅色）。主题色一致。**

## 6. 关键差异总结

- 文本节点: 原大屏 629 vs P7 20（差 609 个未在 P7 看到）
- API 端点: 原大屏 13 vs P7 11
- 元素类型: 原大屏 3 种 vs P7 4 种

### 主要差异

1. **内容面板元素缺失**：P7 内容面板里没看到原大屏的：
   - 5 个气象站下拉（文惠路/先锋中路/机场路/高浪路/运河西路）
   - 历史 24h 路面状态图表（附着系数/路面温度/干湿状态）
   - 道路实时路况下拉切换
   - 告警列表（路名/告警源/告警类型/时间）
   - 服务统计数据
   - "导出"按钮

2. **24h 事件**：原大屏有 6 个独立端点（颠簸/湿滑/积水/结冰/低附着/通用）
   - P7 探测到 12 个 API，但没具体看是否包含这 6 个 24h 端点