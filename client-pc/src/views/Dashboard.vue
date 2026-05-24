<template>
  <div>
    <div class="dash-header">
      <div class="dash-greeting">
        <div class="avatar">{{ userAvatar }}</div>
        <div>
          <div class="greeting-text">{{ greeting }}, {{ userName }} ✨</div>
          <div class="greeting-sub">今日有 <strong>{{ pendingCount }} 篇内容</strong> 待审核
            <span v-if="userMemberInfo" style="margin-left:12px;color:var(--ca-purple)">
              {{ ['免费版','标准版','专业版'][userMemberInfo.memberLevel || 0] || '免费版' }}
              <span v-if="userMemberInfo.memberExpireTime" style="color:var(--cn-400);font-size:12px">到期:{{ userMemberInfo.memberExpireTime.substring(0,10) }}</span>
            </span>
          </div>
          <div v-if="!hasXhsBound" style="margin-top:6px">
            <a href="#/accounts" style="font-size:12px;color:var(--cp-500);text-decoration:none">🔗 现在接入小红书账号，马上查看真实数据 →</a>
          </div>
        </div>
      </div>
      <div class="dash-actions">
        <button class="btn btn-ghost" @click="exportData">📊 导出报告</button>
        <button class="btn btn-ghost" @click="refreshAll">🔄 刷新数据</button>
        <button class="btn btn-ai" @click="$router.push('/ai-create')">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1L10 6H15L11 9.5L12.5 15L8 11.5L3.5 15L5 9.5L1 6H6L8 1Z" fill="white"/></svg>AI 一键创作
        </button>
      </div>
    </div>

    <div class="met-row">
      <MetricCard label="今日发布" :value="dashData.todayPublish || 0" :trend="'已发布'" :up="true" color="#FE2C55" />
      <MetricCard label="总互动量" :value="fmtK(dashData.totalInteractions || 0)" trend="总互动" :up="true" color="#F59E0B" />
      <MetricCard label="内容总数" :value="dashData.totalContents || 0" trend="累计创作" :up="true" color="#22C55E" />
      <MetricCard label="AI 创作次数" :value="dashData.aiCreationCount || 0" trend="AI 调用" :up="true" color="#8B5CF6" />
    </div>

    <div class="chart-row">
      <div class="cc">
        <div class="cc-h">
          <div><div class="cc-t">近 7 天趋势</div><div class="cc-st">发布量 & AI 调用量对比</div></div>
          <div class="legend"><span class="ld ld-pink"></span>发布量 <span class="ld ld-orange"></span>AI调用</div>
        </div>
        <svg viewBox="0 0 520 180" style="width:100%">
          <line x1="40" y1="155" x2="510" y2="155" stroke="#E7E5E4" stroke-width=".5"/>
          <polyline :points="trendPublishPoints" fill="none" stroke="#FE2C55" stroke-width="2.5" stroke-linecap="round"/>
          <polyline :points="trendAiPoints" fill="none" stroke="#F59E0B" stroke-width="2" stroke-dasharray="6 3"/>
          <text v-for="(t, i) in trendLabels" :key="i" :x="trendXs[i]" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">{{ t }}</text>
        </svg>
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">内容效果 Top 5</div></div>
        <HBar v-for="(item, i) in topRankings" :key="i" :label="item.title" :pct="item.pct" :val="String(item.value)" />
        <div v-if="topRankings.length === 0" style="text-align:center;color:var(--cn-400);padding:32px 0;font-size:13px">暂无内容数据</div>
      </div>
    </div>

    <div class="bot-row">
      <div class="cc">
        <div class="cc-h"><div class="cc-t">待审核内容</div><button class="btn btn-ghost btn-sm" @click="$router.push('/review')">查看全部</button></div>
        <ReviewItem v-for="item in pendingReviews" :key="item.id" :title="item.title" :agent="item.agent" :time="item.time" />
        <div v-if="pendingReviews.length === 0" style="text-align:center;color:var(--cn-400);padding:32px 0;font-size:13px">暂无待审核内容</div>
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">快捷操作</div></div>
        <div class="quick-grid">
          <div class="quick-card highlight" @click="$router.push('/ai-create')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cp-500)"><path d="M12 2L14.5 8.5H21L15.75 12.5L17.75 19L12 15L6.25 19L8.25 12.5L3 8.5H9.5L12 2Z" fill="currentColor"/></svg>
            <span>新建 AI 创作</span>
          </div>
          <div class="quick-card" @click="$router.push('/queue')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><rect x="3" y="4" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/><rect x="3" y="10" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/><rect x="3" y="16" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/></svg>
            <span>发布队列</span>
          </div>
          <div class="quick-card" @click="$router.push('/accounts')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2" fill="none"/><path d="M4 20C4 16.5 7 14 12 14C17 14 20 16.5 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
            <span>账号管理</span>
          </div>
          <div class="quick-card" @click="$router.push('/analytics')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><path d="M3 3H9V9H3V3ZM15 3H21V9H15V3ZM3 15H9V21H3V15ZM15 15H21V21H15V15Z" stroke="currentColor" stroke-width="2" fill="none"/></svg>
            <span>数据报告</span>
          </div>
          <div class="quick-card highlight" @click="$router.push('/membership')">
            <span style="font-size:24px">💎</span>
            <span>会员中心</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { analyticsApi, contentApi, userApi, accountApi } from '@/api'
import MetricCard from './components/DashboardMetric.vue'
import ReviewItem from './components/DashboardReviewItem.vue'
import HBar from './components/HBar.vue'

