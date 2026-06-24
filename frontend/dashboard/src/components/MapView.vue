<template>
  <div ref="mapContainer" class="map-wrapper"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// P7-iter.8: 原版 PNG 图标资源（来自 scripts/extract-original-icons.js 从原版 JS bundle 提取）
//   车辆: car.png（默认/未确认）, car_true.png（第三方车队真实车辆）
//   事件: bumpy/slippery/water_2_gray.png（基础灰色），bump-icon-3/5/7 颠簸分级
//   降水: event_marker_icon_06.png（rainIcon）
//   气象站/路侧: roadside_marker_icon_dsc211.png, event_marker_icon_sr50a.png, event_marker_icon_wxt536.png
import carIcon from '@/assets/img/car.png'
import carTrueIcon from '@/assets/img/car_true.png'
import bumpyIcon from '@/assets/img/event/event_marker_icon_bumpy_2_gray.png'
import bumpyLevel3 from '@/assets/img/event/bump-icon-3.png'
import bumpyLevel5 from '@/assets/img/event/bump-icon-5.png'
import bumpyLevel7 from '@/assets/img/event/bump-icon-7.png'
import slipperyIcon from '@/assets/img/event/event_marker_icon_slippery_2_gray.png'
import waterIcon from '@/assets/img/event/event_marker_icon_water_2_gray.png'
import rainIcon from '@/assets/img/event/event_marker_icon_06.png'
import stationDSC211 from '@/assets/img/roadside/roadside_marker_icon_dsc211.png'
import stationSR50A from '@/assets/img/roadside/event_marker_icon_sr50a.png'
import stationWXT536 from '@/assets/img/roadside/event_marker_icon_wxt536.png'

const mapContainer = ref(null)
const emit = defineEmits(['map-ready'])

let map = null
let AMap = null
let vehicleMarkers = []
let eventMarkers = []
let stationMarkers = []

const MAP_KEY = import.meta.env.VITE_MAP_KEY
const MAP_SECRET = import.meta.env.VITE_MAP_SECRET
const center = (import.meta.env.VITE_MAP_CENTER || '120.45,31.59').split(',').map(Number)
const zoom = Number(import.meta.env.VITE_MAP_ZOOM || 12)

const STATIONS = [
  { id: 1, name: '文惠路与锦绣路', pos: [120.35, 31.55] },
  { id: 2, name: '先锋中路与新锡路', pos: [120.40, 31.52] },
  { id: 3, name: '机场路-泰山路', pos: [120.42, 31.50] },
  { id: 4, name: '高浪路-兴梁道', pos: [120.45, 31.48] },
  { id: 5, name: '运河西路', pos: [120.38, 31.54] }
]

