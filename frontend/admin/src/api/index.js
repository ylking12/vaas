import axios from 'axios'

const request = axios.create({
  baseURL: '/spring/v1/admin',
  timeout: 15000
})

request.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
)

export default request

export const adminApi = {
  login(data) { return request.post('/login', data) },
  getList(params) { return request.get('/list', { params }) },
  add(data) { return request.post('/add', data) },
  update(data) { return request.post('/update', data) },
  delete(data) { return request.post('/delete', data) },
  getHeartbeat() { return request.get('/heartbeat') },
  getLog() { return request.get('/log') },
  getRoutes() { return request.get('/get-routes') },
  getDevices() { return request.get('/device-option') },
  getModels() { return request.get('/model-option') },
}
