<template>
  <div class="dashboard-root">
    <div class="top-bar">
      <div class="top-left">
        <span class="s-icon" @click="togglePanel">S</span>
      </div>
      <div class="top-title">恶劣天气道路路面状态感知与预测系统</div>
      <div class="top-right">
        <el-button text @click="goBack">返回</el-button>
      </div>
    </div>
    <div class="coloured-ribbon"></div>
    <div class="main-area">
      <div class="left-panel" :class="{ expanded: panelExpanded }" @mouseenter="panelExpanded = true" @mouseleave="panelExpanded = false">
        <div class="panel-collapsed">
          <div class="panel-btn" @click="openDrawer">实时数据</div>
        </div>
        <div class="panel-content" v-show="panelExpanded">
          <div class="panel-section">
            <h4>实时车队数据</h4>
            <p class="fleet-count">联网车辆 <span class="num">{{ onlineCount }}</span> 辆</p>
          </div>
          <div class="panel-section">
            <h4>路网状态</h4>
            <div v-for="item in roadNetLayers" :key="item.key"
              class="layer-option"
              :class="{ active: selectedLayer === item.key && !item.isSpecial, 'coexist': item.isSpecial && selectedSpecial }"
              @click="toggleLayer(item)">
              <span>{{ item.label }}</span>
            </div>
          </div>
          <div class="panel-section">
            <h4>实时气象数据</h4>
            <p>降水量 <span class="num">{{ precipText }}</span></p>
            <el-button text type="primary" @click="showWeatherDevice">查看气象设备</el-button>
          </div>
        </div>
      </div>
      <MapView ref="mapViewRef" @map-ready="onMapReady" @vehicle-click="onVehicleClick" @event-click="onEventClick" @station-click="onStationClick" />
      <div class="time-slider">
        <span>过去23h</span>
        <el-slider v-model="sliderValue" :min="1" :max="25" :step="1" :marks="sliderMarks" />
        <span>未来1h</span>
      </div>
    </div>
    <el-drawer v-model="drawerVisible" direction="ltr" size="70%" title="实时数据">
      <div class="drawer-grid">
        <div class="drawer-left">
          <div class="panel-section">
            <h4>实时车队数据</h4>
            <p class="fleet-count">联网车辆 <span class="num">{{ onlineCount }}</span> 辆</p>
          </div>
          <div class="panel-section">
            <h4>路网状态</h4>
            <div v-for="item in roadNetLayers" :key="item.key"
              class="layer-option" :class="{ active: selectedLayer === item.key && !item.isSpecial, 'coexist': item.isSpecial && selectedSpecial }"
              @click="toggleLayer(item)">
              <span>{{ item.label }}</span>
            </div>
          </div>
          <div class="panel-section">
            <h4>实时气象数据</h4>
            <p>降水量 <span class="num">{{ precipText }}</span></p>
            <el-button text type="primary" @click="showWeatherDevice">查看气象设备</el-button>
          </div>
        </div>
        <div class="drawer-center">
          <div class="panel-section">
            <h4>历史24小时路面状态</h4>
            <SensorChart :data="chartData" />
          </div>
          <div class="panel-section">
            <h4>道路实时路况</h4>
            <el-select v-model="sensorId" @change="changeSensor">
              <el-option v-for="s in sensors" :key="s.key" :label="s.name" :value="s.key" />
            </el-select>
            <div class="sensor-cards">
              <div class="sensor-card" :class="{ active: chartType === 'airTemperature' }" @click="switchChart('airTemperature')">
                <p>空气温度</p>
                <p class="value">{{ sensorData.airTemperature }}℃</p>
              </div>
              <div class="sensor-card" :class="{ active: chartType === 'roadSurfaceTemperature' }" @click="switchChart('roadSurfaceTemperature')">
                <p>路面温度</p>
                <p class="value">{{ sensorData.roadSurfaceTemperature }}℃</p>
              </div>
              <div class="sensor-card" :class="{ active: chartType === 'relativeHumidity' }" @click="switchChart('relativeHumidity')">
                <p>相对湿度</p>
                <p class="value">{{ sensorData.relativeHumidity }}%</p>
              </div>
            </div>
          </div>
          <div class="panel-section">
            <div class="alarm-header">
              <h4>告警视图列表</h4>
              <el-button size="small" @click="exportAlarm">导出</el-button>
            </div>
            <el-table :data="alarmList" stripe size="small" max-height="400">
              <el-table-column prop="roadName" label="路名" min-width="120" />
              <el-table-column prop="sourceName" label="告警源" min-width="120" />
              <el-table-column prop="eventType" label="告警类型" min-width="100" />
              <el-table-column prop="datetime" label="时间" min-width="160" />
            </el-table>
          </div>
        </div>
        <div class="drawer-right">
          <div class="panel-section">
            <h4>服务统计数据</h4>
            <p class="stat-value">{{ rcsData.coveredArea }} km²</p>
            <p class="stat-label">路况感知覆盖范围</p>
            <p class="stat-weather">{{ weatherData.text || '--' }}</p>
            <el-divider />
            <p>最近24h内颠簸路面个数</p>
            <p class="stat-number">{{ summaryData.num_bumpyroad }}</p>
            <p>最近24h内湿滑路面个数</p>
            <p class="stat-number">{{ summaryData.num_wetroad }}</p>
            <p>最近24h内积水路面个数</p>
            <p class="stat-number">{{ summaryData.num_waterroad }}</p>
            <el-divider />
            <p class="maintain-title">最近24h内以下路段存在颠簸，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.bumpyRoadArray" :key="'b'+i">{{ road }} </span>
              <span v-if="!summaryData.bumpyRoadArray || summaryData.bumpyRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
            <p class="maintain-title">最近24h内以下路段存在湿滑，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.slipperyRoadArray" :key="'s'+i">{{ road }} </span>
              <span v-if="!summaryData.slipperyRoadArray || summaryData.slipperyRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
            <p class="maintain-title">最近24h内以下路段存在积水，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.waterRoadArray" :key="'w'+i">{{ road }} </span>
              <span v-if="!summaryData.waterRoadArray || summaryData.waterRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useDashboardStore } from '../stores/dashboard'
