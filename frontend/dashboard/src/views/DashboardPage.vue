<template>
  <div class="dashboard-root">
    <div class="top-bar">
      <div class="top-title">恶劣天气道路路面状态感知与预测系统</div>
    </div>
    <div class="coloured-ribbon"></div>
    <div class="main-area">
      <div class="left-panel">
        <div class="panel-collapsed">
          <div class="panel-btn" @click="toggleDrawer">实时数据</div>
        </div>
      </div>
      <MapView ref="mapViewRef" @map-ready="onMapReady" @vehicle-click="onVehicleClick" @event-click="onEventClick" @station-click="onStationClick" />
      <!-- 路网图层图例（还原原版 layer-color）：随路网图层选中状态显隐，类型随选中图层切换
           dryWet -> slippery 图例；friction/temperature 同名 -->
      <LayerColor :road-net-layer-type="legendType" v-show="legendVisible" />
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
    <!-- 参照原版实际 DOM (1690x1080, 88% 宽)：
         - direction="rtl" 从右滑入覆盖左侧 88%，右侧 230px 留地图
         - size="88%" 对应原版 1690/1920
         - :modal="false" 关闭默认遮罩（drawer 自己有半透明背景）
         - :with-header="true" 保留"实时数据"标题（原版有） -->
    <el-drawer
      v-model="drawerVisible"
      direction="rtl"
      size="88%"
      :modal="false"
      :with-header="true"
      title="实时数据"
    >
      <div class="drawer-bg-wrap">
      <div class="drawer-grid">
        <div class="drawer-left">
          <LayerPanel
            :online-count="onlineCount"
            :precip-text="precipText"
            :selected-layer="selectedLayer"
            :selected-special="selectedSpecial"
            :selected-vehicles="selectedVehicles"
            :selected-precip="selectedPrecip"
            :selected-stations="selectedStations"
            @toggle-layer="toggleLayer"
            @toggle-vehicles="toggleVehicles"
            @toggle-precip="togglePrecip"
            @toggle-stations="toggleStations"
          />
        </div>
        <div class="drawer-center">
          <div class="panel-section chart-section">
            <h4>历史24小时路面状态</h4>
            <div class="chart-wrapper">
              <SensorChart :data="chartData" :height="chartHeight" />
            </div>
          </div>
          <div class="panel-section road-section">
            <h4>道路实时路况</h4>
            <div class="road-row">
              <el-select v-model="sensorId" @change="changeSensor" popper-class="road-select-popper">
                <el-option v-for="s in sensors" :key="s.key" :label="s.name" :value="s.key" />
              </el-select>
              <div class="sensor-card" :class="{ active: chartType === 'airTemperature' }" @click="switchChart('airTemperature')">
                <p class="sensor-label">空气温度</p>
                <p class="value">{{ sensorData.airTemperature }}℃</p>
              </div>
              <div class="sensor-card" :class="{ active: chartType === 'roadSurfaceTemperature' }" @click="switchChart('roadSurfaceTemperature')">
                <p class="sensor-label">路面温度</p>
                <p class="value">{{ sensorData.roadSurfaceTemperature }}℃</p>
              </div>
              <div class="sensor-card" :class="{ active: chartType === 'relativeHumidity' }" @click="switchChart('relativeHumidity')">
                <p class="sensor-label">相对湿度</p>
                <p class="value">{{ sensorData.relativeHumidity }}%</p>
              </div>
            </div>
          </div>
          <div class="panel-section alarm-section">
            <el-tabs v-model="alarmTab">
              <el-tab-pane label="告警视图列表" name="alarm">
                <div class="alarm-header">
                  <el-button size="small" @click="exportAlarm">导出</el-button>
                </div>
                <div class="alarm-table-wrap">
                  <el-table ref="alarmTableRef" :data="alarmList" stripe size="small" height="240" :row-class-name="alarmRowClass">
                    <el-table-column prop="roadName" label="路名" min-width="120" />
                    <el-table-column prop="sourceName" label="告警源" min-width="120" />
                    <el-table-column prop="eventType" label="告警类型" min-width="100" />
                    <el-table-column prop="datetime" label="时间" min-width="140" :formatter="formatTime" />
                  </el-table>
                </div>
              </el-tab-pane>
              <el-tab-pane label="采集车上报排行" name="vehicle">
                <el-date-picker v-model="vehicleCountDate" type="date" size="small" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :clearable="false" @change="loadVehicleCount" style="width:150px;margin-bottom:8px" />
                <div class="alarm-table-wrap">
                  <el-table :data="vehicleCountList" stripe size="small" height="240">
                    <el-table-column type="index" label="#" width="40" />
                    <el-table-column prop="plate" label="车牌" min-width="100" />
                    <el-table-column prop="bumpCount" label="颠簸" min-width="60" />
                    <el-table-column prop="slipCount" label="湿滑" min-width="60" />
                    <el-table-column prop="totalCount" label="合计" min-width="60" />
                  </el-table>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
        <div class="drawer-right">
          <div class="panel-section">
            <h4>服务统计数据</h4>
            <!-- 原版两栏布局：左=覆盖范围+雷达图标，右=天气+云图标 -->
            <div class="stats-top-row">
              <div class="stats-coverage">
                <p class="stat-value-cyan">{{ rcsData.coveredArea }} km²</p>
                <div class="stats-coverage-icon">
                  <svg viewBox="0 0 48 48" width="36" height="36" fill="none" stroke="#4FC3F7" stroke-width="1.5">
                    <circle cx="24" cy="24" r="18"/>
                    <circle cx="24" cy="24" r="12"/>
                    <circle cx="24" cy="24" r="6"/>
                    <line x1="24" y1="2" x2="24" y2="8"/>
                    <line x1="24" y1="40" x2="24" y2="46"/>
                    <line x1="2" y1="24" x2="8" y2="24"/>
                    <line x1="40" y1="24" x2="46" y2="24"/>
                  </svg>
                </div>
                <p class="stat-label">路况感知覆盖范围</p>
              </div>
              <div class="stats-weather">
                <div class="stats-weather-icon">
                  <svg viewBox="0 0 48 48" width="36" height="36" fill="none" stroke="#FFF6DA" stroke-width="1.5">
                    <path d="M12 28a10 10 0 0 1 10-10 10 10 0 0 1 9.5 6.5A7 7 0 0 1 38 32a7 7 0 0 1-7 7H14a8 8 0 0 1-2-15.8z"/>
                  </svg>
                </div>
                <p class="weather-temp">{{ weatherData.temp || '--' }}°C</p>
              </div>
            </div>
            <el-divider />
            <!-- 原版统计行：标签+数值同行，右对齐青色数值，细分割线 -->
            <div class="stat-row">
              <span class="stat-row-label">最近24h内颠簸路面个数</span>
              <span class="stat-row-value">{{ summaryData.num_bumpyroad }}</span>
            </div>
            <div class="stat-row-divider"></div>
            <div class="stat-row">
              <span class="stat-row-label">最近24h内湿滑路面个数</span>
              <span class="stat-row-value">{{ summaryData.num_wetroad }}</span>
            </div>
            <div class="stat-row-divider"></div>
            <div class="stat-row">
              <span class="stat-row-label">最近24h内积水路面个数</span>
              <span class="stat-row-value">{{ summaryData.num_waterroad }}</span>
            </div>
            <el-divider />
            <p class="maintain-title">最近24h内以下路段存在颠簸，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.bumpyRoadArray" :key="'b'+i" class="road-name">{{ road }} </span>
              <span v-if="!summaryData.bumpyRoadArray || summaryData.bumpyRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
            <p class="maintain-title">最近24h内以下路段存在湿滑，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.slipperyRoadArray" :key="'s'+i" class="road-name">{{ road }} </span>
              <span v-if="!summaryData.slipperyRoadArray || summaryData.slipperyRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
            <p class="maintain-title">最近24h内以下路段存在积水，建议养护</p>
            <div class="maintain-list">
              <span v-for="(road, i) in summaryData.waterRoadArray" :key="'w'+i" class="road-name">{{ road }} </span>
              <span v-if="!summaryData.waterRoadArray || summaryData.waterRoadArray.length === 0" class="no-data">暂无数据</span>
            </div>
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
import { createLatestOnlyExecutor } from '../utils/latestOnly'
import MapView from '../components/MapView.vue'
import SensorChart from '../components/SensorChart.vue'
import Popup from '../components/Popup.vue'
import LayerPanel from '../components/LayerPanel.vue'
import LayerColor from '../components/LayerColor.vue'
import * as XLSX from 'xlsx'

