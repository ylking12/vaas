<template>
  <div>
    <el-form :model="searchForm" label-width="120px">
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="设备编号："><el-input v-model="searchForm.deviceId" placeholder="请输入设备编号" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="KT710序列号："><el-input v-model="searchForm.kt710Id" placeholder="请输入KT710序列号" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="车牌："><el-input v-model="searchForm.plate" placeholder="请输入车牌" /></el-form-item></el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="SIM："><el-input v-model="searchForm.simId" placeholder="请输入SIM" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="组号："><el-input v-model="searchForm.groupId" placeholder="请输入组号" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="联系方式："><el-input v-model="searchForm.phoneNumber" placeholder="请输入联系方式" /></el-form-item></el-col>
      </el-row>
      <el-form-item><el-button type="primary">搜索</el-button><el-button>重置</el-button></el-form-item>
    </el-form>
    <div><el-button type="primary">新增车辆</el-button><el-button>导出Excel</el-button></div>
    <el-table :data="tableData" border stripe class="w-full mt-2">
      <el-table-column prop="kt710Id" label="KT710" min-width="130" />
      <el-table-column prop="plate" label="车牌" min-width="130" sortable />
      <el-table-column label="设备编号" min-width="140"><template #default="{row}">{{row.imei}}</template></el-table-column>
      <el-table-column prop="brandModel" label="车型" />
      <el-table-column label="颠簸" width="90"><template #default="{row}"><el-tag :type="row.bumpEnable?'success':'info'">{{row.bumpEnable?'启用':'禁用'}}</el-tag></template></el-table-column>
      <el-table-column label="湿滑" width="90"><template #default="{row}"><el-tag :type="row.slipEnable?'success':'info'">{{row.slipEnable?'启用':'禁用'}}</el-tag></template></el-table-column>
      <el-table-column label="拒收" width="90"><template #default="{row}"><el-tag :type="row.reject?'danger':'info'">{{row.reject?'是':'否'}}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small">修改</el-button>
          <el-popconfirm title="确定删除？"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api'
const tableData = ref([]), total = ref(0)
const searchForm = reactive({ deviceId:'', kt710Id:'', plate:'', brandModel:'', simId:'', groupId:'', phoneNumber:'' })
onMounted(async () => {
  try { const r = await request.post('/admin/list', { pagination:{ currentPage:1, pageSize:10 } }); tableData.value = r.data?.fleetManagementList || []; total.value = r.data?.pagination?.total || 0 }
  catch { tableData.value = [] }
})
</script>