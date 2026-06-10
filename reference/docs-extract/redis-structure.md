# VaaS Redis 数据结构定义

> 来源: 从代码反编译还原
> 更新日期: 2026-06-10

## 一、Redis 部署模式

| 服务 | 模式 | 说明 |
|------|------|------|
| receiver | 单机 (host:6379) | Redis 单节点 |
| vaas-backend | 单机 (host:6379) | Redis 单节点 |
| detector4kt | 单机 (host:6379) | Redis 单节点 |
| detector4motion | **集群** (7000/7001) | Redis Cluster |
| admin-api | 单机 (host:6379) | Redis 单节点 |

## 二、Key 命名规范

所有业务 key 以 `vaas:` 为前缀，分类如下:

### 2.1 数据结构 Key

| Key 模式 | 类型 | 用途 |
|---------|------|------|
| `vaas:vehicle:info:<deviceId>` | List | 车辆历史坐标（经纬度,速度,时间戳） |
| `vaas:vehicle:kt` | List | KT710 原始数据队列 |
| `vaas:debug:device` | PubSub | 设备调试信息推送主题 |
| `vaas:log` | PubSub | 日志推送主题 |

### 2.2 事件计数器 Key (Hash)

| Key | 字段 | 用途 |
|-----|------|------|
| `vaas:bump:counter` | deviceId → count | 颠簸事件计数器 |
| `vaas:slip:counter` | deviceId → count | 湿滑事件计数器 |
| `vaas:ponding:counter` | deviceId → count | 积水事件计数器 |
| `vaas:ice:counter` | deviceId → count | 结冰事件计数器 |
| `vaas:low-attachment:counter` | deviceId → count | 低附着事件计数器 |

### 2.3 事件缓存 Key (String/JSON)

| Key | 类型 | 用途 |
|-----|------|------|
| `vaas:bump:event` | String(JSON) | 24h 内颠簸事件 |
| `vaas:slip:event` | String(JSON) | 24h 内湿滑事件 |
| `vaas:ice:event` | String(JSON) | 24h 内结冰事件 |
| `vaas:ponding:event` | String(JSON) | 24h 内积水事件 |
| `vaas:low-attachment:event` | String(JSON) | 24h 内低附着事件 |

### 2.4 地理空间 Key

| Key | 类型 | 用途 |
|-----|------|------|
| `vaas:road:segment:coordinates` | Geo | 路段坐标 |
| `vaas:road:segment:map` | Hash | 路段ID → 名称映射 |

### 2.5 队列 Key (List)

| Key | 用途 |
|-----|------|
| `kt710:queue:<groupId>` | KT710 数据队列 (按分组) |
| `vaas:motion:queue:<groupId>` | 运动数据队列 (按分组) |

### 2.6 PubSub 通道

| Channel | 用途 | 发布者 | 订阅者 |
|---------|------|--------|--------|
| `vaas:event:topic` | 事件推送 | vaas-backend | 前端SSE |
| `vaas:kt710:notifier` | KT710 通知 | receiver | detector4kt |
| `vaas:motion:notifier` | 运动数据通知 | receiver | detector4motion |
| `vaas:log` | 日志推送 | 各服务 | admin-api |
| `vaas:debug:device` | 设备调试 | admin-api | 设备 |

### 2.7 心跳统计 Key (String)

| Key | 类型 | 用途 |
|-----|------|------|
| `vaas:stat:max-kt-on` | String(Int) | KT 最大在线数统计 |
| `vaas:stat:max-motion-on` | String(Int) | Motion 最大在线数统计 |
| `vaas:stat:max-location-on` | String(Int) | Location 最大在线数统计 |
| `vaas:heartbeat:kt` | Hash | KT 设备心跳 (deviceId → timestamp) |
| `vaas:heartbeat:motion` | Hash | Motion 设备心跳 (deviceId → timestamp) |
| `vaas:heartbeat:location` | Hash | Location 设备心跳 (deviceId → timestamp) |

### 2.8 气象传感器 Key (String/JSON)

| Key 模式 | 类型 | 用途 |
|---------|------|------|
| `Wsensor_<sensorId>_last24h_measurement` | String(JSON) | 传感器24h测量数据 |
| `Wsensor_<sensorId>_last24h_event` | String(JSON) | 传感器24h事件数据 |

## 三、各服务使用的 Redis Key

### receiver 服务

| Key | 操作 |
|-----|------|
| `vaas:vehicle:info:<deviceId>` | List: leftPush (写GPS坐标) |
| `kt710:queue:<groupId>` | List: rightPush (写KT710数据) |
| `vaas:motion:queue:<groupId>` | List: rightPush (写运动数据) |

### vaas-backend 服务

| Key | 操作 |
|-----|------|
| `vaas:vehicle:info:<deviceId>` | List: range/trim (读取GPS坐标) |
| `vaas:bump:counter` | Hash: increment (颠簸计数) |
| `vaas:slip:counter` | Hash: increment (湿滑计数) |
| `vaas:bump:event` | ZSet: add (事件缓存) |
| `vaas:slip:event` | ZSet: add (事件缓存) |
| `vaas:ice:event` | ZSet: add (事件缓存) |
| `vaas:ponding:event` | ZSet: add (事件缓存) |
| `vaas:low-attachment:event` | ZSet: add (事件缓存) |
| `vaas:event:topic` | PubSub (SSE推送) |
| `Wsensor_*` | String (传感器数据) |

### detector4kt 服务

| Key | 操作 |
|-----|------|
| `kt710:queue:<groupId>` | List: leftPop (消费KT710数据) |
| `vaas:bump:event` | ZSet: add (写入颠簸事件) |
| `vaas:slip:event` | ZSet: add (写入湿滑事件) |
| `vaas:kt710:notifier` | PubSub: subscribe |

### detector4motion 服务

| Key | 操作 |
|-----|------|
| `vaas:motion:queue:<groupId>` | List: leftPop (消费运动数据) |
| `vaas:motion:notifier` | PubSub: subscribe |

### admin-api 服务

| Key | 操作 |
|-----|------|
| `vaas:heartbeat:kt` | Hash: entries/get |
| `vaas:heartbeat:motion` | Hash: entries/get |
| `vaas:heartbeat:location` | Hash: entries/get |
| `vaas:stat:max-kt-on` | String: get/set |
| `vaas:stat:max-motion-on` | String: get/set |
| `vaas:stat:max-location-on` | String: get/set |
| `vaas:log` | PubSub: subscribe |
| `vaas:debug:device` | PubSub: publish |
