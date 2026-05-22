import axios from 'axios'

const api = axios.create({
  baseURL: '/api/admin',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('admin_token')
      window.location.hash = '#/login'
    }
    return Promise.reject(error)
  }
)

// ===== Agent =====
export const agentApi = {
  list: () => api.get('/ai/agents'),
  create: (data) => api.post('/ai/agents', data),
  update: (id, data) => api.put(`/ai/agents/${id}`, data),
  delete: (id) => api.delete(`/ai/agents/${id}`)
}

// ===== AI Models =====
export const aiModelApi = {
  list: () => api.get('/ai/apis'),
  create: (data) => api.post('/ai/apis', data),
  update: (id, data) => api.put(`/ai/apis/${id}`, data),
  delete: (id) => api.delete(`/ai/apis/${id}`)
}

export default api
