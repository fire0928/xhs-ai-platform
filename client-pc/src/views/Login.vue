<template>
  <div class="login-page">
    <div class="lp-brand">
      <div class="lp-c">
        <div class="lp-logo">
          <svg viewBox="0 0 32 32" fill="none"><path d="M16 4L22 9V23L16 28L10 23V9L16 4Z" fill="white" opacity=".9"/><path d="M16 8L19 10.5V21.5L16 24L13 21.5V10.5L16 8Z" fill="white"/></svg>
        </div>
        <div class="lp-tt">红书智创</div>
        <div class="lp-sub">AI 赋能，让小红书运营更轻松</div>
        <div class="lp-feats">
          <div class="lp-feat"><div class="lp-feat-ic"><svg viewBox="0 0 22 22" fill="none"><path d="M11 2L13 7H18L14 10.5L15.5 16L11 12.5L6.5 16L8 10.5L4 7H9L11 2Z" fill="white"/></svg></div><span>AI 智能创作</span></div>
          <div class="lp-feat"><div class="lp-feat-ic"><svg viewBox="0 0 22 22" fill="none"><rect x="3" y="4" width="16" height="14" rx="2" stroke="white" stroke-width="1.5" fill="none"/><path d="M7 10H15M7 14H12" stroke="white" stroke-width="1.5" stroke-linecap="round"/></svg></div><span>批量管理</span></div>
          <div class="lp-feat"><div class="lp-feat-ic"><svg viewBox="0 0 22 22" fill="none"><path d="M3 16V8M7 16V5M11 16V10M15 16V3M19 16V7" stroke="white" stroke-width="2" stroke-linecap="round"/></svg></div><span>数据洞察</span></div>
        </div>
      </div>
    </div>
    <div class="lp-form">
      <div class="lp-fc">
        <div class="lp-ft">{{ isRegister ? '创建账号' : '欢迎回来' }}</div>
        <div class="lp-fd">{{ isRegister ? '注册红书智创账号，开启AI创作之旅' : '登录你的红书智创账号' }}</div>

        <!-- 模式切换标签 -->
        <div class="lp-tabs">
          <button :class="['lp-tab', { active: !isRegister }]" @click="switchMode(false)">密码登录</button>
          <button :class="['lp-tab', { active: isRegister }]" @click="switchMode(true)">手机注册</button>
        </div>

        <!-- 手机号 -->
        <div class="lp-fl">
          <label>手机号</label>
          <input v-model="phone" class="ipt" type="text" placeholder="请输入手机号" maxlength="11">
        </div>

        <!-- 密码 -->
        <div class="lp-fl">
          <label>密码</label>
          <input v-model="password" class="ipt" type="password" placeholder="请输入密码" maxlength="16">
        </div>

        <!-- 注册特有：验证码 -->
        <div v-if="isRegister" class="lp-fl">
          <label>验证码</label>
          <div class="code-row">
            <input v-model="code" class="ipt" type="text" placeholder="请输入验证码" maxlength="6">
            <button class="btn-code" :disabled="counting" @click="sendCode">
              {{ counting ? `${countdown}s` : '获取验证码' }}
            </button>
          </div>
        </div>

        <!-- 注册特有：确认密码 -->
        <div v-if="isRegister" class="lp-fl">
          <label>确认密码</label>
          <input v-model="confirmPassword" class="ipt" type="password" placeholder="请再次输入密码" maxlength="16">
        </div>

        <button class="lp-btn" @click="doLogin" :disabled="loading">
          {{ loading ? '处理中...' : (isRegister ? '注 册' : '登 录') }}
        </button>

        <div v-if="error" class="error-msg">{{ error }}</div>

        <div class="lp-foot">
          <span v-if="!isRegister">
            还没有账号？<a href="#" @click.prevent="switchMode(true)">立即注册</a>
          </span>
          <span v-if="isRegister">
            已有账号？<a href="#" @click.prevent="switchMode(false)">去登录</a>
          </span>
          <span class="lp-sp">|</span>
          登录即表示同意 <a href="#">用户协议</a> 和 <a href="#">隐私政策</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()
const phone = ref('')
const password = ref('')
const code = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')
const isRegister = ref(false)
const counting = ref(false)
const countdown = ref(60)
let timer = null

function switchMode(register) {
  isRegister.value = register
  error.value = ''
  code.value = ''
  confirmPassword.value = ''
}

async function sendCode() {
  if (!phone.value || phone.value.length < 11) {
    error.value = '请输入正确的手机号'
    return
  }
  error.value = ''
  try {
    const res = await api.post('/user/send-code', { phone: phone.value })
    if (res.data.code === 200) {
      counting.value = true
      countdown.value = 60
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          counting.value = false
        }
      }, 1000)
    }
  } catch (e) {
    error.value = '发送验证码失败'
  }
}

