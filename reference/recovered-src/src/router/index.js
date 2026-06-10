import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
    {
        path: '/dashboard',
        name: 'dashboard',
        component: () => import('../views/dashboard')
    },

    {
        path: '/',
        redirect: '/dashboard'
    },
    {
        path: '*',
        redirect: '/dashboard'
    },
    // {
    //     path: '/demo',
    //     name: 'flixedemo',
    //     component: () => import('../views/flixedemo')
    // }
]

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes
})

export default router
