<template>
  <view class="page">
    <!-- 自定义导航栏 -->
    <view class="nav-bar">
      <view class="nav-status">
        <text>9:41</text>
        <view class="nav-signal">
          <view class="signal-bar s1"></view><view class="signal-bar s2"></view><view class="signal-bar s3"></view><view class="signal-bar s4"></view>
        </view>
      </view>
      <view class="nav-title-bar">
        <text class="nav-title">红书智创</text>
        <view class="nav-capsule">
          <view class="capsule-dot"></view>
          <view class="capsule-line"></view>
        </view>
      </view>
    </view>

    <!-- 登录遮罩 -->
    <view class="login-overlay" v-if="!userStore.isLogin">
      <view class="login-deco deco-1"></view>
      <view class="login-deco deco-2"></view>
      <view class="login-logo"><text class="logo-icon">◆</text></view>
      <text class="login-title">红书智创</text>
      <text class="login-slogan">让小红书运营更轻松</text>
      <button class="login-btn" open-type="getPhoneNumber" @getphonenumber="handleWechatLogin">
        微信一键登录
      </button>
      <text class="login-hint">安全登录，不泄露隐私</text>
      <text class="login-terms">登录即表示同意 用户协议 和 隐私政策</text>
    </view>

    <!-- 主内容 -->
    <scroll-view scroll-y class="content" v-else>
      <view class="home-welcome">
        <view class="home-avatar"><text class="avatar-text">创</text></view>
        <view class="home-hello">
          <text class="hello-title">早上好，创作者</text>
          <text class="hello-sub">今天有 3 篇内容待发布</text>
        </view>
        <view class="home-badge">3</view>
      </view>

      <view class="metrics-grid">
        <view class="metric-card" v-for="m in metrics" :key="m.label">
          <text class="metric-label">{{ m.label }}</text>
          <text class="metric-value">{{ m.value }}</text>
          <text class="metric-trend" :class="{ up: m.up }">{{ m.trend }}</text>
        </view>
      </view>

      <view class="chart-card">
        <text class="chart-title">近7天趋势</text>
        <view class="chart-svg">
          <view class="chart-line c1" v-for="i in 7" :key="i" :style="{ height: chartHeights[i-1] + 'px' }"></view>
        </view>
        <view class="chart-labels">
          <text v-for="d in ['一','二','三','四','五','六','日']" :key="d">{{ d }}</text>
        </view>
      </view>

      <view class="home-actions">
        <button class="btn btn-primary" @tap="goCreate">AI一键创作</button>
        <button class="btn btn-outline">查看队列</button>
      </view>

      <view class="recent-section">
        <text class="chart-title">最近创作</text>
        <scroll-view scroll-x class="recent-scroll">
          <view class="recent-card" v-for="r in recents" :key="r.title">
            <view class="recent-thumb" :style="{ background: r.color }">
              <text class="recent-thumb-text">{{ r.tag }}</text>
            </view>
            <text class="recent-title">{{ r.title }}</text>
            <view class="recent-status" :class="r.statusClass">{{ r.status }}</view>
          </view>
        </scroll-view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/store'

const userStore = useUserStore()

const metrics = ref([
  { label: '今日发布', value: '4', trend: '↑ +12%', up: true },
  { label: '互动量', value: '2.8k', trend: '↑ +8%', up: true },
  { label: '粉丝增长', value: '+156', trend: '↑ +23%', up: true },
  { label: 'AI创作', value: '12', trend: '次', up: false }
])

const chartHeights = [35, 42, 40, 58, 52, 70, 66]

const recents = ref([
  { title: '周末探店｜宝藏咖啡', tag: '探店', status: '已发布', statusClass: 's-pub', color: '#FFE0E5' },
  { title: '春日穿搭｜温柔通勤', tag: '穿搭', status: '待发布', statusClass: 's-wait', color: '#FEF3C7' },
  { title: '3天2夜云南攻略', tag: '攻略', status: '审核中', statusClass: 's-review', color: '#EDE9FE' }
])

function handleWechatLogin(e) {
  if (e.detail.errMsg === 'getPhoneNumber:ok') {
    userStore.login('13800000000', '000000')
  }
}

function goCreate() {
  uni.switchTab({ url: '/pages/create/create' })
}
</script>