async function doLogin() {
  error.value = ''
  if (!phone.value || phone.value.length < 11) {
    error.value = '请输入正确的手机号'
    return
  }
  if (!password.value) {
    error.value = '请输入密码'
    return
  }
  if (password.value.length < 6) {
    error.value = '密码至少6位'
    return
  }

  // 注册模式
  if (isRegister.value) {
    if (!code.value) {
      error.value = '请输入验证码'
      return
    }
    if (password.value !== confirmPassword.value) {
      error.value = '两次密码不一致'
      return
    }
    loading.value = true
    try {
      const res = await api.post('/user/register', {
        phone: phone.value,
        password: password.value,
        code: code.value,
        terminal: 'computer'
      })
      if (res.data.code === 200) {
        localStorage.setItem('token', res.data.data.token)
        router.push('/dashboard')
      } else {
        error.value = res.data.message || '注册失败'
      }
    } catch (e) {
      error.value = e.response?.data?.message || '注册失败，请重试'
    } finally {
      loading.value = false
    }
    return
  }

  // 登录模式
  loading.value = true
  try {
    const res = await api.post('/user/login', {
      phone: phone.value,
      password: password.value
    })
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.data.token)
      localStorage.setItem('refreshToken', res.data.data.refreshToken)
      router.push('/dashboard')
    } else {
      error.value = res.data.message || '登录失败'
    }
  } catch (e) {
    error.value = e.response?.data?.message || '手机号或密码错误'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { display: flex; height: 100vh; width: 100vw; position: fixed; inset: 0; z-index: 100; }
.lp-brand { flex: 1; background: #1C1917; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 64px; position: relative; overflow: hidden; }
.lp-brand::before { content: ''; position: absolute; width: 600px; height: 600px; border-radius: 50%; background: rgba(254,44,85,.12); top: -200px; right: -200px; }
.lp-brand::after { content: ''; position: absolute; width: 400px; height: 400px; border-radius: 50%; background: rgba(139,92,246,.08); bottom: -100px; left: -100px; }
.lp-c { position: relative; z-index: 1; text-align: center; color: #fff; }
.lp-logo { width: 64px; height: 64px; background: var(--gai); border-radius: 12px; display: flex; align-items: center; justify-content: center; margin: 0 auto 24px; }
.lp-logo svg { width: 32px; height: 32px; }
.lp-tt { font-size: 28px; font-weight: 700; margin-bottom: 12px; }
.lp-sub { font-size: 16px; opacity: .85; margin-bottom: 32px; }
.lp-feats { display: flex; gap: 24px; }
.lp-feat { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.lp-feat-ic { width: 44px; height: 44px; background: rgba(254,44,85,.15); border-radius: 8px; display: flex; align-items: center; justify-content: center; }
.lp-feat-ic svg { width: 22px; height: 22px; }
.lp-feat span { font-size: 13px; opacity: .9; }
.lp-form { width: 480px; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 48px 64px; background: #fff; }
.lp-fc { width: 100%; max-width: 340px; }
.lp-ft { font-size: 24px; font-weight: 700; color: var(--cn-900); margin-bottom: 8px; }
.lp-fd { font-size: 13px; color: var(--cn-500); margin-bottom: 24px; }
.lp-tabs { display: flex; gap: 0; margin-bottom: 24px; background: var(--cn-100); border-radius: 8px; padding: 3px; }
.lp-tab { flex: 1; padding: 8px 0; border: none; border-radius: 6px; font-size: 13px; font-weight: 500; color: var(--cn-500); background: transparent; cursor: pointer; transition: all .15s; }
.lp-tab.active { background: #fff; color: var(--cn-900); box-shadow: 0 1px 3px rgba(0,0,0,.08); }
.lp-fl { margin-bottom: 16px; }
.lp-fl label { display: block; font-size: 13px; font-weight: 500; color: var(--cn-700); margin-bottom: 8px; }
.lp-fl .ipt { width: 100%; padding: 12px 16px; border: 1px solid var(--cn-300); border-radius: 8px; font-size: 14px; outline: none; transition: border-color .15s; box-sizing: border-box; }
.lp-fl .ipt:focus { border-color: var(--cp-400); box-shadow: 0 0 0 3px rgba(254,44,85,.1); }
.code-row { display: flex; gap: 8px; }
.code-row .ipt { flex: 1; }
.btn-code { padding: 12px 16px; background: #fff; color: var(--cp-500); border: 1px solid var(--cp-300); border-radius: 8px; font-size: 13px; font-weight: 500; cursor: pointer; white-space: nowrap; transition: all .15s; }
.btn-code:hover:not(:disabled) { background: var(--cp-50); }
.btn-code:disabled { color: var(--cn-400); border-color: var(--cn-200); cursor: not-allowed; }
.lp-btn { width: 100%; padding: 12px; font-size: 14px; font-weight: 600; border-radius: 8px; background: var(--cp-500); color: #fff; cursor: pointer; transition: all .15s; border: none; margin-top: 8px; }
.lp-btn:hover { background: var(--cp-600); }
.lp-btn:disabled { opacity: .6; cursor: not-allowed; }
.error-msg { color: var(--ce); font-size: 13px; margin-top: 12px; text-align: center; }
.lp-foot { margin-top: 20px; font-size: 11px; color: var(--cn-400); text-align: center; line-height: 1.8; }
.lp-foot a { color: var(--cp-500); text-decoration: none; }
.lp-sp { margin: 0 6px; }
</style>
