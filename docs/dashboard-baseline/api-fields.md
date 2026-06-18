# 原大屏 API 字段定义（v2 深度探测）

> 共捕获 93 个 API 响应

## `/spring/v1/get_last24h_data_plot`
- Status: 200, Size: 714B

## `/spring/v1/get_real_time_sensor_data`
- Status: 200, Size: 241B
- 顶层字段: `meanWindSpeed`, `airTemperature`, `visibility`, `roadSurfaceTemperature`, `waterLayerThickness`, `roadStatus`, `rainIntensity`, `relativeHumidity`, `meanWindDirection`, `levelOfGrip`, `ts`

## `/spring/v1/get_covered_range`
- Status: 200, Size: 22B

## `/spring/v1/get-event-summary`
- Status: 200, Size: 1091B
- 顶层字段: `water_road_amount`, `bumpy_road_amount`, `water_road_to_maintain`, `slippery_road_amount`, `slippery_road_to_maintain`, `bumpy_road_to_maintain`

## `/spring/v1/location`
- Status: 200, Size: 1302B
- 顶层字段: `865522079540107`, `865522079539612`, `865522079536998`, `865522079537145`, `865522079492333`, `865522079541451`

## `/spring/v1/get_weather`
- Status: 200, Size: 459B
- 顶层字段: `status`, `message`, `data`
- `data` 是对象，字段: `id`, `districtName`, `districtId`, `requestTime`, `updateTime`, `obsTime`, `temp`, `feelsLike`, `icon`, `text`, `wind360`, `windDir`, `windScale`, `windSpeed`, `humidity`, `precip`, `pressure`, `vis`, `cloud`, `dew`, `sources`, `license`

## `/spring/v1/get-alarm-list`
- Status: 200, Size: 36568B

## `/spring/v1/get-rain-points`
- Status: 200, Size: 290B
- 顶层字段: `status`, `message`, `data`
- `data` 是数组（5 条），元素字段: `name`, `longitude`, `latitude`

## `/spring/v1/get-last-24h-ponding-event`
- Status: 200, Size: 2B

## `/spring/v1/get-last-24h-bump-event`
- Status: 200, Size: 51643B

## `/spring/v1/get-last-24h-ice-event`
- Status: 200, Size: 2B

## `/spring/v1/get-last-24h-low-attachment-event`
- Status: 200, Size: 2B

## `/spring/v1/get-last-24h-slip-event`
- Status: 200, Size: 415B
