import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:50410/spring/v1',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.response.use(
  res => res.data,
  err => Promise.reject(err)
)

export function getAlarmList(hour) {
  return http.post('/get-alarm-list', { hour })
}

export function getRealTimeSensorData(roadName) {
  return http.post('/get_real_time_sensor_data', { road_name: roadName })
}

export function getLast24hDataPlot(roadName, dataTitle) {
  return http.post('/get_last24h_data_plot', { road_name: roadName, data_title: dataTitle })
}

export function getCoveredRange() {
  return http.post('/get_covered_range')
}

export function getWeather() {
  return http.get('/get_weather')
}

export function getEventSummary() {
  return http.post('/get-event-summary')
}

export function deleteEvent(eventId, eventType) {
  return http.post('/delete-event', { eventId, eventType })
}

export function getLast24hEvent(eventType) {
  return http.post(`/get-last-24h-${eventType}-event`, {})
}

// 在线车辆位置 - 用于显示"联网车辆"实时数量
export function getOnlineVehicles() {
  return http.get('/location')
}

export default http
