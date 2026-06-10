import Vue from 'vue'
import 'element-ui/lib/theme-chalk/index.css'
import { Progress, Notification, Slider, Drawer, Switch, Carousel, CarouselItem } from 'element-ui'
import 'normalize.css/normalize.css'
import App from './App.vue'
import router from './router'
import store from './store'

Vue.use(Progress)
Vue.use(Slider)
Vue.use(Drawer)
Vue.use(Switch)
Vue.use(Carousel)
Vue.use(CarouselItem)
Vue.prototype.$notify = Notification

import * as echarts from 'echarts'
Vue.prototype.$echarts = echarts

import axios from 'axios'
Vue.prototype.$axios = axios

import moment from 'moment'
Vue.prototype.$moment = moment

import appendTo from '@/utils/appendTo'
Vue.directive('append-to', appendTo)

// 验证模式下抑制 Vue 错误弹窗和数据报警
Vue.config.productionTip = false
Vue.config.errorHandler = () => {}
Vue.config.warnHandler = () => {}

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
}).$mount('#app')
