import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/', component: () => import('@/layout/MainLayout.vue'),
    children: [
      { path: '', redirect: '/welcome' },
      { path: 'welcome', name: 'Welcome', component: () => import('@/views/vaas/Welcome.vue'), meta: { title: '首页' } },
      { path: 'vaas/car-mapping', name: 'CarMapping', component: () => import('@/views/vaas/CarMapping.vue'), meta: { title: '车辆绑定' } },
      { path: 'vaas/heartbeat', name: 'Heartbeat', component: () => import('@/views/vaas/Heartbeat.vue'), meta: { title: '心跳管理' } },
      { path: 'vaas/log', name: 'Log', component: () => import('@/views/vaas/LogViewer.vue'), meta: { title: '动态日志' } },

    ]
  },
  { path: '/error/403', component: () => import('@/views/error/Forbidden.vue') },
  { path: '/error/404', component: () => import('@/views/error/NotFound.vue') },
  { path: '/error/500', component: () => import('@/views/error/ServerError.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/error/404' }
]


export default createRouter({ history: createWebHistory(), routes })