import * as api from '../api'
import MapView from '../components/MapView.vue'
import SensorChart from '../components/SensorChart.vue'
import * as XLSX from 'xlsx'

const store = useDashboardStore()
const drawerVisible = ref(false)
const panelExpanded = ref(false)
const sliderValue = ref(13)
const sensorId = ref(1)
const chartType = ref('airTemperature')
const chartData = ref([])
const alarmList = ref([])
const sensorData = reactive({ airTemperature: 0, roadSurfaceTemperature: 0, relativeHumidity: 0 })
const rcsData = reactive({ coveredArea: 0, coveredRoadLength: 0, totalMilage: 0 })
const weatherData = reactive({ text: '', temp: 0, humidity: 0 })
const summaryData = reactive({ num_bumpyroad: 0, num_wetroad: 0, num_waterroad: 0, bumpyRoadArray: [], slipperyRoadArray: [], waterRoadArray: [] })
const onlineCount = ref(0)        // 联网车辆数（来自 /location 接口）
const precipText = ref('--')     // 降水量（来自 /get_weather.precip）
const selectedLayer = ref('dryWet')
const selectedSpecial = ref(true)
const chartRef = ref(null)

const roadNetLayers = [
  { key: 'dryWet', label: '路面干湿状态图层', isSpecial: false },
  { key: 'friction', label: '路面附着系数图层', isSpecial: false },
  { key: 'temperature', label: '路面温度状态图层', isSpecial: false },
  { key: 'flood', label: '路面积水颠簸事件', isSpecial: true }
]

const sensors = [
  { key: 1, name: '文惠路与锦绣路' },
  { key: 2, name: '先锋中路与新锡路' },
  { key: 3, name: '机场路-泰山路' },
  { key: 4, name: '高浪路-兴梁道' },
  { key: 5, name: '运河西路' }
]

const sliderMarks = {
  1: '1h', 13: 'Now', 25: '未来1h'
}

// 时间轴联动：拖动时刷新数据
watch(sliderValue, (newVal) => {
  loadAlarmList()
  loadChartData()
})

function togglePanel() { panelExpanded.value = !panelExpanded.value }
function goBack() { history.back() }
function openDrawer() { drawerVisible.value = true }

const mapViewRef = ref(null)

function toggleLayer(item) {
  if (item.isSpecial) {
    selectedSpecial.value = !selectedSpecial.value
    mapViewRef.value?.toggleLayer(selectedSpecial.value ? 'flood' : null)
  } else {
    selectedLayer.value = item.key
    mapViewRef.value?.toggleLayer(item.key)
  }
}

function changeSensor(val) {
  sensorId.value = val
  loadSensorData()
}

function switchChart(type) {
  chartType.value = type
  loadChartData()
}

function showWeatherDevice() {
  mapViewRef.value?.addStationMarkers(true)
  mapViewRef.value?.locateTo([120.40, 31.52])
}

function exportAlarm() {
  if (!alarmList.value.length) return
  const ws = XLSX.utils.json_to_sheet(alarmList.value.map(a => ({
    '路名': a.roadName,
    '告警源': a.sourceName,
    '告警类型': a.eventType,
    '时间': a.datetime
  })))
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '告警列表')
  XLSX.writeFile(wb, `告警列表_${new Date().toISOString().slice(0,10)}.xlsx`)
}

function onVehicleClick(v) {
  ElMessageBox.alert(`车牌号：${v.plate || '--'}\n速度：${v.speed || '--'} km/h`, '车辆信息')
}

