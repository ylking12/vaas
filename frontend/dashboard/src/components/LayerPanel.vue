<!--
  LayerPanel - 左侧弹框图层面板（7 个 toggle 按钮）
  按用户原话设计：
  - 左上"实时车队数据"：联网车辆（独立 toggle）
  - 左中"路网状态"：前 3 个（干湿/附着/温度）互斥单选 + 第 4 个积水颠簸独立（可与上述同时）
    默认进入弹框：第 1 个 + 第 4 个选中
  - 左下"实时气象数据"：降水量 + 查看气象设备（独立，都默认未选）
-->
<template>
  <div class="layer-panel">
    <!-- 左上：实时车队数据 -->
    <div class="panel-section">
      <h4>实时车队数据</h4>
      <div
        class="layer-option"
        :class="{ active: selectedVehicles }"
        @click="emit('toggle-vehicles')"
      >
        联网车辆
      </div>
    </div>

    <!-- 左中：路网状态 -->
    <div class="panel-section">
      <h4>路网状态</h4>
      <div
        v-for="item in roadNetLayers"
        :key="item.key"
        class="layer-option"
        :class="{
          active: item.isSpecial
            ? selectedSpecial
            : selectedLayer === item.key
        }"
        @click="emit('toggle-layer', item)"
      >
        <span>{{ item.label }}</span>
      </div>
    </div>

    <!-- 左下：实时气象数据 -->
    <div class="panel-section">
      <h4>实时气象数据</h4>
      <div
        class="layer-option"
        :class="{ active: selectedPrecip }"
        @click="emit('toggle-precip')"
      >
        降水量
      </div>
      <div
        class="layer-option"
        :class="{ active: selectedStations }"
        @click="emit('toggle-stations')"
      >
        查看气象设备
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  selectedLayer: { type: String, required: true },
  selectedSpecial: { type: Boolean, required: true },
  selectedVehicles: { type: Boolean, required: true },
  selectedPrecip: { type: Boolean, required: true },
  selectedStations: { type: Boolean, required: true }
})

const emit = defineEmits(['toggle-layer', 'toggle-vehicles', 'toggle-precip', 'toggle-stations'])

const roadNetLayers = [
  { key: 'dryWet', label: '路面干湿状态图层', isSpecial: false },
  { key: 'friction', label: '路面附着系数图层', isSpecial: false },
  { key: 'temperature', label: '路面温度状态图层', isSpecial: false },
  { key: 'flood', label: '路面积水颠簸事件', isSpecial: true }
]
</script>

<style scoped>
/* LayerPanel 样式（任务 2 修复：参照原版大屏视觉） */
.layer-panel {
  color: #c0d0e0;
  font-size: 13px;
  padding: 4px;
}

.panel-section { margin-bottom: 16px; }
.panel-section h4 {
  font-size: 13px;
  font-weight: 600;        /* 粗体（原版特征）*/
  color: #FFF6DA;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid rgba(255, 246, 218, 0.15);
  letter-spacing: 1px;
}

/* 按钮：原版特征 — 透明底 + 白边 + 大圆角（胶囊） */
.layer-option {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 16px;       /* 大圆角（原版 15-20px）*/
  margin-bottom: 6px;
  color: #ffffff;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.4);  /* 细白边 */
  transition: all 0.15s ease;
  user-select: none;
  font-size: 13px;
}
.layer-option:hover {
  background: rgba(255, 246, 218, 0.08);
  border-color: rgba(255, 246, 218, 0.6);
}

/* 选中态：原版特征 — 浅金渐变 + 深色文字 */
.layer-option.active {
  background: linear-gradient(90deg, rgba(230, 215, 184, 0.9) 0%, rgba(255, 246, 218, 0.4) 100%);
  color: #1a1a1a;          /* 深色文字 */
  border-color: rgba(230, 215, 184, 0.9);
  font-weight: 500;
}
</style>
