<template>
  <div class="admin-login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">◆</div>
        <h1>红书智创</h1>
        <p>管理后台</p>
      </div>
      <div class="login-form">
        <div class="form-field">
          <label>手机号</label>
          <input v-model="phone" type="text" placeholder="请输入手机号" maxlength="11" @keydown.enter="handleLogin" />
        </div>
        <div class="form-field">
          <label>密码</label>
          <input v-model="password" type="password" placeholder="请输入密码" maxlength="16" @keydown.enter="handleLogin" />
        </div>
        <button class="login-btn" :disabled="loading" @click="handleLogin">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const phone = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

  async function handleLogin() {
  errorMsg.value = ''
  if (!phone.value || phone.value.length < 11) {
    errorMsg.value = '请输入正确的手机号'
    return
  }
  if (!password.value) {
    errorMsg.value = '请输入密码'
    return
  }
  if (password.value.length < 6) {
    errorMsg.value = '密码至少6位'
    return
  }
  loading.value = true
  try {
    const res = await axios.post('/api/user/login', {
      phone: phone.value,
      password: password.value
    })
    if (res.data.code === 200) {
      const token = res.data.data.token
      const payload = JSON.parse(atob(token.split('.')[1]))
      if (payload.role !== 'ADMIN') {
        errorMsg.value = '权限不足，仅管理员可登录'
        return
      }
      localStorage.setItem('admin_token', token)
      router.push('/overview')
    } else {
      errorMsg.value = res.data.message || '登录失败'
    }
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '手机号或密码错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  position: fixed; inset: 0; z-index: 1000;
  display: flex; align-items: center; justify-content: center;
  background: #f0f2f5;
}
.login-card {
  width: 380px; background: #fff; border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,.08); padding: 40px;
}
.login-header { text-align: center; margin-bottom: 32px; }
.login-logo {
  width: 48px; height: 48px; background: linear-gradient(135deg, #FE2C55, #FF4D6A);
  border-radius: 10px; display: inline-flex; align-items: center; justify-content: center;
  color: #fff; font-size: 22px; font-weight: bold;
}
.login-header h1 { font-size: 22px; font-weight: 700; color: #1a1a2e; margin: 12px 0 4px; }
.login-header p { font-size: 13px; color: #999; }
.login-form { }
.form-field { margin-bottom: 16px; }
.form-field label { display: block; font-size: 13px; font-weight: 500; color: #333; margin-bottom: 8px; }
.form-field input {
  width: 100%; padding: 11px 14px; border: 1px solid #d9d9d9; border-radius: 8px;
  font-size: 14px; outline: none; transition: border-color .15s; box-sizing: border-box;
}
.form-field input:focus { border-color: #FE2C55; box-shadow: 0 0 0 3px rgba(254,44,85,.1); }
.login-btn {
  width: 100%; padding: 11px; font-size: 15px; font-weight: 600; border-radius: 8px;
  background: linear-gradient(135deg, #FE2C55, #FF4D6A); color: #fff; cursor: pointer;
  border: none; margin-top: 8px; transition: opacity .15s;
}
.login-btn:hover { opacity: .9; }
.login-btn:disabled { opacity: .6; cursor: not-allowed; }
.error-msg { color: #e74c3c; font-size: 13px; margin-top: 12px; text-align: center; }
</style>
