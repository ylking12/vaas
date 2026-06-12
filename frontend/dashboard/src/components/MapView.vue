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

function addVehicleMarkers(vehicles) {
  clearMarkers(vehicleMarkers)
  vehicleMarkers = vehicles.map(v => {
    const marker = new AMap.Marker({
      position: [v.lng, v.lat],
      content: `<div class="vehicle-marker">${v.plate || '车辆'}</div>`,
      offset: new AMap.Pixel(-20, -20)
    })
    marker.on('click', () => emit('vehicle-click', v))
    marker.setMap(map)
    return marker
  })
}

function addEventMarkers(events) {
  clearMarkers(eventMarkers)
  eventMarkers = events.map(e => {
    const colorMap = { bump: '#F56C6C', slip: '#E6A23C', ponding: '#409EFF', ice: '#67C23A', low_attachment: '#909399' }
    const marker = new AMap.Marker({
      position: [e.longitude || e.lng, e.latitude || e.lat],
      content: `<div style="width:12px;height:12px;border-radius:50%;background:${colorMap[e.eventType]||'#F56C6C'};border:2px solid #fff"></div>`,
      offset: new AMap.Pixel(-6, -6)
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
    const marker = new AMap.Marker({
      position: s.pos,
      content: '<div style="width:16px;height:16px;border-radius:50%;background:#67C23A;border:2px solid #fff;cursor:pointer">🌡️</div>',
      offset: new AMap.Pixel(-8, -8)
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
</style>
