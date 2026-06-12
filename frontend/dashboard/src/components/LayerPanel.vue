<template>
  <div class="layer-panel">
    <!-- 实时车队数据 -->
    <div class="panel-section">
      <h4>实时车队数据</h4>
      <p class="fleet-count">联网车辆 <span class="num">{{ onlineCount }}</span> 辆</p>
    </div>

    <!-- 路网状态 -->
    <div class="panel-section">
      <h4>路网状态</h4>
      <div
        v-for="item in roadNetLayers"
        :key="item.key"
        class="layer-option"
        :class="{ active: selectedLayer === item.key && !item.isSpecial, coexist: item.isSpecial && selectedSpecial }"
        @click="toggleLayer(item)"
      >
        <span>{{ item.label }}</span>
      </div>
    </div>

    <!-- 实时气象数据 -->
    <div class="panel-section">
      <h4>实时气象数据</h4>
      <p>降水量 <span class="num">{{ precipText }}</span></p>
      <el-button text type="primary" @click="emit('show-weather-device')">查看气象设备</el-button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  onlineCount: { type: Number, default: 0 },
  precipText: { type: String, default: '--' },
  selectedLayer: { type: String, required: true },
  selectedSpecial: { type: Boolean, required: true }
})

const emit = defineEmits(['toggle-layer', 'show-weather-device'])

const roadNetLayers = [
  { key: 'dryWet', label: '路面干湿状态图层', isSpecial: false },
  { key: 'friction', label: '路面附着系数图层', isSpecial: false },
  { key: 'temperature', label: '路面温度状态图层', isSpecial: false },
  { key: 'flood', label: '路面积水颠簸事件', isSpecial: true }
]

function toggleLayer(item) {
  emit('toggle-layer', item)
}
</script>

<style scoped>
.layer-panel { color: #c0d0e0; font-size: 13px; }

.panel-section { margin-bottom: 20px; }
.panel-section h4 {
  font-size: 14px;
  color: #FFF6DA;
  margin-bottom: 8px;
  border-bottom: 1px solid rgba(255, 246, 218, 0.2);
  padding-bottom: 4px;
}
.layer-option {
  padding: 6px 8px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 2px;
  color: #a0b0c0;
  transition: background 0.15s;
}
.layer-option:hover { background: rgba(255, 246, 218, 0.05); }
.layer-option.active { background: linear-gradient(90deg, #32281e, #FFF6DA); color: #FFF6DA; }
.layer-option.coexist { background: linear-gradient(90deg, #2d3a1e, #67C23A); color: #FFF6DA; }

.fleet-count { color: #FFF6DA; font-size: 16px; }
.num { color: #FFF6DA; font-size: 18px; font-weight: 600; margin: 0 2px; }
</style>
