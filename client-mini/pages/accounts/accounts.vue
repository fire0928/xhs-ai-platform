<template>
  <view class="page">
    <view class="header">
      <text class="back" @tap="goBack">‹ 返回</text>
      <text class="title">账号管理</text>
    </view>
    <view class="body">
      <view class="account-card" v-for="(a,i) in accounts" :key="i">
        <view class="acc-top">
          <view class="acc-av" :style="{ background: colors[i] }">{{ a.name.charAt(0) }}</view>
          <view class="acc-info">
            <text class="acc-name">{{ a.name }}</text>
            <text class="acc-id">ID: {{ a.id }}</text>
          </view>
          <view class="acc-status" :class="a.online ? 'online' : 'offline'">{{ a.online ? '在线' : '过期' }}</view>
        </view>
        <view class="acc-bottom">
          <button class="btn-sm" size="mini">验证</button>
          <button class="btn-sm danger" size="mini">解绑</button>
        </view>
      </view>
      <button class="btn-add">+ 添加小红书账号</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
const accounts = ref([
  { name: '穿搭号', id: '100301', online: true },
  { name: '美食号', id: '100302', online: false }
])
const colors = ['linear-gradient(135deg,#FE2C55,#FF6480)', 'linear-gradient(135deg,#F59E0B,#FBBF24)']
function goBack() { uni.navigateBack() }
</script>

<style scoped>
.page { min-height: 100vh; background: #FAFAF9; }
.header { display: flex; align-items: center; padding: 12px 16px; background: #fff; border-bottom: 1px solid #F5F5F4; }
.back { font-size: 16px; color: #FE2C55; }
.title { flex: 1; text-align: center; font-size: 17px; font-weight: 600; color: #1C1917; margin-right: 40px; }
.body { padding: 16px; }
.account-card { background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 12px; box-shadow: 0 1px 3px rgba(0,0,0,.06); }
.acc-top { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.acc-av { width: 40px; height: 40px; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 16px; font-weight: 700; }
.acc-info { flex: 1; }
.acc-name { font-size: 14px; font-weight: 600; color: #292524; display: block; }
.acc-id { font-size: 11px; color: #A8A29E; }
.acc-status { font-size: 11px; font-weight: 600; padding: 3px 10px; border-radius: 16px; }
.acc-status.online { background: #DCFCE7; color: #16A34A; }
.acc-status.offline { background: #FEF3C7; color: #B45309; }
.acc-bottom { display: flex; gap: 8px; }
.btn-sm { font-size: 11px; padding: 4px 12px; border-radius: 6px; background: transparent; color: #57534E; border: 1px solid #E7E5E4; }
.btn-sm.danger { color: #EF4444; border-color: #FECACA; }
.btn-add { width: 100%; padding: 14px; border-radius: 12px; border: 1.5px dashed #D6D3D1; color: #A8A29E; font-size: 13px; background: transparent; }
</style>
