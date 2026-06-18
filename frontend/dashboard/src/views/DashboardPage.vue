<template>
  <div class="dashboard-root">
    <div class="top-bar">
      <div class="top-left">
        <span class="s-icon" @click="togglePanel" title="切换面板">
          <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="3" width="7" height="7"/>
            <rect x="14" y="3" width="7" height="7"/>
            <rect x="3" y="14" width="7" height="7"/>
            <rect x="14" y="14" width="7" height="7"/>
          </svg>
        </span>
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
          <LayerPanel
            :online-count="onlineCount"
            :precip-text="precipText"
            :selected-layer="selectedLayer"
            :selected-special="selectedSpecial"
            @toggle-layer="toggleLayer"
            @show-weather-device="showWeatherDevice"
          />
        </div>
      </div>
      <MapView ref="mapViewRef" @map-ready="onMapReady" @vehicle-click="onVehicleClick" @event-click="onEventClick" @station-click="onStationClick" />
      <div class="time-slider">
        <!-- P7+ 时间轴：参照原版 11-timeline.png + 设计文档 §3.4
             - 外层黑底圆角浮层（原版有）+ 70% 宽 + 居中 + 距底 5%
             - 两端 "过去23h" / "未来1h" 文字（原版有）
             - 24 段色块跑道（每段 1 小时）
             - 真实小时刻度（16:00, 17:00, ..., Now, ..., 13:00）
             - 默认 Now 位置（sliderValue=24） -->
        <p class="time-prefix">过去23h</p>
        <el-slider
          v-model="sliderValue"
          :min="1" :max="25" :step="1"
          show-stops
          :marks="sliderMarks"
          :input-style="{ color: '#fff6da', fontFamily: 'Noto Sans SC', fontWeight: '100' }"
          tooltip-class="time-tooltip"
        />
        <p class="time-suffix">未来1h</p>
      </div>
    </div>
    <el-drawer v-model="drawerVisible" direction="ltr" size="100%" title="实时数据">
      <div class="drawer-grid">
        <div class="drawer-left">
          <LayerPanel
            :online-count="onlineCount"
            :precip-text="precipText"
            :selected-layer="selectedLayer"
            :selected-special="selectedSpecial"
            @toggle-layer="toggleLayer"
            @show-weather-device="showWeatherDevice"
          />
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

    <!-- P7-iter.2-1: 自定义 Popup 替代 ElMessageBox -->
    <Popup v-model="popupVisible" :title="popupTitle" :type="popupType" :show-default-buttons="popupHasButtons" @confirm="onPopupConfirm" @cancel="onPopupCancel">
      <div v-if="popupData" class="popup-detail">
        <div v-for="(value, key) in popupData" :key="key" class="popup-row">
          <span class="popup-label">{{ key }}</span>
          <span class="popup-value">{{ value }}</span>
        </div>
      </div>
    </Popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useDashboardStore } from '../stores/dashboard'
import * as api from '../api'
import MapView from '../components/MapView.vue'
import SensorChart from '../components/SensorChart.vue'
import Popup from '../components/Popup.vue'
import LayerPanel from '../components/LayerPanel.vue'
import * as XLSX from 'xlsx'

const store = useDashboardStore()
const drawerVisible = ref(false)
const panelExpanded = ref(false)
// P7+ 时间轴：默认 Now 位置（24=现在），原版也是 Now 在中部偏右
const sliderValue = ref(24)
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

// P7-iter.2-1: 自定义 Popup 状态
const popupVisible = ref(false)
const popupTitle = ref('提示')
const popupType = ref('info')            // info | success | warning | danger
const popupData = ref(null)              // {label: value} 形式展示
const popupHasButtons = ref(true)        // 决定是否显示底部默认按钮
const pendingEvent = ref(null)           // 事件删除确认时暂存
const selectedSpecial = ref(true)
const chartRef = ref(null)

// P7-iter.2-3: roadNetLayers 已抽到 LayerPanel.vue 内部
const sensors = [
  { key: 1, name: '文惠路与锦绣路' },
  { key: 2, name: '先锋中路与新锡路' },
  { key: 3, name: '机场路-泰山路' },
  { key: 4, name: '高浪路-兴梁道' },
  { key: 5, name: '运河西路' }
]

// P7+ 时间轴：参照原版 11-timeline.png 的真实小时刻度（HH:00 格式）
// 过去 23h ~ 未来 1h：以当前小时为基准，滑动到对应位置时显示真实小时
//   value 1..23 → 过去 (currentHour-23)..(currentHour-1) 的 HH:00
//   value 24     → Now
//   value 25     → (currentHour+1) 的 HH:00
const sliderMarks = computed(() => {
  const m = {}
  const now = new Date()
  const h = now.getHours()
  const fmt = (n) => String(n).padStart(2, '0')
  for (let i = 1; i <= 23; i++) {
    const past = (h - 24 + i + 24) % 24
    m[i] = `${fmt(past)}:00`
  }
  m[24] = 'Now'
  m[25] = `${fmt((h + 1) % 24)}:00`
  return m
})

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
  popupTitle.value = '车辆信息'
  popupType.value = 'info'
  popupData.value = {
    '车牌号': v.plate || '--',
    '速度': `${v.speed || '--'} km/h`
  }
  popupHasButtons.value = false
  popupVisible.value = true
}

