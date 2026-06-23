<template>
  <div ref="mapContainer" class="map-wrapper"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

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

// P7-iter.2-2: 车辆/事件/气象站标记重做，使用内嵌 SVG + CSS 动画

// 车辆图标 SVG（简洁车型：圆角车身 + 方向三角）
const VEHICLE_SVG = `
<svg xmlns="http://www.w3.org/2000/svg" width="36" height="20" viewBox="0 0 36 20">
  <defs>
    <linearGradient id="vg" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0" stop-color="#32281e"/>
      <stop offset="1" stop-color="#FFF6DA"/>
    </linearGradient>
  </defs>
  <path d="M3 14 L4 6 Q5 4 8 4 L28 4 Q31 4 32 6 L33 14 Z" fill="url(#vg)" stroke="#1a1a1a" stroke-width="1"/>
  <polygon points="29,9 33,9 31,13" fill="#FFF6DA"/>
  <circle cx="9" cy="16" r="2.5" fill="#1a1a1a"/>
  <circle cx="27" cy="16" r="2.5" fill="#1a1a1a"/>
</svg>
`

// 事件标记 SVG（中心点 + 脉冲环）
const eventMarkerSvg = (color) => `
<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28">
  <circle class="pulse-ring" cx="14" cy="14" r="13" fill="none" stroke="${color}" stroke-width="1.5" opacity="0.6"/>
  <circle cx="14" cy="14" r="6" fill="${color}" stroke="#fff" stroke-width="1.5"/>
  <circle cx="14" cy="14" r="2.5" fill="#fff"/>
</svg>
`

// 气象站图标 SVG（塔形 + 信号环）
const STATION_SVG = `
<svg xmlns="http://www.w3.org/2000/svg" width="26" height="32" viewBox="0 0 26 32">
  <defs>
    <radialGradient id="sg" cx="0.5" cy="0.5" r="0.5">
      <stop offset="0" stop-color="#67C23A" stop-opacity="0.9"/>
      <stop offset="1" stop-color="#67C23A" stop-opacity="0.3"/>
    </radialGradient>
  </defs>
  <circle cx="13" cy="20" r="11" fill="url(#sg)"/>
  <rect x="11" y="3" width="4" height="22" rx="1" fill="#1a1a1a" stroke="#67C23A" stroke-width="1.5"/>
  <circle cx="13" cy="6" r="2.5" fill="#67C23A" stroke="#fff" stroke-width="1"/>
  <path d="M6 28 L20 28" stroke="#67C23A" stroke-width="2" stroke-linecap="round"/>
  <text x="13" y="13" text-anchor="middle" font-size="6" fill="#fff" font-family="Arial">°C</text>
</svg>
`

function addVehicleMarkers(vehicles) {
  clearMarkers(vehicleMarkers)
  vehicleMarkers = vehicles.map(v => {
    const el = document.createElement('div')
    el.className = 'vehicle-marker'
    el.innerHTML = VEHICLE_SVG
    el.title = v.plate || '车辆'
    const marker = new AMap.Marker({
      position: [v.lng, v.lat],
      content: el,
      offset: new AMap.Pixel(-18, -10)
    })
    marker.on('click', () => emit('vehicle-click', v))
    marker.setMap(map)
    return marker
  })
}

function addEventMarkers(events) {
  clearMarkers(eventMarkers)
  const colorMap = { bump: '#F56C6C', slip: '#E6A23C', ponding: '#409EFF', ice: '#67C23A', low_attachment: '#909399' }
  const labelMap = { bump: '颠', slip: '滑', ponding: '积', ice: '冰', low_attachment: '低' }
  eventMarkers = events.map(e => {
    const color = colorMap[e.eventType] || '#F56C6C'
    const el = document.createElement('div')
    el.className = 'event-marker'
    el.setAttribute('data-type', e.eventType)
    el.setAttribute('data-color', color)
    el.title = `${e.eventType || '事件'} - ${e.eventTime || ''}`
    el.innerHTML = `
      <div class="event-pulse" style="background:${color}"></div>
      <div class="event-core" style="background:${color}">
        <span class="event-label">${labelMap[e.eventType] || '!'}</span>
      </div>
    `
    const marker = new AMap.Marker({
      position: [e.longitude || e.lng, e.latitude || e.lat],
      content: el,
      offset: new AMap.Pixel(-14, -14)
    })
    marker.on('click', () => emit('event-click', e))
    marker.setMap(map)
    return marker
  })
}

