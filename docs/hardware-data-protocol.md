# 车端硬件数据协议（VaaS）

---

## 一、服务清单

| # | 服务 | 入口协议 | 入口路径 | 频率 | 产出事件 |
|---|------|---------|---------|------|---------|
| 1 | **detector4kt** | HTTP POST | `/kt-data` | 5–10 Hz | 颠簸 / 湿滑 |
| 2 | **detector4motion** | WebSocket | `/ws/motion` | 10 Hz | 6 轴颠簸（Level 3/5/7）|
| 3 | **vaas-backend 事件处理** | WebSocket | `/ws/location` | 10 Hz | 事件地图定位 |

> 鉴权：所有入口的 `sn` / `deviceId` 必须预注册到 `fleet_management` 表，未注册则数据被静默丢弃。

---

## 二、detector4kt（颠簸 + 湿滑）

### 2.1 字段清单（stream_data）

| 字段名 | 数据类型 | 含义 | 单位 | 范围 | 必填 | 用途 |
|--------|----------|------|------|------|------|------|
| `VehicleSpd( km/h)` | String | 车速 | km/h | ≥ 0 | 是 | 颠簸判定（车速 ≥ 9.7 km/h 才参与）/ 湿滑滑移率分母 |
| `SteerWheelAngle( deg)` | String | 方向盘转角（左负右正）| 度 | -540 ~ +540 | 是 | 颠簸左右轮速比 / 转向角比率 |
| `LateralAcce( m/s2)` | String | 横向加速度 | m/s2 | -50 ~ +50 | 是 | 附着系数 μ 计算（平方项）|
| `LongitudeAcc( m/s2)` | String | 纵向加速度 | m/s2 | -50 ~ +50 | 是 | 附着系数 μ 计算（平方项）|
| `ESC_Mcylinder_Pressure( bar)` | String | ESC 主缸制动压力 | bar | ≥ 0 | 是 | 颠簸判定（< 1.0 bar 才触发）|
| `FLWheelSpd( km/h)` | String | 左前轮速 | km/h | ≥ 0 | 是 | 左轮速差 / 滑移率 |
| `FRWheelSpd( km/h)` | String | 右前轮速 | km/h | ≥ 0 | 是 | 右轮速差 / 滑移率 |
| `RLWheelSpd( km/h)` | String | 左后轮速 | km/h | ≥ 0 | 是 | 左轮速差 / 滑移率 |
| `RRWheelSpd( km/h)` | String | 右后轮速 | km/h | ≥ 0 | 是 | 右轮速差 / 滑移率 |
| `WiperFlag` | String | 雨刷状态 | — | On/Off | 否 | 当前算法未消费（预留）|

### 2.2 顶层容器字段

| 字段 | 数据类型 | 必填 | 说明 |
|------|----------|------|------|
| `data` | Array | ✅ | 支持批量上传多帧 |
| `data[].sn` | String | ✅ | KT710 SN；必须注册 |
| `data[].date` | String | ✅ | 格式 `YYYY_MM_DD_HH_MM_SS_mmm`，UTC-0 |
| `data[].stream_data` | Array<{name:String, value:String}> | ✅ | 上述字段 |
| `packageSize` | Int | ❌ | 数据包数量 |

> ⚠️ **丢帧机制**：9 个必填 `stream_data` 的 `value` 必须是合法数字字符串（解析失败 = `Float.MIN_VALUE` ≈ 1.4E-45），任一字段解析失败即**整帧丢弃**。

### 2.3 Payload 示例

```json
{
  "data": [
    {
      "date": "2025_08_15_13_36_11_781",
      "sn": "9040345325",
      "stream_data": [
        { "name": "VehicleSpd( km/h)",            "value": "0.1" },
        { "name": "FLWheelSpd( km/h)",            "value": "0.0" },
        { "name": "FRWheelSpd( km/h)",            "value": "0.0" },
        { "name": "RLWheelSpd( km/h)",            "value": "0.0" },
        { "name": "RRWheelSpd( km/h)",            "value": "0.0" },
        { "name": "ESC_Mcylinder_Pressure( bar)","value": "0.0" },
        { "name": "LateralAcce( m/s2)",           "value": "0.05" },
        { "name": "LongitudeAcc( m/s2)",          "value": "0.03" },
        { "name": "SteerWheelAngle( deg)",        "value": "-4.50" },
        { "name": "WiperFlag",                    "value": "Off" }
      ]
    }
  ],
  "packageSize": 1
}
```

---

## 三、detector4motion（6 轴颠簸）

### 3.1 字段清单

| 字段名 | 数据类型 | 含义 | 单位 | 范围 | 必填 | 用途 |
|--------|----------|------|------|------|------|------|
| `deviceId` | String | 设备 IMEI | — | — | 是 | 设备分组 / 鉴权 |
| `timestamp` | Long | 设备采集时间戳 | 毫秒 | Unix epoch | 是 | 时间窗对齐 |
| `ax` | Double | 前后加速度 | m/s2 | -5 ~ +5 | 是 | 占位（当前算法未消费，建议仍发送）|
| `ay` | Double | 左右加速度 | m/s2 | -5 ~ +5 | 是 | 振幅统计（Level 3 ≥ 0.23，Level 5/7 ≥ 0.25）|
| `az` | Double | 上下加速度 | m/s2 | -5 ~ +5 | 是 | 振幅统计 + 异常值过滤（> 2.5 整帧丢弃）|
| `wx` | Double | 前后角速度 | rad/s | — | 是 | 静止检测（== 0 连续 3 次清缓存）|
| `wy` | Double | 左右角速度 | rad/s | — | 是 | 占位（当前算法未消费，建议仍发送）|
| `wz` | Double | 上下角速度 | rad/s | — | 是 | 占位（当前算法未消费，建议仍发送）|

> ⚠️ **字段名大小写**：协议字段名一律**小写**（`ax/ay/az/wx/wy/wz`），反编译 DTO 注解显示的大写 `aX/aY/aZ` 不可信。

### 3.2 Payload 示例

```json
{
  "deviceId": "868000000000001",
  "timestamp": 1753348112985,
  "ax": 0.01,
  "ay": 0.02,
  "az": 9.81,
  "wx": 0.0,
  "wy": 0.0,
  "wz": 0.0
}
```

---

## 四、vaas-backend 事件处理（GPS 定位）

### 4.1 字段清单

| 字段名 | 数据类型 | 含义 | 单位 | 范围 | 必填 | 用途 |
|--------|----------|------|------|------|------|------|
| `deviceId` | String | 设备 IMEI | — | — | 是 | 设备分组 / 鉴权 |
| `longitude` | Double | 经度（正东）| 度 | -180 ~ 180 | 是 | 事件地图定位 |
| `latitude` | Double | 纬度（正北）| 度 | -90 ~ 90 | 是 | 事件地图定位 |
| `timestamp` | Long | 设备采集时间戳 | 毫秒 | Unix epoch | 是 | 速度计算（≥ 1000ms 间隔）|

### 4.2 Payload 示例

```json
{
  "deviceId": "868000000000000",
  "longitude": 121.283131,
  "latitude": 31.78321798,
  "timestamp": 1753348112985
}
```