function onEventClick(e) {
  ElMessageBox.confirm(`类型：${e.eventType}\n位置：${e.longitude || '--'}, ${e.latitude || '--'}\n时间：${e.eventTime || '--'}`, '确认删除事件？', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    api.deleteEvent(e.eventId, e.eventType).catch(() => {})
    loadAlarmList()
  }).catch(() => {})
}

function onStationClick(s) {
  ElMessageBox.alert(`气象站：${s.name}`, '气象设备')
}

function loadAlarmList() {
  api.getAlarmList(1).then(res => {
    if (res && res.data) alarmList.value = res.data
  }).catch(() => {})
}

function loadSensorData() {
  api.getRealTimeSensorData(sensorId.value).then(res => {
    if (res && res.data) {
      Object.assign(sensorData, res.data)
      store.setSensorData(res.data)
    }
  }).catch(() => {})
}

function loadChartData() {
  api.getLast24hDataPlot(sensorId.value, chartType.value).then(res => {
    if (res && Array.isArray(res.data)) {
      chartData.value = res.data.map((v, i) => ({ time: i + ':00', value: v }))
    }
  }).catch(() => {})
}

function loadCoveredData() {
  api.getCoveredRange().then(res => {
    if (res && res.data) {
      rcsData.coveredArea = res.data[0]
      rcsData.coveredRoadLength = res.data[1]
      rcsData.totalMilage = res.data[2]
    }
  }).catch(() => {})
  api.getWeather().then(res => {
    if (res && res.data) {
      Object.assign(weatherData, res.data)
      // 降水量格式化：null/0 都显示，>0 显示 1 位小数
      const p = res.data.precip
      precipText.value = (p === null || p === undefined) ? '--' : `${Number(p).toFixed(1)} mm`
    }
  }).catch(() => {})
}

// 联网车辆数：/location 返回 Map<deviceId, OnlineVehicle>
function loadOnlineVehicles() {
  api.getOnlineVehicles().then(res => {
    if (res && typeof res === 'object') {
      // 后端直接返回 Map，axios 拦截器已 unwrap 到 res.data
      onlineCount.value = Object.keys(res).length
    }
  }).catch(() => {})
}

function loadEventSummary() {
  api.getEventSummary().then(res => {
    if (res && res.data) {
      summaryData.num_bumpyroad = res.data.bumpy_road_amount || 0
      summaryData.num_wetroad = res.data.slippery_road_amount || 0
      summaryData.num_waterroad = res.data.water_road_amount || 0
      summaryData.bumpyRoadArray = res.data.bumpy_road_to_maintain || []
      summaryData.slipperyRoadArray = res.data.slippery_road_to_maintain || []
      summaryData.waterRoadArray = res.data.water_road_to_maintain || []
    }
  }).catch(() => {})
}

let mapInstance = null
let refreshTimer = null
let sseSource = null

function onMapReady(instance, AMap) {
  mapInstance = { instance, AMap }
  loadMapEvents()
}

async function loadMapEvents() {
  const mv = mapViewRef.value
  if (!mv) return
  try {
    const [bump, slip] = await Promise.all([
      api.getLast24hEvent('bump').catch(() => []),
      api.getLast24hEvent('slip').catch(() => [])
    ])
    const events = [
      ...(Array.isArray(bump) ? bump.map(e => ({ ...e, eventType: 'bump' })) : []),
      ...(Array.isArray(slip) ? slip.map(e => ({ ...e, eventType: 'slip' })) : [])
    ]
    if (events.length) mv.addEventMarkers(events)
  } catch {}
}

onMounted(() => {
  loadAlarmList()
  loadSensorData()
  loadChartData()
  loadCoveredData()
  loadEventSummary()
  loadOnlineVehicles()

  refreshTimer = setInterval(() => {
    loadSensorData()
    loadCoveredData()
    loadOnlineVehicles()
  }, 15 * 60 * 1000)

  // SSE
  const baseUrl = import.meta.env.VITE_API_BASE || 'http://localhost:50410/spring/v1'
  sseSource = new EventSource(baseUrl + '/stream_data')
  sseSource.addEventListener('message', e => {
    if (e.data === 'None') return
    try {
      const j = JSON.parse(e.data)
      if (j && j.eventType) {
        loadAlarmList()
      }
    } catch {}
  })
})

