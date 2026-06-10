import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)
const routes = [
  { path: '/dashboard', component: () => import('../views/DashboardPage') },
  { path: '/', redirect: '/dashboard' },
  { path: '*', redirect: '/dashboard' },
]
export default new VueRouter({ mode: 'hash', base: process.env.BASE_URL, routes })
