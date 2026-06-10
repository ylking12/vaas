<template>
  <div>
    <el-descriptions :column="6" border class="mb-8">
      <el-descriptions-item label="峰值KT">{{ stats.maxKtOnline }}</el-descriptions-item>
      <el-descriptions-item label="峰值六轴">{{ stats.maxMotionOnline }}</el-descriptions-item>
      <el-descriptions-item label="峰值GPS">{{ stats.maxLocationOnline }}</el-descriptions-item>
      <el-descriptions-item label="当前KT">{{ stats.currentKtOnline }}</el-descriptions-item>
      <el-descriptions-item label="当前六轴">{{ stats.currentMotionOnline }}</el-descriptions-item>
      <el-descriptions-item label="当前GPS">{{ stats.currentLocationOnline }}</el-descriptions-item>
    </el-descriptions>
    <el-table :data="tableData" border stripe>
      <el-table-column prop="deviceId" label="设备ID" width="160" />
      <el-table-column label="KT710在线" width="120"><template #default="{row}"><el-tag :type="row.ktOnline?'success':'danger'" size="small">{{row.ktOnline?'在线':'离线'}}</el-tag> {{row.ktLastOnlineTime}}</template></el-table-column>
      <el-table-column label="六轴在线" width="120"><template #default="{row}"><el-tag :type="row.motionOnline?'success':'danger'" size="small">{{row.motionOnline?'在线':'离线'}}</el-tag> {{row.motionLastOnlineTime}}</template></el-table-column>
      <el-table-column label="GPS在线" width="120"><template #default="{row}"><el-tag :type="row.locationOnline?'success':'danger'" size="small">{{row.locationOnline?'在线':'离线'}}</el-tag> {{row.locationLastOnlineTime}}</template></el-table-column>
      <el-table-column prop="phoneNumber" label="手机号" width="130" />
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api'
const tableData = ref([])
const stats = ref({ maxKtOnline:0, maxMotionOnline:0, maxLocationOnline:0, currentKtOnline:0, currentMotionOnline:0, currentLocationOnline:0 })
onMounted(async () => {
  try { const r = await request.get('/admin/heartbeat'); stats.value = r.data || stats.value; tableData.value = r.data?.heartbeatInfoList || [] }
  catch {}
})
</script>