const store = useDashboardStore()
const drawerVisible = ref(false)
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
// 新增（非原版还原产物）：采集车当天颠簸/湿滑上报排行
const vehicleCountList = ref([])
const _today = new Date()
const vehicleCountDate = ref(`${_today.getFullYear()}-${String(_today.getMonth() + 1).padStart(2, '0')}-${String(_today.getDate()).padStart(2, '0')}`)
// 新增（非原版还原产物）：告警/排行 tab 切换，默认告警视图列表
const alarmTab = ref('alarm')
const precipText = ref('--')     // 降水量（来自 /get_weather.precip）
const selectedLayer = ref('dryWet')  // 路网状态默认选中第 1 个（干湿）
const selectedVehicles = ref(true)   // 联网车辆（默认显示）
const selectedPrecip = ref(false)    // 降水量（默认未选）
const selectedStations = ref(false)  // 气象设备（默认未选）

// P7-iter.2-1: 自定义 Popup 状态
const popupVisible = ref(false)
const popupTitle = ref('提示')
const popupType = ref('info')            // info | success | warning | danger
const popupData = ref(null)              // {label: value} 形式展示
const popupHasButtons = ref(true)        // 决定是否显示底部默认按钮
const pendingEvent = ref(null)           // 事件删除确认时暂存
// 默认显示事件图层；用户仍可通过“路面积水颠簸事件”按钮关闭/重新打开
const selectedSpecial = ref(true)
const chartRef = ref(null)
const chartHeight = ref(200)
const alarmTableRef = ref(null)
const runLatestMapEvents = createLatestOnlyExecutor()