<style scoped>
.page { display: flex; flex-direction: column; height: 100vh; background: #FAFAF9; }
.nav-bar { background: #fff; flex-shrink: 0; }
.nav-status { height: 44px; display: flex; align-items: flex-end; justify-content: space-between; padding: 0 24px 4px; font-size: 13px; font-weight: 600; color: #1C1917; }
.nav-signal { display: flex; gap: 2px; align-items: flex-end; }
.signal-bar { width: 3px; border-radius: 1px; background: #1C1917; }
.s1 { height: 4px; } .s2 { height: 7px; } .s3 { height: 9px; } .s4 { height: 12px; opacity: .3; }
.nav-title-bar { height: 44px; display: flex; align-items: center; justify-content: center; position: relative; }
.nav-title { font-size: 17px; font-weight: 600; color: #1C1917; }
.nav-capsule { position: absolute; right: 12px; top: 6px; width: 87px; height: 32px; background: #E7E5E4; border-radius: 16px; display: flex; align-items: center; justify-content: center; gap: 6px; }
.capsule-dot { width: 6px; height: 6px; border-radius: 50%; background: #A8A29E; }
.capsule-line { width: 12px; height: 2px; border-radius: 1px; background: #A8A29E; }

.login-overlay { position: absolute; inset: 0; z-index: 200; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #FAFAF9; padding: 40px 32px; }
.login-deco { position: absolute; border-radius: 50%; opacity: .1; }
.deco-1 { width: 280px; height: 280px; background: #FE2C55; top: -60px; right: -40px; }
.deco-2 { width: 200px; height: 200px; background: #8B5CF6; bottom: 100px; left: -60px; }
.login-logo { width: 64px; height: 64px; background: linear-gradient(135deg,#FE2C55,#8B5CF6); border-radius: 16px; display: flex; align-items: center; justify-content: center; margin-bottom: 16px; }
.logo-icon { color: #fff; font-size: 28px; }
.login-title { font-size: 24px; font-weight: 700; color: #1C1917; margin-bottom: 4px; }
.login-slogan { font-size: 14px; color: #78716C; margin-bottom: 32px; }
.login-btn { width: 100%; padding: 14px; border-radius: 12px; background: #07C160; color: #fff; font-size: 16px; font-weight: 700; border: none; box-shadow: 0 8px 24px rgba(7,193,96,.3); }
.login-hint { font-size: 11px; color: #A8A29E; margin-top: 12px; }
.login-terms { font-size: 11px; color: #A8A29E; margin-top: 24px; }

.content { flex: 1; overflow-y: auto; }
.home-welcome { display: flex; align-items: center; gap: 10px; padding: 16px; background: #fff; margin-bottom: 12px; }
.home-avatar { width: 36px; height: 36px; border-radius: 50%; background: linear-gradient(135deg,#FE2C55,#FF6480); display: flex; align-items: center; justify-content: center; }
.avatar-text { color: #fff; font-size: 14px; font-weight: 600; }
.home-hello { flex: 1; }
.hello-title { font-size: 15px; font-weight: 700; color: #1C1917; display: block; }
.hello-sub { font-size: 11px; color: #78716C; }
.home-badge { padding: 3px 8px; border-radius: 10px; background: #FFF1F3; color: #E61A45; font-size: 11px; font-weight: 600; }

.metrics-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; padding: 0 16px 12px; }
.metric-card { background: #fff; border-radius: 12px; padding: 14px 16px; border-top: 3px solid #FE2C55; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.metric-label { font-size: 11px; color: #78716C; display: block; margin-bottom: 6px; }
.metric-value { font-size: 24px; font-weight: 700; color: #1C1917; display: block; }
.metric-trend { font-size: 11px; color: #78716C; }
.metric-trend.up { color: #22C55E; }

.chart-card { padding: 16px; margin: 0 16px 12px; background: #fff; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.chart-title { font-size: 13px; font-weight: 600; color: #44403C; margin-bottom: 12px; display: block; }
.chart-svg { display: flex; align-items: flex-end; gap: 12px; height: 80px; padding: 0 8px; }
.chart-line { flex: 1; background: linear-gradient(180deg,#FE2C55,#FF6480); border-radius: 4px 4px 0 0; min-height: 4px; opacity: .7; }
.chart-labels { display: flex; gap: 12px; padding: 0 8px; margin-top: 6px; }
.chart-labels text { flex: 1; text-align: center; font-size: 10px; color: #A8A29E; }

.home-actions { display: flex; gap: 10px; padding: 0 16px 16px; }
.btn { flex: 1; padding: 14px; border-radius: 24px; font-size: 14px; font-weight: 600; display: flex; align-items: center; justify-content: center; gap: 6px; border: none; }
.btn-primary { background: linear-gradient(135deg,#FE2C55,#FF6480); color: #fff; box-shadow: 0 4px 12px rgba(254,44,85,.3); }
.btn-outline { background: #fff; color: #FE2C55; border: 1.5px solid #FE2C55; }

.recent-section { padding: 0 16px 24px; }
.recent-scroll { display: flex; gap: 10px; white-space: nowrap; }
.recent-card { width: 160px; flex-shrink: 0; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,.05); }
.recent-thumb { width: 160px; height: 90px; display: flex; align-items: center; justify-content: center; }
.recent-thumb-text { font-size: 14px; color: #57534E; font-weight: 600; }
.recent-title { font-size: 11px; font-weight: 600; color: #44403C; display: block; padding: 8px 10px 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.recent-status { font-size: 9px; padding: 2px 6px; border-radius: 16px; font-weight: 500; display: inline-block; margin: 0 10px 8px; }
.s-pub { background: #DCFCE7; color: #16A34A; } .s-wait { background: #DBEAFE; color: #2563EB; } .s-review { background: #FEF3C7; color: #B45309; }
</style>
