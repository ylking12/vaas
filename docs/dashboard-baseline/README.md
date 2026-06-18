# 原大屏深度探测基线（v2）

> 探测时间: 2026-06-18T14:40:48
> 目标: https://vaas.wx-iov.com:444/#/dashboard

## 探测概况

- HTTP 状态: 200
- 元素总数: 13
- 文本节点数: 1929
- API 响应数: 93
- 截图数: 7
- JS 错误: 0

## 产物清单

| 文件 | 用途 |
|------|------|
| `baseline-capture-v2.js` | 探测脚本（可重跑） |
| `full-ui-inventory.md` | 完整 UI 元素清单 |
| `api-fields.md` | 15+ API 字段定义 |
| `design-tokens.md` | 视觉规范（颜色/字体/间距） |
| `screenshots-summary.md` | 7 张截图 + 主题分析 |
| `interaction-state-machine.md` | 交互状态机 |
| `all-elements.json` | 元素原始数据 |
| `all-texts.json` | 文本原始数据 |
| `api-responses.json` | API 响应原始数据 |
| `api-fields.json` | API 字段结构 |
| `design-tokens.json` | CSS 变量原始数据 |
| `screenshots-analysis.json` | 截图主色分析 |
| `errors.json` | JS 错误日志 |
| `screenshots-v2/` | 7 张截图 |

## 关键发现

1. **主题色**: 深色 (#171717 底) + 洋红时间轴 (#CB2388)
2. **API 数量**: 至少 13 个独立端点（含 SSE/location）
3. **高德地图**: 1920x1076 全屏 + 400+ 车辆标记 (40x18 amap-marker)
4. **ECharts**: 历史 24h 路面状态图表
5. **左侧面板**: 实时车队 / 路网状态 / 实时气象
6. **5 个气象站**: 文惠路与锦绣路 / 先锋中路与新锡路 / 机场路-泰山路 / 高浪路-兴梁道 / 运河西路
7. **7 种事件类型**: 颠簸/湿滑/积水/结冰/低附着（24h 统计）+ 实时告警

## 下一步

1. 启动 P7 dev server，用同一份脚本访问 http://localhost:8083
2. 用 baseline-capture-v2.js 跑 P7 baseline
3. 写 diff 脚本对比原大屏 vs P7
4. 根据 diff 修复 frontend/dashboard/
5. 复测确认