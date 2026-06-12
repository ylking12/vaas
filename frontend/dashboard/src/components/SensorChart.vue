<template>
  <div ref="chartRef" style="width:100%;height:200px"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Array, default: () => [] },
  title: { type: String, default: '' }
})

const chartRef = ref(null)
let chart = null

function renderChart() {
  if (!chart || !props.data.length) return
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: props.data.map(d => d.time || ''), axisLabel: { fontSize: 10 } },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed' } } },
    series: [{
      data: props.data.map(d => d.value),
      type: 'line',
      smooth: true,
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.1 }
    }]
  })
}

watch(() => props.data, renderChart, { deep: true })

onMounted(() => {
  chart = echarts.init(chartRef.value)
  renderChart()
  window.addEventListener('resize', () => chart?.resize())
})

onBeforeUnmount(() => {
  if (chart) { chart.dispose(); chart = null }
})
</script>
