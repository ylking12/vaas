import axios from 'axios'

const service = axios.create({
    headers:{
        'access-control-allow-origin':'*',
        'Content-Type': 'application/json'
    },
    baseURL: process.env.VUE_APP_URL,
    timeout: 30000,
});

// 请求时的拦截器
axios.interceptors.request.use(config => {
    // if(token) {
    //     config.headers.Authorization = token;
    // }
    return config;
}, error => {
    return Promise.reject(error);
})

// 请求完成后的拦截器
axios.interceptors.response.use(response => {
    const res = response.data;
    return Promise.resolve(res);
}, error => {
    return Promise.reject(error);
})

export default service;
