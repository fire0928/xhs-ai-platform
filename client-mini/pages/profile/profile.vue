<template>
  <view class="page">
    <view class="nav-bar">
      <view class="nav-status"><text>9:41</text><view class="nav-signal"><view class="signal-bar s1"></view><view class="signal-bar s2"></view><view class="signal-bar s3"></view><view class="signal-bar s4"></view></view></view>
      <view class="nav-title-bar">
        <text class="nav-title">我的</text>
        <view class="nav-capsule"><view class="capsule-dot"></view><view class="capsule-line"></view></view>
      </view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="prof-header">
        <view class="prof-avatar"><text class="avatar-text">王</text></view>
        <text class="prof-name">创作者小王</text>
        <text class="prof-bind">已绑定 2 个小红书账号</text>
      </view>

      <view class="prof-list">
        <view class="prof-group">
          <view class="prof-item" @tap="goAccounts">
            <view class="pi-icon" style="background:#FFF1F3"><text class="pi-emoji">⭐</text></view>
            <text class="pi-label">账号管理</text>
            <text class="pi-badge">2个账号</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="prof-item">
            <view class="pi-icon" style="background:#DBEAFE"><text class="pi-emoji">📋</text></view>
            <text class="pi-label">发布队列</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="prof-item">
            <view class="pi-icon" style="background:#DBEAFE"><text class="pi-emoji">📊</text></view>
            <text class="pi-label">数据分析</text>
            <text class="pi-arrow">›</text>
          </view>
        </view>

        <view class="prof-group">
          <view class="prof-item" @tap="goSettings">
            <view class="pi-icon" style="background:#EDE9FE"><text class="pi-emoji">✨</text></view>
            <text class="pi-label">AI偏好设置</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="prof-item">
            <view class="pi-icon" style="background:#FEF3C7"><text class="pi-emoji">🔔</text></view>
            <text class="pi-label">通知设置</text>
            <text class="pi-arrow">›</text>
          </view>
        </view>

        <view class="prof-group">
          <view class="prof-item">
            <view class="pi-icon" style="background:#DCFCE7"><text class="pi-emoji">💬</text></view>
            <text class="pi-label">分享给朋友</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="prof-item">
            <view class="pi-icon" style="background:#F5F5F4"><text class="pi-emoji">❓</text></view>
            <text class="pi-label">帮助与反馈</text>
            <text class="pi-arrow">›</text>
          </view>
          <view class="prof-item">
            <view class="pi-icon" style="background:#F5F5F4"><text class="pi-emoji">ℹ️</text></view>
            <text class="pi-label">关于我们</text>
            <text class="pi-arrow">›</text>
          </view>
        </view>
      </view>

      <view class="prof-logout" @tap="handleLogout">退出登录</view>
    </scroll-view>
  </view>
</template>

<script setup>
import { useUserStore } from '@/store'

const userStore = useUserStore()

function goAccounts() { uni.navigateTo({ url: '/pages/accounts/accounts' }) }
function goSettings() { uni.navigateTo({ url: '/pages/settings/settings' }) }

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success(res) {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({ url: '/pages/index/index' })
      }
    }
  })
}
</script>

<style scoped>
.page { display: flex; flex-direction: column; height: 100vh; background: #FAFAF9; }
.nav-bar { background: #fff; flex-shrink: 0; }
.nav-status { height: 44px; display: flex; align-items: flex-end; justify-content: space-between; padding: 0 24px 4px; font-size: 13px; font-weight: 600; color: #1C1917; }
.nav-signal { display: flex; gap: 2px; align-items: flex-end; }
.signal-bar { width: 3px; border-radius: 1px; background: #1C1917; }
.s1{height:4px}.s2{height:7px}.s3{height:9px}.s4{height:12px;opacity:.3}
.nav-title-bar { height: 44px; display: flex; align-items: center; justify-content: center; position: relative; }
.nav-title { font-size: 17px; font-weight: 600; color: #1C1917; }
.nav-capsule { position: absolute; right: 12px; top: 6px; width: 87px; height: 32px; background: #E7E5E4; border-radius: 16px; display: flex; align-items: center; justify-content: center; gap: 6px; }
.capsule-dot { width: 6px; height: 6px; border-radius: 50%; background: #A8A29E; }
.capsule-line { width: 12px; height: 2px; border-radius: 1px; background: #A8A29E; }

.content { flex: 1; overflow-y: auto; }
.prof-header { padding: 20px 16px; text-align: center; background: #fff; }
.prof-avatar { width: 56px; height: 56px; border-radius: 50%; background: linear-gradient(135deg,#FE2C55,#FF6480); margin: 0 auto 8px; display: flex; align-items: center; justify-content: center; }
.avatar-text { color: #fff; font-size: 22px; font-weight: 700; }
.prof-name { font-size: 18px; font-weight: 700; color: #1C1917; display: block; }
.prof-bind { font-size: 11px; color: #78716C; }

.prof-list { padding: 8px 0; }
.prof-group { background: #fff; margin-bottom: 8px; }
.prof-item { display: flex; align-items: center; padding: 13px 16px; }
.pi-icon { width: 30px; height: 30px; border-radius: 8px; display: flex; align-items: center; justify-content: center; margin-right: 10px; }
.pi-emoji { font-size: 14px; }
.pi-label { flex: 1; font-size: 13px; color: #292524; }
.pi-badge { font-size: 11px; color: #A8A29E; margin-right: 6px; }
.pi-arrow { color: #D6D3D1; font-size: 18px; }

.prof-logout { margin: 20px 16px; padding: 13px; text-align: center; border-radius: 12px; border: 1.5px solid #FE2C55; color: #FE2C55; font-size: 13px; font-weight: 600; }
</style>
