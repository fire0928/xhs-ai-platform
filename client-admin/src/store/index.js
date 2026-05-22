import { defineStore } from 'pinia'
import api from '@/api'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    adminInfo: null,
    stats: {
      totalUsers: 0, activeUsers: 0, dau: 0,
      totalContents: 0, aiTotal: 0,
      pendingPublishes: 0, publishedToday: 0,
      systemHealth: '--'
    }
  }),
  actions: {
    async login(phone, password) {
      const res = await api.post('/login', { phone, password })
      if (res.data.code === 200) {
        this.token = res.data.data.token
        localStorage.setItem('admin_token', this.token)
        return true
      }
      return false
    },
    logout() {
      this.token = ''
      this.adminInfo = null
      localStorage.removeItem('admin_token')
    },
    async fetchStats() {
      try {
        const res = await api.get('/overview')
        if (res.data.code === 200) Object.assign(this.stats, res.data.data)
      } catch { }
    }
  }
})
