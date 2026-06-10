<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="(item,i) in stats" :key="i">
        <el-card style="margin-bottom:16px">
          <h3>{{item.label}}</h3>
          <p style="font-size:28px;color:#409EFF;margin:8px 0">{{item.value}}</p>
        </el-card>
      </el-col>
    </el-row>
    <el-card>
      <template #header><span>系统概览</span></template>
      <p>VaaS 城市级道路状态感知与预警系统</p>
      <p>状态: <el-tag type="success">运行中</el-tag></p>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
const stats = ref([
  { label:'车辆总数', value:150 }, { label:'在线设备', value:128 },
  { label:'今日事件', value:47 }, { label:'告警数', value:3 },
])
</script>
