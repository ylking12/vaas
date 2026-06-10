<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div style="height:60px;line-height:60px;text-align:center;color:#fff;font-size:18px;font-weight:bold">VaaS车辆后台</div>
      <el-menu :router="true" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF" default-active="/welcome">
        <el-menu-item index="/welcome"><el-icon><HomeFilled /></el-icon><span>首页</span></el-menu-item>
        <el-menu-item index="/vaas/car-mapping"><el-icon><Connection /></el-icon><span>车辆绑定</span></el-menu-item>
        <el-menu-item index="/vaas/heartbeat"><el-icon><Monitor /></el-icon><span>心跳管理</span></el-menu-item>
        <el-menu-item index="/vaas/log"><el-icon><Document /></el-icon><span>动态日志</span></el-menu-item>

      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #e6e6e6;display:flex;align-items:center;justify-content:space-between">
        <span>VaaS车辆后台管理平台</span>
        <el-button text @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main style="background:#f0f2f5"><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const handleLogout = () => { localStorage.removeItem('token'); router.push('/login') }
</script>
