# Admin 前端还原设计

## 目标
从编译后混淆 JS 中反提取管理后台页面结构，还原与原始代码一致的 Vue 3 + Vite + Element Plus 页面。

## 还原范围（严格按反编译结果）
从反混淆 JS 中提取到的路由结构：

| 路由 | 页面 | 来源 chunk | 确认 |
|------|------|-----------|------|
| `/welcome` | 首页 | index-DgRB5vEa.js | ✅ |
| `/vaas/car-mapping` | 车辆绑定 | index-C4qJNYT_.js | ✅ |
| `/vaas/heartbeat` | 心跳管理 | index-BPMVWVoZ.js | ✅ |
| `/vaas/log` | 动态日志 | DynamicLog-D_ZUJF3P.js | ✅ |
| `/error/403/404/500` | 错误页 | 独立 chunk | ✅ |

## 技术栈
- Vue 3 + Vite 5.9.0（从 platform-config.json 确认）
- Element Plus
- 请求前缀: `/api/`（从源码提取）
- 后端代理: `/api` → localhost:50415

## 页面详情

### 1. 车辆绑定 (`/vaas/car-mapping`)
**来源:** index-C4qJNYT_.js (21KB) + hook-Jm01w6Y2.js
**搜索字段:** deviceId, kt710Id, plate, brandModel, simId, groupId, phoneNumber
**表格列:** kt710Id, plate, deviceId(imei), brandModel, bumpEnable, slipEnable, reject
**操作:** 新增, 修改, 删除, 导出Excel
**API:** `POST /admin/list`, `POST /admin/add`, `PUT /admin/update`, `DELETE /admin/delete`

### 2. 心跳管理 (`/vaas/heartbeat`)
**来源:** index-BPMVWVoZ.js (7KB) + hook-DDzwW943.js
**峰值统计:** 6 个 el-descriptions-item（KT/六轴/GPS × 峰值/当前）
**表格列:** deviceId, plate, ktLastOnlineTime, motionLastOnlineTime, locationLastOnlineTime, phoneNumber
**状态标签:** success(在线), danger(离线)
**API:** `GET /admin/heartbeat`

### 3. 动态日志 (`/vaas/log`)
**来源:** DynamicLog-D_ZUJF3P.js (6KB) + hook-D6jXWR3N.js
**功能:** 设备选择 → Enable/Disable Debug → 接收日志(WebSocket)
**WebSocket:** wss://vaas.wx-iov.com:444/ws/log-stream（原始地址，本地需改为 ws://localhost:50415/ws/log）

### 4. 首页 (`/welcome`)
**来源:** main chunk
**内容:** 纯展示型页面，含路由重定向

## 不需要还原的页面
- 系统配置、设备管理、权限管理、数据报表 → 原始代码中不存在这些路由

## 已完成的页面内容
- CarMapping.vue - 基于反编译结构重写 ✅
- Heartbeat.vue - 基于反编译结构重写 ✅
- LogViewer.vue - 基于反编译结构重写 ✅