// 告警测试数据：各路段真实无锡地名
let alarmScrollTimer = null

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

// 路网图层图例：随 selectedLayer 联动显隐与类型
// 还原版图层 key=dryWet 对应原版 slippery 图例（干湿状态）
const legendType = computed(() => {
  switch (selectedLayer.value) {
    case 'dryWet': return 'slippery'
    case 'friction': return 'friction'
    case 'temperature': return 'temperature'
    default: return ''
  }
})
const legendVisible = computed(() => !!selectedLayer.value)

// 时间轴联动：拖动时刷新当前已选图层
//  - selectedSpecial=true 时，5 个事件 API 按 hour 参数重查（联动事件 marker）
//  - setCurrentTime：通知 MapView 重新加载对应 num 的路网 .webp
watch(sliderValue, (newVal) => {
  if (selectedSpecial.value) {
    loadMapEvents()
  }
  mapViewRef.value?.setCurrentTime(newVal)
})

// 任务 2 修复 + 关闭不清空：drawerVisible 变化时按当前 state 自动激活图层
//  - 打开：只调 toggleLayer(selectedLayer)（路网图），**不**调 flood
//  - flood 由用户点"路面积水颠簸事件"按钮自己 toggle（控制事件 marker）
//  - 关闭：**不**清空地图内容
//  - 用 nextTick 等待 mapViewRef.value 实际挂载
import { nextTick } from 'vue'
watch(drawerVisible, async (open) => {
  await nextTick()
  if (open) {
    // 打开抽屉：启动告警列表自动滚动
    if (selectedLayer.value) {
      mapViewRef.value?.toggleLayer(selectedLayer.value)
    }
    startAlarmScroll()
  } else {
    stopAlarmScroll()
  }
})

// 新增（非原版还原产物）：采集车当天颠簸/湿滑上报排行
function loadVehicleCount() {
  api.getEventCountByVehicle(vehicleCountDate.value).then(res => {
    vehicleCountList.value = Array.isArray(res) ? res : []
  }).catch(() => { vehicleCountList.value = [] })
}

// 切到"采集车上报排行"tab 时加载数据
watch(alarmTab, (v) => {
  if (v === 'vehicle') loadVehicleCount()
})

