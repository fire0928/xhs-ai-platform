<template>
  <view class="page">
    <view class="nav-bar">
      <view class="nav-status"><text>9:41</text><view class="nav-signal"><view class="signal-bar s1"></view><view class="signal-bar s2"></view><view class="signal-bar s3"></view><view class="signal-bar s4"></view></view></view>
      <view class="nav-title-bar">
        <text class="nav-title">消息</text>
        <view class="nav-capsule"><view class="capsule-dot"></view><view class="capsule-line"></view></view>
      </view>
    </view>

    <scroll-view scroll-y class="content">
      <view class="subscribe">
        <view class="sub-icon">i</view>
        <text class="sub-text">开启通知，不错过发布结果</text>
        <button class="sub-btn" size="mini">允许</button>
      </view>

      <view class="msg-tabs">
        <text class="msg-tab" :class="{ active: activeTab === 'all' }" @tap="activeTab = 'all'">全部</text>
        <text class="msg-tab" :class="{ active: activeTab === 'review' }" @tap="activeTab = 'review'">审核</text>
        <text class="msg-tab" :class="{ active: activeTab === 'publish' }" @tap="activeTab = 'publish'">发布</text>
      </view>

      <view class="msg-list">
        <view class="msg-card" v-for="m in filteredMsgs" :key="m.title">
          <view class="msg-thumb" :style="{ background: m.color }"></view>
          <view class="msg-info">
            <text class="msg-title">{{ m.title }}</text>
            <text class="msg-time">{{ m.time }}</text>
          </view>
          <view class="msg-status" :class="'s-' + m.statusType">{{ m.status }}</view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

const activeTab = ref('all')

const msgs = ref([
  { title: '周末探店｜宝藏咖啡店', time: '2分钟前 · 审核通过', status: '通过', statusType: 'ok', color: '#FFE0E5' },
  { title: '春日穿搭分享｜温柔通勤', time: '1小时前 · 需修改', status: '修改', statusType: 'warn', color: '#FEF3C7' },
  { title: '3天2夜云南攻略', time: '3小时前 · 发布成功', status: '成功', statusType: 'ok', color: '#EDE9FE' },
  { title: '家居好物推荐', time: '5小时前 · 发布失败', status: '失败', statusType: 'err', color: '#FFF1F3' },
  { title: '美食食谱｜15分钟晚餐', time: '6小时前 · 审核通过', status: '通过', statusType: 'ok', color: '#DCFCE7' }
])

const filteredMsgs = computed(() => {
  if (activeTab.value === 'all') return msgs.value
  return msgs.value.filter(m => {
    if (activeTab.value === 'review') return m.status.includes('审核') || m.status.includes('修改')
    return m.status.includes('发布') || m.status.includes('成功') || m.status.includes('失败')
  })
})
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
.subscribe { margin: 12px 16px; padding: 14px; background: #EFF6FF; border-radius: 12px; display: flex; align-items: center; gap: 10px; }
.sub-icon { width: 32px; height: 32px; border-radius: 8px; background: #DBEAFE; display: flex; align-items: center; justify-content: center; color: #3B82F6; font-weight: 700; flex-shrink: 0; }
.sub-text { flex: 1; font-size: 13px; color: #44403C; }
.sub-btn { padding: 6px 14px; border-radius: 14px; background: #3B82F6; color: #fff; font-size: 11px; font-weight: 600; border: none; }

.msg-tabs { display: flex; background: #fff; border-bottom: 1px solid #F5F5F4; padding: 0 16px; }
.msg-tab { padding: 10px 14px; font-size: 13px; color: #78716C; border-bottom: 2px solid transparent; }
.msg-tab.active { color: #FE2C55; border-bottom-color: #FE2C55; font-weight: 600; }

.msg-list { padding: 12px 16px; }
.msg-card { display: flex; gap: 10px; padding: 12px; background: #fff; border-radius: 10px; margin-bottom: 8px; align-items: center; box-shadow: 0 1px 2px rgba(0,0,0,.04); }
.msg-thumb { width: 44px; height: 44px; border-radius: 8px; flex-shrink: 0; }
.msg-info { flex: 1; min-width: 0; }
.msg-title { font-size: 13px; font-weight: 600; color: #292524; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.msg-time { font-size: 11px; color: #78716C; }
.msg-status { font-size: 10px; padding: 3px 8px; border-radius: 16px; font-weight: 500; }
.s-ok { background: #DCFCE7; color: #16A34A; }
.s-warn { background: #FEF3C7; color: #B45309; }
.s-err { background: #FEE2E2; color: #DC2626; }
</style>