onBeforeUnmount(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  if (sseSource) sseSource.close()
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { width: 100%; height: 100%; overflow: hidden; font-family: 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.dashboard-root { width: 100%; height: 100%; display: flex; flex-direction: column; position: relative; background: #020203; }
.coloured-ribbon { height: 4px; width: 100%; background: linear-gradient(90deg, #ff6b6b, #ffd93d, #6bcb77, #4d96ff, #6c5ce7); }
.top-bar { height: 48px; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; z-index: 10; }
.top-title { font-size: 19.2px; font-weight: 500; color: #FFF6DA; letter-spacing: 2px; font-family: 'Noto Sans SC', sans-serif; }
.s-icon { cursor: pointer; font-size: 20px; font-weight: bold; color: #FFF6DA; padding: 8px; }
.main-area { flex: 1; position: relative; overflow: hidden; }
.left-panel { position: absolute; left: 0; top: 0; height: 100%; width: 43px; background: transparent; transition: width 0.3s; z-index: 5; overflow: hidden; }
.left-panel.expanded { background: rgba(0,0,0,0.8); width: 288px; }
.panel-collapsed { width: 43px; }
.panel-btn { writing-mode: vertical-lr; padding: 16px 12px; cursor: pointer; color: #FFF6DA; font-size: 19.2px; background: #000; font-family: 'Noto Sans SC', sans-serif; }
.panel-content { width: 245px; padding: 12px; color: #c0d0e0; font-size: 13px; }
.panel-section { margin-bottom: 20px; }
.panel-section h4 { font-size: 14px; color: #FFF6DA; margin-bottom: 8px; border-bottom: 1px solid rgba(255,246,218,0.2); padding-bottom: 4px; }
.layer-option { padding: 6px 8px; cursor: pointer; border-radius: 4px; margin-bottom: 2px; color: #a0b0c0; }
.layer-option.active { background: linear-gradient(90deg, #32281e, #FFF6DA); color: #FFF6DA; }
.layer-option.coexist { background: linear-gradient(90deg, #2d3a1e, #67C23A); color: #FFF6DA; }
.fleet-count { color: #FFF6DA; font-size: 16px; }
.fleet-count .num, .panel-section p > .num { color: #FFF6DA; font-size: 18px; font-weight: 600; margin: 0 2px; }
.time-slider { position: absolute; bottom: 0; left: 0; right: 0; height: 54px; background: #000; display: flex; align-items: center; padding: 0 24px; gap: 16px; z-index: 5; color: #c0d0e0; }
.time-slider .el-slider { flex: 1; }
.drawer-grid { display: flex; gap: 16px; height: 100%; background: #1a1a1a; color: #FFF6DA; }
.drawer-left { width: 240px; overflow-y: auto; padding: 8px; }
.drawer-center { flex: 1; overflow-y: auto; padding: 8px; }
.drawer-right { width: 260px; overflow-y: auto; padding: 8px; }
.sensor-cards { display: flex; gap: 8px; margin-top: 8px; }
.sensor-card { flex: 1; background: rgba(255,246,218,0.05); border-radius: 6px; padding: 12px; text-align: center; cursor: pointer; border: 2px solid transparent; color: #FFF6DA; }
.sensor-card.active { border-color: #FFF6DA; background: rgba(255,246,218,0.1); }
.sensor-card .value { font-size: 20px; font-weight: bold; color: #FFF6DA; }
.alarm-header { display: flex; justify-content: space-between; align-items: center; }
.stat-value { font-size: 28px; font-weight: bold; color: #FFF6DA; }
.stat-label { font-size: 12px; color: #a0a0a0; }
.stat-number { font-size: 22px; font-weight: bold; color: #FFF6DA; }
.stat-weather { font-size: 24px; margin: 8px 0; color: #FFF6DA; }
/* Drawer dark theme overrides - Brown/Gold scheme */
.el-drawer { background: #1a1a1a !important; }
.el-drawer__header { color: #FFF6DA !important; }
.el-table { background: transparent !important; color: #FFF6DA !important; }
.el-table th.el-table__cell { background: rgba(50,40,30,0.8) !important; color: #FFF6DA !important; }
.el-table td.el-table__cell { background: transparent !important; color: #FFF6DA !important; }
.el-table--striped .el-table__body tr.el-table__row--striped td { background: rgba(255,246,218,0.03) !important; }
.el-select-dropdown { background: #1a1a1a !important; border: 1px solid rgba(255,246,218,0.2) !important; }
.el-select-dropdown__item { color: #FFF6DA !important; }
.el-select-dropdown__item.hover { background: rgba(255,246,218,0.1) !important; }
.el-button--primary { background: linear-gradient(90deg, #32281e, #FFF6DA) !important; border-color: #FFF6DA !important; color: #000 !important; }
.el-button--primary:hover { background: linear-gradient(90deg, #FFF6DA, #32281e) !important; }
.el-divider { background: rgba(255,246,218,0.2) !important; }
.maintain-title { font-size: 12px; color: #FFF6DA; margin: 12px 0 6px 0; opacity: 0.8; }
.maintain-list { font-size: 13px; color: #FFF6DA; min-height: 20px; padding: 4px 0; }
.maintain-list .no-data { color: #666; font-style: italic; }
</style>
