import { defineStore } from 'pinia'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    selectBur: 1,       // 时间选择: 1=现在, 2=1小时前
    sensorData: {}      // 当前传感器数据
  }),
  getters: {
    currentHour: (state) => state.selectBur
  },
  actions: {
    setSelectBur(val) { this.selectBur = val },
    setSensorData(data) { this.sensorData = data }
  }
})
