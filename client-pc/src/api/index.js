import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.hash = '#/login'
    }
    return Promise.reject(error)
  }
)

// ===== 用户 =====
export const userApi = {
  login: (phone, password) => api.post('/user/login', { phone, password }),
  sendCode: (phone) => api.post('/user/send-code', null, { params: { phone } }),
  register: (data) => api.post('/user/register', data),
  info: () => api.get('/user/info'),
  updateInfo: (data) => api.put('/user/info', data),
  profile: () => api.get('/user/profile')
}

// ===== AI 创作 =====
export const aiApi = {
  getAgents: () => api.get('/ai/agents'),
  getImageAgents: () => api.get('/ai/image-agents'),
  getDirections: () => api.get('/ai/directions'),
  generateTitles: (data) => api.post('/ai/generate-titles', data, { timeout: 60000 }),
  generateContent: (data) => api.post('/ai/generate-content', data, { timeout: 60000 }),
  generateImagePrompt: (data) => api.post('/ai/generate-image-prompt', data, { timeout: 60000 }),
  generateImages: (data) => api.post('/ai/generate-images', data, { timeout: 60000 })
}

// ===== 内容 =====
export const contentApi = {
  myList: (params) => api.get('/content/my', { params }),
  create: (data) => api.post('/content', data),
  detail: (id) => api.get(`/content/${id}`),
  submitAudit: (id) => api.post(`/content/${id}/submit-audit`),
  approve: (id) => api.post(`/content/${id}/approve`),
  reject: (id, reason) => api.post(`/content/${id}/reject`, null, { params: { reason } }),
  update: (id, data) => api.put(`/content/${id}`, data),
  delete: (id) => api.delete(`/content/${id}`),
  aiScore: (id) => api.get(`/content/${id}/ai-score`)
}

// ===== 发布队列 =====
export const publishApi = {
  queue: (params) => api.get('/publish/queue', { params }),
  stats: () => api.get('/publish/queue/stats'),
  add: (data) => api.post('/publish/queue/add', data),
  remove: (id) => api.delete(`/publish/queue/${id}`),
  publishNow: (data) => api.post('/publish/publish-now', data)
}

// ===== 数据分析 =====
export const analyticsApi = {
  dashboard: () => api.get('/analytics/dashboard'),
  contentRankings: () => api.get('/analytics/content-rankings'),
  trends: (days = 7) => api.get('/analytics/trends', { params: { days } })
}

// ===== 账号 =====
export const accountApi = {
  list: () => api.get('/account/list'),
  bind: (data) => api.post('/account/bind', data),
  unbind: (id) => api.delete(`/account/unbind/${id}`),
  data: (accountId, date) => api.get(`/account/data/${accountId}`, { params: { date } }),
  history: (accountId, params) => api.get(`/account/data/${accountId}/history`, { params })
}

export default api
