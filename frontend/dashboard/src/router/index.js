import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/DashboardPage.vue') },
  { path: '/', redirect: '/dashboard' },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