function addStationMarkers(active) {
  clearMarkers(stationMarkers)
  const list = active ? STATIONS : []
  stationMarkers = list.map(s => {
    const el = document.createElement('div')
    el.className = 'station-marker'
    el.innerHTML = STATION_SVG
    el.title = s.name
    el.style.cursor = 'pointer'
    const marker = new AMap.Marker({
      position: s.pos,
      content: el,
      offset: new AMap.Pixel(-13, -28)
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

// 修复 4 个问题：MapView 多图层叠加 + 降水/气象设备区分

// 路网图：支持多图层叠加（key=type）
const roadNetLayers = new Map()  // type -> AMap.ImageLayer

// 降水点 marker 独立存储（与 stationMarkers 区分）
let precipMarkers = []

// 图层类型 → 子目录映射（按原版命名）
const LAYER_DIRS = {
  dryWet: 'road_humidity',     // 干湿状态
  friction: 'road_friction',   // 附着系数
  temperature: 'road_temperature',  // 温度
  flood: 'road_humidity'       // 积水颠簸用干湿图（同原版——它本质上就是路面湿度/积水状态）
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

// 切换图层（外部调用，支持多图层叠加 + toggle 关闭）
function toggleLayer(type) {
  if (!type) {
    // 清空所有
    clearAllRoadNetLayers()
    emit('layer-changed', null)
    return
  }
  if (roadNetLayers.has(type)) {
    // 已存在 → 移除（toggle 关闭）
    removeRoadNetLayer(type)
  } else {
    // 不存在 → 添加
    loadRoadNet(type, currentTime)
  }
  emit('layer-changed', type)
}

// 降水点：5 个区质心（/get-rain-points 数据，null 时用 5 个气象站位置降级）
// 视觉与气象设备（addStationMarkers 用 STATION_SVG 白色）区分：蓝色水滴占位
function addPrecipPoints(points) {
  clearPrecipPoints()
  // 降级方案：后端返回 null 时用 STATIONS 的 5 个位置（用户后续给图标替换）
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
    el.innerHTML = '💧'  // 占位：用户后续给图标
    el.title = `${p.name || '降水点'}: ${p.intensity || 0}mm`
    el.style.cssText = 'background:rgba(0,150,255,0.85);border-radius:50%;width:24px;height:24px;display:flex;align-items:center;justify-content:center;font-size:14px;border:2px solid #fff;box-shadow:0 0 6px rgba(0,150,255,0.6)'
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
  addPrecipPoints, clearPrecipPoints  // 任务 2 修复
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

/* P7-iter.2-2: 地图标记样式 */
.vehicle-marker {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.5));
  transition: transform 0.15s;
  cursor: pointer;
}
.vehicle-marker:hover { transform: scale(1.15); }

.event-marker {
  position: relative;
  width: 28px;
  height: 28px;
  cursor: pointer;
}
.event-pulse {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  opacity: 0.4;
  animation: event-pulse 1.8s ease-out infinite;
}
.event-core {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  border: 2px solid #fff;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}
.event-label {
  font-size: 11px;
  font-weight: bold;
  color: #fff;
  line-height: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}
@keyframes event-pulse {
  0%   { transform: scale(0.6); opacity: 0.7; }
  100% { transform: scale(2.0); opacity: 0; }
}

.station-marker {
  filter: drop-shadow(0 2px 6px rgba(103, 194, 58, 0.5));
  transition: transform 0.15s;
}
.station-marker:hover { transform: scale(1.1); }
</style>