const dashData = ref({})
const trendData = ref([])
const topRankings = ref([])
const pendingReviews = ref([])
const userName = ref('创作者')
const userAvatar = ref('创')
const userMemberInfo = ref(null)
const hasXhsBound = ref(false)

const pendingCount = computed(() => pendingReviews.value.length)

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

function fmtK(v) {
  if (v >= 10000) return (v / 10000).toFixed(1) + '万'
  if (v >= 1000) return (v / 1000).toFixed(1) + 'K'
  return String(v)
}

// 趋势图数据
const trendLabels = computed(() => trendData.value.slice(-7).map(d => d.date?.substring(5) || ''))
const trendXs = computed(() => trendLabels.value.map((_, i) => 107 + i * 67))

const maxVal = computed(() => {
  const max = Math.max(...trendData.value.map(d => Math.max(d.publishCount || 0, d.aiCount || 0)), 1)
  return max
})
function trendY(v) { return 155 - (v / maxVal.value) * 135 }

const trendPublishPoints = computed(() =>
  trendData.value.slice(-7).map((d, i) => `${trendXs.value[i]},${trendY(d.publishCount || 0)}`).join(' ')
)
const trendAiPoints = computed(() =>
  trendData.value.slice(-7).map((d, i) => `${trendXs.value[i]},${trendY(d.aiCount || 0)}`).join(' ')
)

async function loadDashboard() {
  try {
    const res = await analyticsApi.dashboard()
    if (res.data.code === 200) dashData.value = res.data.data
  } catch { }

  try {
    const res = await analyticsApi.trends(7)
    if (res.data.code === 200) trendData.value = res.data.data || []
  } catch { }

  try {
    const res = await analyticsApi.contentRankings()
    if (res.data.code === 200) {
      const items = res.data.data || []
      const maxV = Math.max(...items.map(i => i.value || 0), 1)
      topRankings.value = items.slice(0, 5).map(i => ({ ...i, pct: Math.round((i.value || 0) / maxV * 100) }))
    }
  } catch { }

  // 待审核内容
  try {
    const res = await contentApi.myList({ page: 1, pageSize: 5, auditStatus: 1 })
    if (res.data.code === 200) {
      pendingReviews.value = (res.data.data?.records || []).map(c => ({
        id: c.id,
        title: c.title || '无标题',
        agent: c.agentName || 'AI创作',
        time: formatTime(c.createTime)
      }))
    }
  } catch { }

  // 用户信息
  try {
    const res = await userApi.info()
    if (res.data.code === 200) {
      const u = res.data.data
      userName.value = u.nickname || u.phone || '创作者'
      userAvatar.value = (u.nickname || '创').charAt(0)
      userMemberInfo.value = u
    }
  } catch { }

  // 检查是否绑定了小红书账号
  try {
    const res = await accountApi.list()
    if (res.data.code === 200) {
      hasXhsBound.value = (res.data.data || []).length > 0
    }
  } catch { }
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return Math.floor(diff / 86400000) + '天前'
}

function refreshAll() { loadDashboard() }

function exportData() {
  const csv = [
    ['指标', '数值'],
    ['今日发布', dashData.value.todayPublish || 0],
    ['总互动量', dashData.value.totalInteractions || 0],
    ['内容总数', dashData.value.totalContents || 0],
    ['AI创作次数', dashData.value.aiCreationCount || 0],
    ['待审核内容', pendingReviews.value.length],
  ].map(row => row.join(',')).join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `数据报告_${new Date().toISOString().slice(0,10)}.csv`
  link.click()
}

onMounted(loadDashboard)
</script>

<style scoped>
.dash-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.dash-greeting { display: flex; align-items: center; gap: 16px; }
.avatar { width: 44px; height: 44px; border-radius: 50%; background: var(--gb); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px; font-weight: 700; }
.greeting-text { font-size: 20px; font-weight: 700; color: var(--cn-900); }
.greeting-sub { font-size: 13px; color: var(--cn-500); }
.greeting-sub strong { color: var(--cp-500); font-weight: 600; }
.dash-actions { display: flex; align-items: center; gap: 12px; }
.met-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.chart-row { display: grid; grid-template-columns: 1.6fr 1fr; gap: 20px; margin-bottom: 24px; }
.bot-row { display: grid; grid-template-columns: 1.4fr 1fr; gap: 20px; }

.btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 500; transition: all .15s; white-space: nowrap; cursor: pointer; border: none; background: none; }
.btn svg { width: 16px; height: 16px; }
.btn-ghost { color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-100); }
.btn-ai { background: var(--gai); color: #fff; }
.btn-sm { padding: 4px 12px; font-size: 11px; }

.cc { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.cc-h { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.cc-t { font-size: 14px; font-weight: 600; color: var(--cn-800); }
.cc-st { font-size: 11px; color: var(--cn-500); }
.legend { display: flex; align-items: center; gap: 12px; font-size: 11px; color: var(--cn-400); }
.ld { width: 8px; height: 3px; border-radius: 2px; display: inline-block; margin-right: 4px; }
.ld-pink { background: var(--cp-500); }
.ld-orange { background: var(--ca-orange); }

.quick-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.quick-card { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; border-radius: 12px; border: 1px solid var(--cn-100); gap: 8px; cursor: pointer; transition: all .15s; }
.quick-card:hover { background: var(--cn-50); }
.quick-card.highlight { border-color: var(--cp-200); background: linear-gradient(135deg, #FFE0E5, #EDE9FE); }
.quick-card span { font-size: 13px; font-weight: 500; color: var(--cn-700); }
</style>
