# 原大屏交互探测报告

> 探测时间: 2026-06-18T06:30:09.462Z
> 目标 URL: https://vaas.wx-iov.com:444/#/dashboard

## 1. 访问结果

[1] 访问 https://vaas.wx-iov.com:444/#/dashboard
[2] HTTP 状态: 200
[3] 📸 01-default-state.png
[4] 📸 01-default-fullpage.png (fullPage)
[5] 发现 1 个可见交互元素
[6] 发现 19 个标题/面板名
[7] 📸 02-after-click-实时数据.png
[8] 点击"实时数据"后截图
[9] 共点击 0 个按钮
[10] 📸 04-map-only.png (地图单独截图)
[11] ✅ 无 JS 错误
[12] 记录到 15 个后端 API 调用

## 2. 错误汇总

✅ 无错误

## 3. 后端 API 调用

```json
[
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-alarm-list",
    "status": 200,
    "size": 4299
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-event-summary",
    "status": 200,
    "size": 1210
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get_real_time_sensor_data",
    "status": 200,
    "size": 496
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get_last24h_data_plot",
    "status": 200,
    "size": 469
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get_covered_range",
    "status": 200,
    "size": 342
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get_weather",
    "status": 200,
    "size": 638
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-rain-points",
    "status": 200,
    "size": 455
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/location",
    "status": 200,
    "size": 681
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/location",
    "status": 200,
    "size": 680
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-last-24h-slip-event",
    "status": 200,
    "size": 511
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-last-24h-bump-event",
    "status": 200,
    "size": 10659
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-last-24h-ponding-event",
    "status": 200,
    "size": 322
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-last-24h-ice-event",
    "status": 200,
    "size": 322
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/get-last-24h-low-attachment-event",
    "status": 200,
    "size": 322
  },
  {
    "name": "https://vaas.wx-iov.com:444/spring/v1/location",
    "status": 200,
    "size": 679
  }
]
```

## 4. 产物清单

- screenshots/ — 所有截图
- interactives.json — 交互元素清单
- headings.json — 标题/面板名清单
- api-calls.json — 后端 API 调用清单
