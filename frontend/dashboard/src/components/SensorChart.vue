<template>
  <div ref="chartRef" class="sensor-chart" :style="{ height: typeof height === 'number' ? height + 'px' : height }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Array, default: () => [] },
  title: { type: String, default: '' },
  height: { type: [Number, String], default: 300 }
})

const chartRef = ref(null)
let chart = null
let resizeObserver = null
let resizeHandler = null

function renderChart() {
  if (!chart) return
  const hasData = props.data.length > 0
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: hasData ? props.data.map(d => d.time || '') : [],
      axisLabel: {
        fontSize: 10,
        color: '#a0a0a0',
        interval: 0,
        // 参照原版：只显示 5 个关键相对标签 -23h / -18h / -12h / -6h / now
        formatter: (value, index) => {
          const markers = [0, 5, 11, 17, 23]
          const labels = ['-23h', '-18h', '-12h', '-6h', 'now']
          const idx = markers.indexOf(index)
          return idx >= 0 ? labels[idx] : ''
        }
      },
      axisLine: { lineStyle: { color: 'rgba(255,246,218,0.15)' } },
      splitLine: { show: false }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 40,
      interval: 10,
      axisLabel: { fontSize: 10, color: '#a0a0a0' },
      splitLine: { lineStyle: { type: 'dashed', color: 'rgba(255,246,218,0.08)' } }
    },
    series: [{
      data: hasData ? props.data.map(d => d.value) : [],
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 4,
      lineStyle: { width: 2, color: '#4FC3F7' },
      itemStyle: { color: '#4FC3F7' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(79,195,247,0.25)' },
          { offset: 1, color: 'rgba(79,195,247,0.02)' }
        ])
      }
    }]
  })
  // 延迟 resize 确保容器已布局完成
  requestAnimationFrame(() => chart?.resize())
}

function initChart() {
  if (!chartRef.value) return
  chart = echarts.init(chartRef.value)
  // 先用空 option 初始化让 canvas 按容器尺寸渲染
  chart.setOption({}, true)
  // 等待一帧确保容器尺寸已定
  requestAnimationFrame(() => {
    chart?.resize()
    renderChart()
  })
}

watch(() => props.data, () => {
  nextTick(renderChart)
}, { deep: true })

watch(() => props.height, () => {
  nextTick(() => chart?.resize())
})

onMounted(() => {
  // 若容器不可见（抽屉未打开），由 ResizeObserver 在尺寸变化时初始化
  const el = chartRef.value
  if (el && el.clientWidth > 0 && el.clientHeight > 0) {
    initChart()
  }
  // ResizeObserver 监听容器尺寸变化：抽屉打开时自动完成首次渲染
  if (window.ResizeObserver && el) {
    resizeObserver = new ResizeObserver(() => {
      if (!chart && el.clientWidth > 0 && el.clientHeight > 0) {
        initChart()
      } else {
        chart?.resize()
      }
    })
    resizeObserver.observe(el)
  }
  resizeHandler = () => chart?.resize()
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  if (resizeObserver) { resizeObserver.disconnect(); resizeObserver = null }
  if (chart) { chart.dispose(); chart = null }
  if (resizeHandler) { window.removeEventListener('resize', resizeHandler); resizeHandler = null }
})
</script>

<style scoped>
.sensor-chart { width: 100%; height: 100%; min-height: 200px; }
</style>