function loadAMap() {
  return new Promise((resolve, reject) => {
    if (window.AMap) return resolve(window.AMap)
    window._AMapSecurityConfig = { securityJsCode: MAP_SECRET }
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${MAP_KEY}`
    script.onload = () => resolve(window.AMap)
    script.onerror = reject
    document.head.appendChild(script)
  })
}

// P7-iter.8: 原版 marker 用 PNG 图标，不再用内嵌 SVG
//   - 车辆: 90×90 → 显示 36×36（缩小 0.4x，避免遮盖地图）
//   - 事件: 80×80 → 显示 32×32
//   - 气象站: 80×80 → 显示 32×32

// 颠簸 level → 图标映射（按算法等级）
//   bump-icon-3/5/7.png 是 12×12 等级条，用于事件列表标签，不适合作 marker
//   marker 统一用 event_marker_icon_bumpy_2_gray.png（80×80），level 只作 tooltip 信息
function getBumpyIconByLevel(_level) {
  return bumpyIcon
}

// 事件类型 → 图标映射
//   bump=颠簸（按 level 分级）, slip=湿滑, ponding=积水
//   ice/low_attachment 等扩展类型暂用 slippery 兼容
function getEventIcon(eventType, level) {
  switch (eventType) {
    case 'bump': return getBumpyIconByLevel(level)
    case 'slip': return slipperyIcon
    case 'ponding': return waterIcon
    case 'ice':
    case 'low_attachment': return slipperyIcon
    default: return bumpyIcon
  }
}

function addVehicleMarkers(vehicles) {
  clearMarkers(vehicleMarkers)
  vehicleMarkers = vehicles.map(v => {
    // 按 isTrue 字段选图标（原版语义：true=第三方车队真实车辆 car_true.png；false=未认证车辆 car.png）
    const iconUrl = v.isTrue ? carTrueIcon : carIcon
    const el = document.createElement('div')
    el.className = 'vehicle-marker'
    el.title = v.plate || '车辆'
    el.innerHTML = `<img src="${iconUrl}" alt="vehicle" draggable="false" style="width:36px;height:36px;object-fit:contain;display:block" />`
    const marker = new AMap.Marker({
      position: [v.lng, v.lat],
      content: el,
      offset: new AMap.Pixel(-18, -18)
    })
    marker.on('click', () => emit('vehicle-click', v))
    marker.setMap(map)
    return marker
  })
}

function addEventMarkers(events) {
  lastEventData = events  // 缓存供 toggleEventMarkers 切换
  clearMarkers(eventMarkers)
  eventMarkersVisible = true
  eventMarkers = events.map(e => {
    // 按事件类型 + 颠簸 level 选图标（原版语义）
    const iconUrl = getEventIcon(e.eventType, e.level)
    const el = document.createElement('div')
    el.className = 'event-marker'
    el.setAttribute('data-type', e.eventType)
    el.title = `${e.eventType || '事件'}${e.level != null ? ' L' + e.level : ''} - ${e.eventTime || ''}`
    el.innerHTML = `<img src="${iconUrl}" alt="event" draggable="false" style="width:32px;height:32px;object-fit:contain;display:block" />`
    const marker = new AMap.Marker({
      position: [e.longitude || e.lng, e.latitude || e.lat],
      content: el,
      offset: new AMap.Pixel(-16, -16)
    })
    marker.on('click', () => emit('event-click', e))
    if (eventMarkersVisible) marker.setMap(map)
    return marker
  })
}

// 清除事件 marker（flood 按钮取消时调用）
// 不影响其他图层（路网图/降水点/气象站），与"关闭 drawer 不清空"语义一致
function clearEventMarkers() {
  clearMarkers(eventMarkers)
  lastEventData = []
  eventMarkersVisible = false
}

// 切换事件 marker 显示/隐藏（路面积水颠簸事件按钮）
function toggleEventMarkers() {
  eventMarkersVisible = !eventMarkersVisible
  if (eventMarkersVisible) {
    // 显示：用缓存数据重画
    if (lastEventData.length > 0) addEventMarkers(lastEventData)
  } else {
    // 隐藏：从地图移除
    eventMarkers.forEach(m => m.setMap(null))
  }
}

// 气象站类型 → 图标映射（原版有 3 种：DSC211 路侧主机 / SR50A 雨量计 / WXT536 气象站）
// 当前 STATIONS 5 个站没有 type 字段，默认全部用 DSC211（路侧主机最常见）
// 后续如果数据中带 type 字段，按 type 切换
function getStationIcon(stationType) {
  switch (stationType) {
    case 'sr50a': return stationSR50A
    case 'wxt536': return stationWXT536
    case 'dsc211':
    default: return stationDSC211
  }
}

function addStationMarkers(active) {
  clearMarkers(stationMarkers)
  const list = active ? STATIONS : []
  stationMarkers = list.map(s => {
    const iconUrl = getStationIcon(s.type)
    const el = document.createElement('div')
    el.className = 'station-marker'
    el.title = s.name
    el.style.cursor = 'pointer'
    el.innerHTML = `<img src="${iconUrl}" alt="station" draggable="false" style="width:24px;height:24px;object-fit:contain;display:block" />`
    const marker = new AMap.Marker({
      position: s.pos,
      content: el,
      offset: new AMap.Pixel(-12, -12)
    })
    marker.on('click', () => emit('station-click', s))
    marker.setMap(map)
    return marker
  })
}

function clearMarkers(arr) {
  arr.forEach(m => { m.setMap(null); m = null })
  arr.length = 0
}

// 修复 5 个问题：'flood' 改为控制事件 marker（不是路网图）+ 关闭 drawer 不清空

// 路网图：支持多图层叠加（key=type）
const roadNetLayers = new Map()  // type -> AMap.ImageLayer

// 事件 marker 缓存：保留最近一次 loadMapEvents 的数据，用于 toggleLayer('flood') 切换显示
let lastEventData = []
let eventMarkersVisible = true  // 默认显示（之前 B1 修复时 onMounted 调用 addEventMarkers）

// 降水点 marker 独立存储（与 stationMarkers 区分）
let precipMarkers = []

// 图层类型 → 子目录映射（按原版命名）— 'flood' 移除（不再当作路网图）
const LAYER_DIRS = {
  dryWet: 'road_humidity',     // 干湿状态
  friction: 'road_friction',   // 附着系数
  temperature: 'road_temperature'  // 温度
}

// 无锡市路网 bounds（按原版：120.05, 31.36 - 120.60, 31.73，加 0.0002 偏移修正南北方向偏差）
const WUXI_BOUNDS = [[120.05, 31.3598], [120.60, 31.7298]]

// 当前时间轴值（1-25，对应 num 0,4,8,12,...,92）
let currentTime = 24  // 默认 Now

// 计算 num（按原版公式：curTime 1-24 → num 0-92 步长 4；curTime > 24 → 92）
function calcNum(curTime) {
  if (curTime > 24) return 92
  return (curTime - 1) * 4
}

// 获取图层图片 URL
function getImageSource(type, curTime) {
  const dir = LAYER_DIRS[type]
  if (!dir) return null
  const num = calcNum(curTime)
  const version = new Date().getTime()  // 防止缓存（按原版）
  return `/road_network_image/${dir}/${num}.webp?v=${version}`
}

// 清除指定类型的路网图
function removeRoadNetLayer(type) {
  if (roadNetLayers.has(type)) {
    map.remove(roadNetLayers.get(type))
    roadNetLayers.delete(type)
  }
}

// 清除所有路网图
function clearAllRoadNetLayers() {
  roadNetLayers.forEach(layer => map.remove(layer))
  roadNetLayers.clear()
}

// 加载路网图（支持多图层叠加）
function loadRoadNet(type, curTime) {
  const url = getImageSource(type, curTime)
  if (!url) return
  try {
    const layer = new AMap.ImageLayer({
      bounds: new AMap.Bounds(WUXI_BOUNDS[0], WUXI_BOUNDS[1]),
      opacity: 1,
      zooms: [10, 15],
      url
    })
    map.add(layer)
    roadNetLayers.set(type, layer)
    console.log(`[loadRoadNet] type=${type} curTime=${curTime} num=${calcNum(curTime)} url=${url}`)
  } catch (err) {
    console.error('[loadRoadNet] error:', err)
  }
}

// 设置当前时间（外部调用，触发所有激活图层按时间更新）
function setCurrentTime(t) {
  currentTime = t
  // 重新加载所有激活的图层
  const types = Array.from(roadNetLayers.keys())
  types.forEach(t => removeRoadNetLayer(t))
  types.forEach(t => loadRoadNet(t, currentTime))
}

// 切换图层（外部调用，问题 4 修复：add-only 不删已有）
// 特殊：type='flood' **完全不切事件 marker**（按用户原话"图层不能消失"）
//   → 只 emit layer-changed 让按钮高亮切换；marker 永远 setMap(map)
function toggleLayer(type) {
  if (type === 'flood') {
    // 不调任何地图操作（按钮只切 state，不影响 marker 显隐）
    emit('layer-changed', type)
    return
  }
  if (!type) {
    // 不清空（按用户原话关闭 drawer 不清空地图）
    emit('layer-changed', null)
    return
  }
  // 问题 4 修复：add-only，已有图层不删
  if (roadNetLayers.has(type)) {
    return
  }
  loadRoadNet(type, currentTime)
  emit('layer-changed', type)
}

// 降水点：5 个区质心（/get-rain-points 数据，null 时用 5 个气象站位置降级）
// P7-iter.8: 用原版 event_marker_icon_06.png (rainIcon)，与气象设备 DSC211 区分
function addPrecipPoints(points) {
  clearPrecipPoints()
  // 降级方案：后端返回 null 时用 STATIONS 的 5 个位置
  const list = (points && points.length > 0)
    ? points
    : STATIONS.map(s => ({
        longitude: s.pos[0],
        latitude: s.pos[1],
        name: s.name,
        intensity: 0
      }))
  precipMarkers = list.map(p => {
    const el = document.createElement('div')
    el.className = 'precip-marker'
    el.title = `${p.name || '降水点'}: ${p.intensity || 0}mm`
    el.innerHTML = `<img src="${rainIcon}" alt="rain" draggable="false" style="width:24px;height:24px;object-fit:contain;display:block" />`
    const marker = new AMap.Marker({
      position: [p.longitude, p.latitude],
      content: el,
      offset: new AMap.Pixel(-12, -12)
    })
    marker.setMap(map)
    return marker
  })
}

function clearPrecipPoints() {
  precipMarkers.forEach(m => map.remove(m))
  precipMarkers = []
}

// 暴露给外部
defineExpose({
  addVehicleMarkers, addEventMarkers, addStationMarkers,
  toggleLayer, showVehicleMarkers, clearAll, locateTo,
  setCurrentTime,
  addPrecipPoints, clearPrecipPoints,  // 任务 2 修复
  clearEventMarkers                    // flood 按钮取消时清除 marker
})

function showVehicleMarkers(visible) {
  vehicleMarkers.forEach(m => { visible ? m.setMap(map) : m.setMap(null) })
}

function clearAll() {
  clearMarkers(vehicleMarkers)
  clearMarkers(eventMarkers)
  clearMarkers(stationMarkers)
}

function locateTo(pos) {
  if (map) map.setCenter(pos)
}

onMounted(async () => {
  try {
    AMap = await loadAMap()
    // 加载热力图插件
    AMap.plugin(['AMap.Heatmap'], () => {
      map = new AMap.Map(mapContainer.value, { center, zoom, mapStyle: 'amap://styles/dark' })
      emit('map-ready', map, AMap)
    })
  } catch (e) {
    console.error('高德地图加载失败:', e)
  }
})

onBeforeUnmount(() => { if (map) map.destroy() })
</script>

<style scoped>
.map-wrapper { width: 100%; height: 100%; }

/* P7-iter.8: 原版 PNG marker 容器样式
   注意：marker DOM 由 createElement 创建，不在 Vue 模板渲染范围，
   故 img 尺寸必须用 inline style 设置（scoped CSS 选不中）*/
.vehicle-marker,
.event-marker,
.station-marker,
.precip-marker {
  cursor: pointer;
  transition: transform 0.15s;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.5));
}
.vehicle-marker:hover,
.event-marker:hover,
.station-marker:hover,
.precip-marker:hover {
  transform: scale(1.15);
}
</style>
