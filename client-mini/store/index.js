import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null,
    isLogin: false
  }),
  actions: {
    async login(phone, code) {
      try {
        const res = await uni.request({
          url: 'https://api.hongshu.ai/api/user/login',
          method: 'POST',
          data: { phone, code }
        })
        if (res.data.code === 200) {
          this.token = res.data.data.token
          this.isLogin = true
          uni.setStorageSync('token', this.token)
          return true
        }
        return false
      } catch { return false }
    },
    logout() {
      this.token = ''
      this.userInfo = null
      this.isLogin = false
      uni.removeStorageSync('token')
    },
    async fetchProfile() {
      try {
        const { api } = await import('@/api/index.js')
        const res = await api.get('/user/profile')
        if (res.code === 200) this.userInfo = res.data
      } catch { }
    }
  }
})
