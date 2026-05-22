<template>
  <view class="page">
    <view class="header">
      <text class="back" @tap="goBack">‹ 返回</text>
      <text class="title">个人设置</text>
      <text class="save" @tap="save">保存</text>
    </view>
    <view class="body">
      <view class="section">
        <text class="s-title">AI 偏好</text>
        <view class="item">
          <text class="l">默认 Agent</text>
          <picker :range="agents" :value="aiIdx" @change="aiIdx = $event.detail.value">
            <text class="r">{{ agents[aiIdx] }}</text>
          </picker>
        </view>
        <view class="item">
          <text class="l">内容风格</text>
          <picker :range="styles" :value="styleIdx" @change="styleIdx = $event.detail.value">
            <text class="r">{{ styles[styleIdx] }}</text>
          </picker>
        </view>
      </view>
      <view class="section">
        <text class="s-title">通知</text>
        <view class="item">
          <text class="l">发布结果通知</text>
          <switch :checked="notifPub" @change="notifPub = $event.detail.value" color="#FE2C55" />
        </view>
        <view class="item">
          <text class="l">审核结果通知</text>
          <switch :checked="notifRev" @change="notifRev = $event.detail.value" color="#FE2C55" />
        </view>
        <view class="item">
          <text class="l">Cookie 过期提醒</text>
          <switch :checked="notifCookie" @change="notifCookie = $event.detail.value" color="#FE2C55" />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const agents = ['无偏好', '生活分享', '美食探店', '旅行攻略', '穿搭时尚']
const styles = ['自动匹配', '轻松随意', '专业严谨', '潮流时尚', '温暖治愈']
const aiIdx = ref(0)
const styleIdx = ref(0)
const notifPub = ref(true)
const notifRev = ref(true)
const notifCookie = ref(true)

function goBack() { uni.navigateBack() }
function save() {
  uni.showToast({ title: '已保存', icon: 'success' })
  uni.navigateBack()
}
</script>

<style scoped>
.page { min-height: 100vh; background: #FAFAF9; }
.header { display: flex; align-items: center; padding: 12px 16px; background: #fff; border-bottom: 1px solid #F5F5F4; }
.back { font-size: 16px; color: #FE2C55; }
.title { flex: 1; text-align: center; font-size: 17px; font-weight: 600; color: #1C1917; }
.save { font-size: 16px; color: #FE2C55; font-weight: 600; }
.body { padding: 16px; }
.section { background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 12px; }
.s-title { font-size: 13px; font-weight: 600; color: #78716C; display: block; margin-bottom: 12px; }
.item { display: flex; align-items: center; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #F5F5F4; }
.item:last-child { border-bottom: none; }
.l { font-size: 14px; color: #292524; }
.r { font-size: 13px; color: #78716C; }
</style>