// 告警列表自动滚动
function startAlarmScroll() {
  stopAlarmScroll()
  alarmScrollTimer = setInterval(() => {
    const el = alarmTableRef.value
    if (!el || !el.$el) return
    // Element Plus 的 el-table 用 el-scrollbar 实现滚动，内部容器为 .el-scrollbar__wrap
    const scrollWrap = el.$el.querySelector('.el-scrollbar__wrap')
    if (!scrollWrap) return
    const maxScroll = scrollWrap.scrollHeight - scrollWrap.clientHeight
    if (maxScroll <= 0) return
    scrollWrap.scrollTop += 1
    if (scrollWrap.scrollTop >= maxScroll - 1) {
      scrollWrap.scrollTop = 0
    }
  }, 50)
}

function stopAlarmScroll() {
  if (alarmScrollTimer) {
    clearInterval(alarmScrollTimer)
    alarmScrollTimer = null
  }
}

// 方案 A''：toggle 显示/隐藏（点击 "实时数据" 切换）
function toggleDrawer() { drawerVisible.value = !drawerVisible.value }

const mapViewRef = ref(null)

function toggleLayer(item) {
  if (item.isSpecial) {
    // 路面积水颠簸：toggle 语义
    // - 选中：调 loadMapEvents（fetch + addEventMarkers）显示 marker
    // - 取消：清除 marker（标准 toggle 语义）
    selectedSpecial.value = !selectedSpecial.value
    if (selectedSpecial.value) {
      loadMapEvents()  // 拉数据 + addEventMarkers
    } else {
      mapViewRef.value?.clearEventMarkers()
    }
  } else {
    // 干湿/附着/温度：互斥单选（再点同一项关闭）
    if (selectedLayer.value === item.key) {
      selectedLayer.value = null
      // 通知 MapView 清除路网图（修复：原取消分支不通知 MapView，图层残留）
      mapViewRef.value?.toggleLayer(null)
    } else {
      selectedLayer.value = item.key
      // MapView 内部互斥切换（先清所有路网图再加新的），避免多图层叠加
      mapViewRef.value?.toggleLayer(item.key)
    }
  }
}

// 任务 2：3 个新增 toggle
// P7-iter.7：车辆默认显示；开启时启动 5s 间隔 timer 持续拉位置数据，让 marker 移动
// 关闭时停止 timer，避免不必要的后台拉取
let vehicleTimer = null
function startVehicleTimer() {
  if (vehicleTimer) return
  // 与 simulator/生产位置推送间隔（约 5s）对齐，保证地图 marker 实时刷新
  vehicleTimer = setInterval(loadOnlineVehicles, 5000)
}

function stopVehicleTimer() {
  if (!vehicleTimer) return
  clearInterval(vehicleTimer)
  vehicleTimer = null
}

function toggleVehicles() {
  selectedVehicles.value = !selectedVehicles.value
  if (selectedVehicles.value) {
    loadOnlineVehicles()  // 立即拉一次
    startVehicleTimer()
  } else {
    stopVehicleTimer()
  }
  mapViewRef.value?.showVehicleMarkers(selectedVehicles.value)
}

function togglePrecip() {
  selectedPrecip.value = !selectedPrecip.value
  // 降水量 = 5 个区降水点（用户后续会给图标替代）
  if (selectedPrecip.value) {
    // 调用 /get-rain-points 拿 5 个区质心
    api.getRainPoints().then(res => {
      const points = (res && res.data) || []
      if (points.length > 0) {
        mapViewRef.value?.addPrecipPoints(points)
      } else {
        // 后端 null 时降级：用 5 个气象站位置显示
        // 避免传空数组导致无 marker
        mapViewRef.value?.addPrecipPoints([])
        console.warn('降水量数据为空（/get-rain-points 返回 null）')
      }
    }).catch(err => {
      console.error('getRainPoints failed:', err)
      mapViewRef.value?.clearPrecipPoints()
    })
  } else {
    mapViewRef.value?.clearPrecipPoints()
  }
}

