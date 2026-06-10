<template>
  <div class="dashboard-page">
    <!-- 顶部标题栏 -->
    <div class="header">
      <h1>道路检测与预警平台</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="main-content">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <div class="panel-section">
          <h3>事件统计</h3>
          <div class="stat-item" v-for="s in eventStats" :key="s.label">
            <span class="stat-label">{{ s.label }}</span>
            <span class="stat-value" :style="{color: s.color}">{{ s.value }}</span>
          </div>
        </div>
      </div>

      <!-- 中间地图区域 -->
      <div class="center-panel" id="map-container">
        <div class="map-placeholder">
          <el-empty description="高德地图 API (需配置 Key)"></el-empty>
          <p style="color:#999;text-align:center">实时车辆位置、道路事件可视化</p>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <div class="panel-section">
          <h3>实时告警</h3>
          <div v-if="alarms.length === 0" style="color:#999;padding:20px;text-align:center">暂无告警</div>
          <div v-for="a in alarms" :key="a.id" class="alarm-item" :class="a.level">
            <span class="alarm-type">{{ a.type }}</span>
            <span class="alarm-road">{{ a.road }}</span>
            <span class="alarm-time">{{ a.time }}</span>
          </div>
        </div>
        <div class="panel-section">
          <h3>天气信息</h3>
          <div v-if="weather" class="weather-info">
            <div>{{ weather.temp }}°C</div>
            <div>湿度: {{ weather.humidity }}%</div>
            <div>{{ weather.text }}</div>
          </div>
          <div v-else style="color:#999;padding:20px;text-align:center">加载中...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
const API = 'http://localhost:50410/spring/v1'

export default {
  name: 'DashboardPage',
  data() {
    return {
      currentTime: '',
      timer: null,
      eventStats: [
        { label: '颠簸事件', value: 0, color: '#F56C6C' },
        { label: '湿滑事件', value: 0, color: '#E6A23C' },
        { label: '积水事件', value: 0, color: '#409EFF' },
        { label: '结冰事件', value: 0, color: '#67C23A' },
      ],
      alarms: [],
      weather: null
    }
  },
  mounted() {
    this.updateTime()
    this.timer = setInterval(this.updateTime, 1000)
    this.fetchWeather()
    this.fetchEvents()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    updateTime() {
      this.currentTime = new Date().toLocaleString('zh-CN')
    },
    async fetchWeather() {
      try {
        const r = await axios.get(API + '/get_weather')
        if (r.data && r.data.data) {
          this.weather = r.data.data
        }
      } catch {}
    },
    async fetchEvents() {
      try {
        const r = await axios.get(API + '/external/getEventSummary/2026-06-09T00:00:00/2026-06-11T00:00:00')
        if (r.data && r.data.data) {
          // Update stats based on events
          console.log('Events:', r.data.data)
        }
      } catch {}
    }
  }
}
</script>

<style scoped>
.dashboard-page { width:100%; height:100%; background:#0a1a3a; color:#fff; display:flex; flex-direction:column }
.header { height:60px; background:linear-gradient(135deg,#0d2247,#1a3a6a); display:flex; align-items:center; justify-content:space-between; padding:0 24px; border-bottom:2px solid #2a5aaa }
.header h1 { font-size:22px; margin:0 }
.header-time { font-size:16px; color:#8cf }
.main-content { flex:1; display:flex; overflow:hidden }
.left-panel, .right-panel { width:250px; background:rgba(13,34,71,0.9); padding:12px; overflow-y:auto }
.center-panel { flex:1; display:flex; align-items:center; justify-content:center }
.map-placeholder { text-align:center }
.panel-section { margin-bottom:16px; background:rgba(26,58,106,0.5); border-radius:8px; padding:12px }
.panel-section h3 { margin:0 0 12px; font-size:14px; color:#8cf; border-bottom:1px solid #2a5aaa; padding-bottom:8px }
.stat-item { display:flex; justify-content:space-between; padding:6px 0; border-bottom:1px solid rgba(42,90,170,0.3) }
.stat-value { font-size:20px; font-weight:bold }
.alarm-item { padding:8px; margin-bottom:6px; border-radius:4px; font-size:13px }
.alarm-item.urgent { background:rgba(245,108,108,0.2); border-left:3px solid #F56C6C }
.alarm-item.warning { background:rgba(230,162,60,0.2); border-left:3px solid #E6A23C }
.alarm-type { font-weight:bold; margin-right:8px }
.alarm-road { color:#8cf }
.alarm-time { float:right; color:#999; font-size:12px }
.weather-info { font-size:14px; line-height:1.8 }
</style>
