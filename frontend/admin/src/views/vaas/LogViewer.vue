<template>
  <div>
    <el-alert title="选择设备后, 点击Enable把设备加入Debug列表, 点击接收日志启动接收" type="warning" :closable="false" class="mb-4" />
    <div class="mb-4">
      <el-select v-model="selectedDevice" placeholder="选择设备" style="width:300px"><el-option label="设备1" value="dev1" /><el-option label="设备2" value="dev2" /></el-select>
      <el-button-group class="ml-2">
        <el-button :type="!debugEnabled?'primary':''" @click="toggleDebug(true)">Enable Debug</el-button>
        <el-button :type="debugEnabled?'danger':''" @click="toggleDebug(false)">Disable Debug</el-button>
      </el-button-group>
    </div>
    <el-button type="primary" @click="startLog" class="mr-2">接收日志</el-button>
    <el-button @click="stopLog">停止接收</el-button>
    <el-button @click="clearLog">清空日志</el-button>
    <el-input v-model="logContent" type="textarea" :rows="20" class="w-full mt-2" readonly />
  </div>
</template>
<script setup>
import { ref } from 'vue'
const selectedDevice = ref('')
const logContent = ref('')
const debugEnabled = ref(false)
const toggleDebug = (e) => { debugEnabled.value = e }
const startLog = () => { logContent.value += '日志接收中...\n' }
const stopLog = () => { logContent.value += '日志已停止\n' }
const clearLog = () => { logContent.value = '' }
</script>