function onEventClick(e) {
  // 保存待删除事件引用，Popup 确认时使用
  pendingEvent.value = e
  popupTitle.value = '确认删除事件？'
  popupType.value = 'warning'
  popupData.value = {
    '类型': e.eventType,
    '位置': `${e.longitude || '--'}, ${e.latitude || '--'}`,
    '时间': e.eventTime || '--'
  }
  popupHasButtons.value = true
  popupVisible.value = true
}

function onStationClick(s) {
  popupTitle.value = '气象设备'
  popupType.value = 'info'
  popupData.value = {
    '站点名': s.name,
    '坐标': `${s.pos?.[0] || '--'}, ${s.pos?.[1] || '--'}`
  }
  popupHasButtons.value = false
  popupVisible.value = true
}

function onPopupConfirm() {
  if (pendingEvent.value) {
    api.deleteEvent(pendingEvent.value.eventId, pendingEvent.value.eventType).catch(() => {})
    loadAlarmList()
    pendingEvent.value = null
  }
}

function onPopupCancel() {
  pendingEvent.value = null
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
    // P9-修复: 补全 5 种事件类型（与原大屏一致）
    // 原大屏: 颠簸/湿滑/积水/结冰/低附着
    // P7 重写版: 之前只支持 bump/slip，现补全 ponding/ice/low-attachment
    const [bump, slip, ponding, ice, lowAttach] = await Promise.all([
      api.getLast24hEvent('bump').catch(() => []),
      api.getLast24hEvent('slip').catch(() => []),
      api.getLast24hEvent('ponding').catch(() => []),
      api.getLast24hEvent('ice').catch(() => []),
      api.getLast24hEvent('low-attachment').catch(() => [])
    ])
    const events = [
      ...(Array.isArray(bump) ? bump.map(e => ({ ...e, eventType: 'bump' })) : []),
      ...(Array.isArray(slip) ? slip.map(e => ({ ...e, eventType: 'slip' })) : []),
      ...(Array.isArray(ponding) ? ponding.map(e => ({ ...e, eventType: 'ponding' })) : []),
      ...(Array.isArray(ice) ? ice.map(e => ({ ...e, eventType: 'ice' })) : []),
      ...(Array.isArray(lowAttach) ? lowAttach.map(e => ({ ...e, eventType: 'low-attachment' })) : [])
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
.coloured-ribbon { height: 3px; width: 100%; background: linear-gradient(90deg, #32281e 0%, #6b4a25 25%, #c19a5b 50%, #FFF6DA 75%, #32281e 100%); }
.top-bar { height: 48px; display: flex; align-items: center; justify-content: space-between; padding: 0 16px; z-index: 10; }
.top-title { font-size: 19.2px; font-weight: 500; color: #FFF6DA; letter-spacing: 2px; font-family: 'Noto Sans SC', sans-serif; }
.s-icon { cursor: pointer; color: #FFF6DA; padding: 6px; display: inline-flex; align-items: center; justify-content: center; border-radius: 4px; transition: background 0.2s; }
.s-icon:hover { background: rgba(255, 246, 218, 0.1); }
.main-area { flex: 1; position: relative; overflow: hidden; }
.left-panel { position: absolute; left: 0; top: 0; height: 100%; width: 43px; background: transparent; transition: width 0.3s; z-index: 5; overflow: hidden; }
.left-panel.expanded { background: rgba(0,0,0,0.8); width: 288px; }
.panel-collapsed { width: 43px; }
.panel-btn { writing-mode: vertical-lr; padding: 16px 12px; cursor: pointer; color: #FFF6DA; font-size: 19.2px; background: #000; font-family: 'Noto Sans SC', sans-serif; }
.panel-content { width: 245px; padding: 12px; color: #c0d0e0; font-size: 13px; }
.panel-section { margin-bottom: 20px; }
.panel-section h4 { font-size: 14px; color: #FFF6DA; margin-bottom: 8px; border-bottom: 1px solid rgba(255,246,218,0.2); padding-bottom: 4px; }
/* P7+ 时间轴：参照原版 11-timeline.png + 设计文档 §3.4
   - 外层黑底圆角浮层（原版有）+ 70% 宽 + 居中 + 距底 5%
   - 两端 "过去23h" / "未来1h" 文字（原版有）
   - 跑道：6 色全光谱渐变（品红→紫→蓝→天蓝→青绿→嫩绿）
   - 24 段 stop 形成视觉分割
   - 滑块：白色圆角胶囊 + 中心三条垂直细线（防滑纹路） */
/* .time 容器：黑底外框 + 圆角 + 70% 宽 + 居中 + 距底 5%（原版结构） */
.time-slider {
  position: absolute;
  bottom: 5%; left: 50%; transform: translateX(-50%);
  display: flex; width: 70%; height: 8%; min-height: 48px;
  align-items: center; padding: 0 16px;
  background: #000;
  border-radius: 4px;
  font-family: 'Noto Sans SC', sans-serif; color: #fff6da; font-weight: 100; z-index: 999;
}
/* 端点文字 */
.time-prefix, .time-suffix {
  font-size: 12px; color: #fff6da;
  text-align: center; flex-shrink: 0;
  padding: 0 8px; margin: 0;
  white-space: nowrap;
}

/* el-slider 占满中间 */
.time-slider .el-slider { flex: 1; min-width: 0; padding: 0 8px; }

/* 跑道：6 色全光谱渐变（参考原版 11-timeline.png） */
.time-slider .el-slider__runway { height: 14px; background-image: linear-gradient(90deg, #c6077a 0%, #7e05d1 25%, #0c87f1 50%, #00d4e0 70%, #75df0a 85%, #b5e84a 100%); border-radius: 3px; }
.time-slider .el-slider__bar { height: 14px; background-color: rgba(64, 158, 255, 0); border-radius: 3px; }
.time-slider .el-slider__runway .el-slider__stop { height: 100%; background-color: #fff6da; border-radius: 0; }
/* marks 文字（HH:00 真实小时）— 位于跑道下方（Element Plus 默认 15px）
   黑底外框已提供足够对比度，不再需要 text-shadow */
.time-slider .el-slider__runway .el-slider__marks-text {
  font-size: 11.66px; margin-top: 15px;
  font-family: 'Noto Sans SC', sans-serif; color: #fff6da;
}
/* 滑块按钮：白色圆角胶囊 + 中心三条垂直细线（防滑纹路，原版视觉特征） */
.time-slider .el-slider__runway .el-slider__button-wrapper { width: 26px; height: 30px; top: -10px; }
.time-slider .el-slider__runway .el-slider__button-wrapper .el-slider__button {
  width: 100%; height: 100%;
  background: #fff;          /* 白色底 */
  border: 0; border-radius: 8px;  /* 圆角胶囊 */
  position: relative;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.15);
}
/* 中心三条垂直细线（防滑纹路，原版视觉特征：等距 3 条细灰线） */
.time-slider .el-slider__runway .el-slider__button::before {
  content: '';
  position: absolute;
  left: 50%; top: 50%;
  transform: translate(-50%, -50%);
  width: 12px; height: 14px;
  /* 三条 1px 宽的灰色竖线，等间距 */
  background: linear-gradient(to right,
    #888 0px, #888 1px,
    transparent 1px, transparent 5px,
    #888 5px, #888 6px,
    transparent 6px, transparent 10px,
    #888 10px, #888 11px,
    transparent 11px, transparent 12px);
}
.time-slider .el-slider__runway .el-slider__button::after { display: none; }
/* tooltip 样式 */
.time-tooltip { font-weight: 700; }
.drawer-grid { display: flex; gap: 16px; height: 100%; background: #090909; color: #FFF6DA; }
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
.el-drawer { background: #090909 !important; }
.el-drawer__header { color: #FFF6DA !important; }
.el-table { background: transparent !important; color: #FFF6DA !important; }
.el-table th.el-table__cell { background: rgba(50,40,30,0.8) !important; color: #FFF6DA !important; }
.el-table td.el-table__cell { background: transparent !important; color: #FFF6DA !important; }
.el-table--striped .el-table__body tr.el-table__row--striped td { background: rgba(255,246,218,0.03) !important; }
.el-select-dropdown { background: #090909 !important; border: 1px solid rgba(255,246,218,0.2) !important; }
.el-select-dropdown__item { color: #FFF6DA !important; }
.el-select-dropdown__item.hover { background: rgba(255,246,218,0.1) !important; }
.el-button--primary { background: linear-gradient(90deg, #32281e, #FFF6DA) !important; border-color: #FFF6DA !important; color: #000 !important; }
.el-button--primary:hover { background: linear-gradient(90deg, #FFF6DA, #32281e) !important; }
.el-divider { background: rgba(255,246,218,0.2) !important; }
.maintain-title { font-size: 12px; color: #FFF6DA; margin: 12px 0 6px 0; opacity: 0.8; }
.maintain-list { font-size: 13px; color: #FFF6DA; min-height: 20px; padding: 4px 0; }
.maintain-list .no-data { color: #666; font-style: italic; }

/* P7-iter.2-1: 自定义 Popup 详情样式 */
.popup-detail { display: flex; flex-direction: column; gap: 8px; }
.popup-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px dashed rgba(255, 246, 218, 0.1); }
.popup-row:last-child { border-bottom: none; }
.popup-label { color: #a0a0a0; font-size: 13px; }
.popup-value { color: #FFF6DA; font-size: 14px; font-weight: 500; }
</style>