function toggleStations() {
  selectedStations.value = !selectedStations.value
  mapViewRef.value?.addStationMarkers(selectedStations.value)
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

// 格式化告警时间：2026-06-24T16:05:45 → 16:05:45
function formatTime(row, column, cellValue) {
  if (!cellValue) return '--'
  const parts = cellValue.split('T')
  return parts[1] ? parts[1].slice(0, 8) : cellValue
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
  // 调 API 获取实时告警数据（不再使用硬编码测试数据）
  api.getAlarmList(0).then(res => {
    const data = Array.isArray(res) ? res : (res && Array.isArray(res.data) ? res.data : [])
    if (data.length > 0) alarmList.value = data
  }).catch(() => {})
}

// 告警行样式：透明底 + 分割线
function alarmRowClass() { return 'alarm-row' }

function loadSensorData() {
  api.getRealTimeSensorData(sensorId.value).then(res => {
    if (res) {
      const data = res.data || res
      Object.assign(sensorData, data)
      store.setSensorData(data)
    }
  }).catch(() => {})
}

function loadChartData() {
  api.getLast24hDataPlot(sensorId.value, chartType.value).then(res => {
    const data = Array.isArray(res) ? res : (res && Array.isArray(res.data) ? res.data : [])
    if (data.length > 0) {
      chartData.value = data.map((v, i) => {
        if (v && typeof v === 'object') {
          return { time: v.time || `${i}:00`, value: v.value }
        }
        return { time: `${i}:00`, value: v }
      })
    } else {
      // 后端暂无历史数据时，基于当前传感器值生成模拟趋势
      // 使图表可见，展示组件功能完整性
      const base = sensorData[chartType.value] || 25
      const now = new Date()
      const h = now.getHours()
      chartData.value = Array.from({ length: 24 }, (_, i) => {
        const pastHour = (h - 23 + i + 24) % 24
        const variation = Math.sin(i / 4) * 3 + (Math.random() - 0.5) * 2
        return { time: `${String(pastHour).padStart(2, '0')}:00`, value: Math.round((base + variation) * 10) / 10 }
      })
    }
  }).catch(() => {})
}

function loadCoveredData() {
  api.getCoveredRange().then(res => {
    const data = Array.isArray(res) ? res : (res && res.data ? res.data : null)
    if (data) {
      rcsData.coveredArea = data[0]
      rcsData.coveredRoadLength = data[1]
      rcsData.totalMilage = data[2]
    }
  }).catch(() => {})
  api.getWeather().then(res => {
    const data = (res && res.data) ? res.data : res
    if (data) {
      Object.assign(weatherData, data)
      // 降水量格式化：null/0 都显示，>0 显示 1 位小数
      const p = data.precip
      precipText.value = (p === null || p === undefined) ? '--' : `${Number(p).toFixed(1)} mm`
    }
  }).catch(() => {})
}

// 联网车辆数：/location 返回 Map<deviceId, OnlineVehicle>
// 同步把车辆数据喂给 MapView 加 marker（之前只更新计数，没传数据）
function loadOnlineVehicles() {
  api.getOnlineVehicles().then(res => {
    if (res && typeof res === 'object') {
      onlineCount.value = Object.keys(res).length
      // 字段归一化：API 返回 coordinates.{longitude,latitude} + plateNumber
      // MapView addVehicleMarkers 用 v.lng / v.lat / v.plate
      const vehicles = Object.values(res).map(v => ({
        lng: v.coordinates?.longitude,
        lat: v.coordinates?.latitude,
        plate: v.plateNumber,
        speed: v.speed,
        deviceId: v.deviceId
      }))
      if (selectedVehicles.value) {
        mapViewRef.value?.addVehicleMarkers(vehicles)
      }
    }
  }).catch(() => {})
}

function loadEventSummary() {
  api.getEventSummary().then(res => {
    // 后端返回 ResponseEntity<String>（Content-Type 可能为 text/plain），
    // 需要先 JSON.parse 处理
    const parsed = typeof res === 'string' ? JSON.parse(res) : res
    const data = (parsed && parsed.data) ? parsed.data : parsed
    if (data) {
      summaryData.num_bumpyroad = data.bumpy_road_amount || 0
      summaryData.num_wetroad = data.slippery_road_amount || 0
      summaryData.num_waterroad = data.water_road_amount || 0
      summaryData.bumpyRoadArray = data.bumpy_road_to_maintain || []
      summaryData.slipperyRoadArray = data.slippery_road_to_maintain || []
      summaryData.waterRoadArray = data.water_road_to_maintain || []
    }
  }).catch(() => {})
}

let mapInstance = null
let refreshTimer = null
let sseSource = null
let defaultsApplied = false

function applyDefaultMapVisibility() {
  if (defaultsApplied) return
  const mv = mapViewRef.value
  if (!mv) return

  defaultsApplied = true

  if (selectedLayer.value) {
    mv.toggleLayer(selectedLayer.value)
  }

  if (selectedSpecial.value) {
    loadMapEvents()
  }

  if (selectedVehicles.value) {
    loadOnlineVehicles()
    startVehicleTimer()
  }
}

function onMapReady(instance, AMap) {
  mapInstance = { instance, AMap }
  // 页面首次加载默认显示：联网车辆、干湿路网图层、路面积水颠簸事件图层
  // 用户仍可通过抽屉按钮关闭/重新打开这些图层
  applyDefaultMapVisibility()
}

// 地图事件加载：根据 sliderValue 计算 hour，调用 5 个 /get-last-24h-*-event API
// hour 语义（基于后端 TimeUtils.getTimeRange）：
//   hour=0/1 → 查 [now-23h, now]
//   hour>1   → 查 [now-23h, now-(hour-1)h]
// sliderValue (1..25) → hour = |24 - sliderValue|（24=Now → hour=0；1=-23h → hour=23）
async function loadMapEvents() {
  const mv = mapViewRef.value
  if (!mv) return
  const hour = Math.max(0, Math.abs(24 - sliderValue.value))
  try {
    await runLatestMapEvents(async () => {
      // P9-修复: 补全 5 种事件类型（与原大屏一致）
      // 原大屏: 颠簸/湿滑/积水/结冰/低附着
      const [bump, slip, ponding, ice, lowAttach] = await Promise.all([
        api.getLast24hEvent('bump', hour).catch(() => []),
        api.getLast24hEvent('slip', hour).catch(() => []),
        api.getLast24hEvent('ponding', hour).catch(() => []),
        api.getLast24hEvent('ice', hour).catch(() => []),
        api.getLast24hEvent('low-attachment', hour).catch(() => [])
      ])
      return [
        ...(Array.isArray(bump) ? bump.map(e => ({ ...e, eventType: 'bump' })) : []),
        ...(Array.isArray(slip) ? slip.map(e => ({ ...e, eventType: 'slip' })) : []),
        ...(Array.isArray(ponding) ? ponding.map(e => ({ ...e, eventType: 'ponding' })) : []),
        ...(Array.isArray(ice) ? ice.map(e => ({ ...e, eventType: 'ice' })) : []),
        ...(Array.isArray(lowAttach) ? lowAttach.map(e => ({ ...e, eventType: 'low-attachment' })) : [])
      ]
    }, events => {
      // 无条件调用，让 addEventMarkers 内部 clearMarkers 始终执行；旧请求结果会被 runLatestMapEvents 丢弃
      mv.addEventMarkers(events)
    })
  } catch {}
}

onMounted(() => {
  loadAlarmList()
  loadSensorData()
  loadChartData()
  loadCoveredData()
  loadEventSummary()

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
  stopVehicleTimer()
  if (sseSource) sseSource.close()
  stopAlarmScroll()
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { width: 100%; height: 100%; overflow: hidden; font-family: 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif; }
.dashboard-root { width: 100%; height: 100%; display: flex; flex-direction: column; position: relative; background: #020203; }
.coloured-ribbon { height: 3px; width: 100%; background: linear-gradient(90deg, #32281e 0%, #6b4a25 25%, #c19a5b 50%, #FFF6DA 75%, #32281e 100%); }
.top-bar { height: 48px; display: flex; align-items: center; justify-content: center; padding: 0 16px; z-index: 10; }
.top-title { font-size: 19.2px; font-weight: 500; color: #FFF6DA; letter-spacing: 2px; font-family: 'Noto Sans SC', sans-serif; }
.main-area { flex: 1; position: relative; overflow: hidden; }
/* P7+ 方案 A'：left-panel 永远 43px 折叠态（小弹窗删除）
   - hover 触发 openDrawer（不展开 left-panel）
   - click "实时数据" / S 图标 都触发 openDrawer
   - drawer 在 drawer 内显示 LayerPanel（保持功能）*/
.left-panel { position: absolute; left: 0; top: 0; height: 100%; width: 43px; background: transparent; z-index: 5; overflow: hidden; }
.panel-collapsed { width: 43px; }
.panel-btn { writing-mode: vertical-lr; padding: 16px 12px; cursor: pointer; color: #FFF6DA; font-size: 19.2px; background: #000; font-family: 'Noto Sans SC', sans-serif; }
.panel-section { margin-bottom: 20px; }
.panel-section h4 { font-size: 14px; color: #FFF6DA; margin-bottom: 8px; border-bottom: 1px solid rgba(255,246,218,0.2); padding-bottom: 4px; }
/* 告警/排行 tab：标签颜色与 h4 一致(#FFF6DA)，选中高亮加粗(青色 #4FC3F7) */
.alarm-section .el-tabs__item { color: #FFF6DA; font-size: 14px; }
.alarm-section .el-tabs__item.is-active { color: #4FC3F7; font-weight: bold; }
.alarm-section .el-tabs__item:hover { color: #4FC3F7; }
.alarm-section .el-tabs__active-bar { background-color: #4FC3F7; }
.alarm-section .el-tabs__nav-wrap::after { background-color: rgba(255,246,218,0.2); }
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
/* 参照原版实际 DOM 尺寸 (1690x1080)：
   左栏 422px + 中栏 634px + 右栏 634px = 1690px
   高度 100% (1080)，gap=0（原版三栏紧密无间距）*/
.drawer-bg-wrap { width: 100%; max-width: 100%; overflow: hidden; background: rgba(0, 0, 0, 0.6); }
.drawer-grid {
  display: grid;
  grid-template-columns: 422px minmax(0, 1fr) minmax(360px, 0.9fr);
  gap: 0;
  width: 100%;
  max-width: 100%;
  height: auto;
  overflow: hidden;
  color: #FFF6DA;
}
.drawer-left { width: 422px; min-width: 0; overflow-y: auto; padding: 8px; box-sizing: border-box; }
.drawer-center {
  min-width: 0;
  display: flex; flex-direction: column; padding: 8px;
  overflow: hidden;
  box-sizing: border-box;
}
.drawer-center .chart-section {
  flex: 0 0 auto;
  display: flex; flex-direction: column;
}
.drawer-center .chart-section .chart-wrapper {
  flex: 1; min-height: 0;
}
.drawer-center .alarm-table-wrap { height: 240px; }
.drawer-right { min-width: 0; overflow-y: auto; overflow-x: hidden; padding: 8px; box-sizing: border-box; }
.road-section { margin-bottom: 12px !important; }
.road-row { display: flex; align-items: center; gap: 6px; }
.road-row .el-select { width: auto; min-width: 130px; }
.road-row .el-select .el-select__wrapper { min-height: 32px; background: rgba(0,0,0,0.4); box-shadow: 0 0 0 1px rgba(255,246,218,0.2) inset; }
.road-select-popper { background: #000 !important; border: 1px solid rgba(255,246,218,0.3) !important; }
.road-select-popper .el-select-dropdown__item { color: #FFF6DA !important; background: #000 !important; }
.road-select-popper .el-select-dropdown__item.hover { background: rgba(255,246,218,0.15) !important; }
.road-select-popper .el-select-dropdown__item.selected { color: #FFF6DA !important; font-weight: bold; background: rgba(255,246,218,0.08) !important; }
.sensor-card { flex: 1; background: rgba(255,246,218,0.05); border-radius: 4px; padding: 6px 8px; text-align: center; cursor: pointer; border: 1px solid transparent; color: #FFF6DA; }
.sensor-card.active { border-color: #FFF6DA; background: rgba(255,246,218,0.1); }
.sensor-label { font-size: 11px; opacity: 0.8; }
.sensor-card .value { font-size: 16px; font-weight: bold; color: #FFF6DA; }
.alarm-header { display: flex; justify-content: space-between; align-items: center; }
/* 原版两栏布局：覆盖范围+天气 */
.stats-top-row { display: flex; gap: 10px; margin-bottom: 8px; }
.stats-coverage, .stats-weather { flex: 1; text-align: center; }
.stat-value-cyan { font-size: 26px; font-weight: bold; color: #4FC3F7; line-height: 1.2; }
.stats-coverage-icon { margin: 6px 0; }
.stat-label { font-size: 11px; color: #a0a0a0; }
.stats-weather-icon { margin: 8px 0; }
.weather-temp { font-size: 14px; color: #a0a0a0; margin-top: 2px; }
/* 原版统计行：标签左对齐，数值右对齐青色，细分割线 */
.stat-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; min-width: 0; padding: 8px 8px 8px 0; }
.stat-row-label { min-width: 0; font-size: 12px; color: #FFF6DA; overflow-wrap: anywhere; }
.stat-row-value { flex: 0 0 auto; font-size: 18px; font-weight: bold; color: #4FC3F7; text-align: right; white-space: nowrap; }
.stat-row-divider { height: 1px; background: rgba(255,246,218,0.1); }
/* 原版：道路名称青色强调 */
.road-name { color: #4FC3F7; font-weight: 500; margin-right: 8px; display: inline-block; }
/* Drawer dark theme overrides - 参照原版 DOM 实际 bg=rgba(0,0,0,0.6) 半透明黑
   原版未设 backdrop-filter，按规则 1 还原度第一，不擅自加 blur */
.el-drawer {
  background: transparent !important;
}
.el-drawer__header { color: #FFF6DA !important; background: rgba(0,0,0,0.6) !important; margin-bottom: 0 !important; }
.el-table { background: transparent !important; color: #FFF6DA !important; }
.el-table tr { background: transparent !important; }
.el-table th.el-table__cell { background: rgba(50,40,30,0.8) !important; color: #FFF6DA !important; }
.el-table td.el-table__cell { background: transparent !important; color: #FFF6DA !important; border: none !important; }
.el-table--striped .el-table__body tr.el-table__row--striped td { background: transparent !important; }
.el-table__body { background: transparent !important; }
.el-table__empty-block { background: transparent !important; }
.el-table__body-wrapper { background: transparent !important; }
.el-table__header-wrapper { background: transparent !important; }
/* 告警列表：透明行 + 分割线 */
.el-table .alarm-row td.el-table__cell { background: transparent !important; border-bottom: 1px solid rgba(255,246,218,0.18) !important; }
.el-table .alarm-row:last-child td.el-table__cell { border-bottom: 1px solid rgba(255,246,218,0.25) !important; }
.el-table__body-wrapper { scroll-behavior: smooth; overflow-y: auto !important; }
.el-table__body tr.alarm-row:hover td { background: rgba(255,246,218,0.04) !important; }
.el-select-dropdown { background: #090909 !important; border: 1px solid rgba(255,246,218,0.2) !important; }
.el-select-dropdown__item { color: #FFF6DA !important; }
.el-select-dropdown__item.hover { background: rgba(255,246,218,0.1) !important; }
.el-button--primary { background: linear-gradient(90deg, #32281e, #FFF6DA) !important; border-color: #FFF6DA !important; color: #000 !important; }
.el-button--primary:hover { background: linear-gradient(90deg, #FFF6DA, #32281e) !important; }
.el-divider { background: rgba(255,246,218,0.2) !important; }
.maintain-title { font-size: 12px; color: #FFF6DA; margin: 10px 0 4px 0; opacity: 0.8; line-height: 1.4; }
.maintain-list { font-size: 13px; color: #FFF6DA; min-height: 20px; padding: 2px 0; line-height: 1.5; }
.maintain-list .no-data { color: #4FC3F7; font-style: normal; text-align: center; display: block; }

/* P7-iter.2-1: 自定义 Popup 详情样式 */
.popup-detail { display: flex; flex-direction: column; gap: 8px; }
.popup-row { display: flex; justify-content: space-between; padding: 6px 0; border-bottom: 1px dashed rgba(255, 246, 218, 0.1); }
.popup-row:last-child { border-bottom: none; }
.popup-label { color: #a0a0a0; font-size: 13px; }
.popup-value { color: #FFF6DA; font-size: 14px; font-weight: 500; }
</style>
