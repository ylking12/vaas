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

let currentLayerType = null
let overlayLayer = null
let heatmap = null

// 图层配置
const layerConfigs = {
  dryWet: {
    name: '路面干湿状态',
    color: 'rgba(0, 150, 255, 0.3)',
    data: [] // 需要从 API 获取
  },
  friction: {
    name: '路面附着系数',
    gradient: { 0: 'rgba(255,0,0,0.5)', 0.5: 'rgba(255,255,0,0.5)', 1: 'rgba(0,255,0,0.5)' },
    data: []
  },
  temperature: {
    name: '路面温度',
    gradient: { 0: 'rgba(0,0,255,0.5)', 20: 'rgba(0,255,0,0.5)', 40: 'rgba(255,0,0,0.5)' },
    data: []
  },
  flood: {
    name: '路面积水颠簸',
    color: 'rgba(0, 100, 200, 0.4)',
    data: []
  }
}

function toggleLayer(type) {
  // 移除现有图层
  if (overlayLayer) {
    if (Array.isArray(overlayLayer)) {
      overlayLayer.forEach(p => p.setMap(null))
    } else {
      overlayLayer.setMap(null)
    }
    overlayLayer = null
  }
  if (heatmap) {
    heatmap.setMap(null)
    heatmap = null
  }

  // 切换状态
  if (!type || type === currentLayerType) {
    currentLayerType = null
    emit('layer-changed', null)
    return
  }

  currentLayerType = type
  const config = layerConfigs[type]
  console.log('Layer activated:', config.name, 'type:', type)

  // 创建简单的圆形标记作为图层可视化（简化版）
  const overlayMarkers = []
  const color = type === 'dryWet' ? '#0096ff' : type === 'flood' ? '#0064c8' : '#ffd700'

  // 在地图中心区域创建一些可视化点
  const centerPoints = [
    [120.38, 31.52], [120.40, 31.53], [120.42, 31.50],
    [120.36, 31.55], [120.44, 31.51], [120.39, 31.54]
  ]

  centerPoints.forEach(([lng, lat]) => {
    const circle = new AMap.Circle({
      center: [lng, lat],
      radius: 500,
      fillColor: color,
      fillOpacity: 0.3,
      strokeColor: color,
      strokeWeight: 2,
      strokeOpacity: 0.8
    })
    circle.setMap(map)
    overlayMarkers.push(circle)
  })

  overlayLayer = overlayMarkers
  console.log(`Created ${overlayMarkers.length} overlay circles for ${config.name}`)
  emit('layer-changed', type)
}

// 生成模拟热力图数据
function generateMockHeatmapData(type) {
  const points = []
  const count = 50
  for (let i = 0; i < count; i++) {
    points.push({
      lng: 120.3 + Math.random() * 0.3,
      lat: 31.4 + Math.random() * 0.2,
      count: Math.floor(Math.random() * 100)
    })
  }
  return points
}

// 生成模拟多边形数据
function generateMockPolygons(type) {
  const polygons = []
  const color = type === 'dryWet' ? 'rgba(0, 150, 255, 0.2)' : 'rgba(0, 100, 200, 0.3)'
  const strokeColor = type === 'dryWet' ? '#0096ff' : '#0064c8'

  // 创建几个模拟路段多边形
  const segments = [
    [[120.35, 31.55], [120.36, 31.55], [120.36, 31.56], [120.35, 31.56]],
    [[120.38, 31.52], [120.40, 31.52], [120.40, 31.53], [120.38, 31.53]],
    [[120.42, 31.50], [120.44, 31.50], [120.44, 31.51], [120.42, 31.51]]
  ]

  segments.forEach(path => {
    const polygon = new AMap.Polygon({
      path: path,
      fillColor: color,
      fillOpacity: 0.5,
      strokeColor: strokeColor,
      strokeWeight: 2,
      strokeOpacity: 0.8
    })
    polygons.push(polygon)
  })

  return polygons
}

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

defineExpose({ addVehicleMarkers, addEventMarkers, addStationMarkers, toggleLayer, showVehicleMarkers, clearAll, locateTo })

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
