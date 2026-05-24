<template>
  <div>
    <div class="atr">
      <span v-for="t in timeRanges" :key="t.days" :class="['att', { active: currentDays === t.days }]" @click="switchRange(t.days)">{{ t.label }}</span>
    </div>

    <div class="am-row">
      <AnalyticsStat label="总阅读量" :value="fmtK(dashData.readCount)" :change="'总阅读'" color="var(--cs)" />
      <AnalyticsStat label="总互动量" :value="fmtK(dashData.totalInteractions)" :change="'总互动'" color="var(--cs)" />
      <AnalyticsStat label="AI 创作占比" :value="dashData.aiRatio + '%'" :change="'创作占比'" color="var(--cs)" />
      <AnalyticsStat label="内容总数" :value="dashData.totalContents || 0" :change="'累计篇数'" color="var(--ca-orange)" />
    </div>

    <div class="cc" style="margin-bottom:24px">
      <div class="cc-h"><div class="cc-t">发布与 AI 调用趋势</div></div>
      <svg viewBox="0 0 520 180" style="width:100%">
        <line x1="40" y1="155" x2="510" y2="155" stroke="#E7E5E4" stroke-width=".5"/>
        <polyline :points="trendPublishPoints" fill="none" stroke="#FE2C55" stroke-width="2.5" stroke-linecap="round"/>
        <polyline :points="trendAiPoints" fill="none" stroke="#F59E0B" stroke-width="2" stroke-dasharray="6 3"/>
        <text v-for="(t, i) in trendLabels" :key="i" :x="trendXs[i]" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">{{ t }}</text>
      </svg>
    </div>

    <div class="ab-row">
      <div class="cc">
        <div class="cc-h"><div class="cc-t">内容效果 Top 10</div></div>
        <RankItem v-for="(item, i) in topRankings" :key="i" :rank="i + 1" :title="item.title" :value="String(item.value)" :bg="item.bg || '#FFE0E5'" />
        <div v-if="topRankings.length === 0" style="text-align:center;color:var(--cn-400);padding:32px 0;font-size:13px">暂无内容排名数据</div>
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">创作方式分布</div></div>
        <div class="hb-row">
          <div class="hb-label">AI 创作</div>
          <div class="hb-bar"><div class="hb-fill" :style="{ width: aiPct + '%', background: 'var(--gai)' }">{{ aiPct }}%</div></div>
        </div>
        <div class="hb-row">
          <div class="hb-label">手动创作</div>
          <div class="hb-bar"><div class="hb-fill" :style="{ width: manualPct + '%', background: 'var(--cn-300)' }">{{ manualPct }}%</div></div>
        </div>
        <div class="ai-compare">
          <div class="comp-title">创作数据摘要</div>
          <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px">
            <div class="comp-stat">
              <div class="comp-val" style="color:var(--ca-purple)">{{ dashData.aiCreationCount || 0 }}</div>
              <div class="comp-label">AI 创作次数</div>
            </div>
            <div class="comp-stat">
              <div class="comp-val" style="color:var(--cn-600)">{{ Math.max(0, (dashData.totalContents || 0) - (dashData.aiCreationCount || 0)) }}</div>
              <div class="comp-label">其他内容数</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { analyticsApi } from '@/api'
import AnalyticsStat from './components/AnalyticsStat.vue'
import RankItem from './components/RankItem.vue'

const timeRanges = [
  { days: 7, label: '7天' },
  { days: 30, label: '30天' },
  { days: 90, label: '90天' }
]

const currentDays = ref(30)
const dashData = ref({})
const trendData = ref([])
const topRankings = ref([])
const loading = ref(false)

const aiPct = computed(() => {
  const total = (dashData.value.totalContents || 1)
  return Math.round((dashData.value.aiCreationCount || 0) / total * 100)
})
const manualPct = computed(() => Math.max(0, 100 - aiPct.value))

function fmtK(v) {
  if (!v && v !== 0) return '--'
  if (v >= 10000) return (v / 10000).toFixed(1) + '万'
  if (v >= 1000) return (v / 1000).toFixed(1) + 'K'
  return String(Math.round(v))
}

// 趋势图数据
const trendLabels = computed(() => trendData.value.map(d => d.date?.substring(5) || ''))
const trendXs = computed(() => {
  const total = trendLabels.value.length
  if (total <= 1) return ['255']
  return trendLabels.value.map((_, i) => 107 + i * (403 / (total - 1)))
})

const maxTrendVal = computed(() => {
  const max = Math.max(...trendData.value.map(d => Math.max(d.publishCount || 0, d.aiCount || 0)), 1)
  return max
})
function yPos(v) { return 155 - (v / maxTrendVal.value) * 135 }

const trendPublishPoints = computed(() =>
  trendData.value.map((d, i) => `${trendXs.value[i]},${yPos(d.publishCount || 0)}`).join(' ')
)
const trendAiPoints = computed(() =>
  trendData.value.map((d, i) => `${trendXs.value[i]},${yPos(d.aiCount || 0)}`).join(' ')
)

async function loadAll() {
  loading.value = true
  try {
    const res = await analyticsApi.dashboard()
    if (res.data.code === 200) {
      dashData.value = res.data.data || {}
    } else {
      dashData.value = {}
    }
  } catch (e) {
    console.error('dashboard error:', e)
    dashData.value = {}
  }

  try {
    const res = await analyticsApi.trends(currentDays.value)
    if (res.data.code === 200) {
      trendData.value = res.data.data || []
    } else {
      trendData.value = []
    }
  } catch (e) {
    console.error('trends error:', e)
    trendData.value = []
  }

  try {
    const res = await analyticsApi.contentRankings()
    if (res.data.code === 200) {
      topRankings.value = res.data.data || []
    } else {
      topRankings.value = []
    }
  } catch (e) {
    console.error('rankings error:', e)
    topRankings.value = []
  }
  loading.value = false
}

function switchRange(days) {
  currentDays.value = days
  loadAll()
}

onMounted(loadAll)
</script>

<style scoped>
.atr { display: flex; gap: 8px; margin-bottom: 24px; }
.att { padding: 8px 16px; border-radius: 9999px; font-size: 13px; font-weight: 500; color: var(--cn-500); cursor: pointer; transition: all .15s; }
.att:hover { color: var(--cn-700); background: var(--cn-100); }
.att.active { background: var(--cn-800); color: #fff; }
.am-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.ab-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }

.cc { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.cc-h { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.cc-t { font-size: 14px; font-weight: 600; color: var(--cn-800); }

.hb-row { margin-bottom: 16px; }
.hb-label { font-size: 11px; color: var(--cn-500); margin-bottom: 6px; }
.hb-bar { height: 20px; background: var(--cn-100); border-radius: 9999px; overflow: hidden; }
.hb-fill { height: 100%; border-radius: 9999px; display: flex; align-items: center; padding-left: 8px; font-size: 11px; font-weight: 600; color: #fff; min-width: 40px; }

.ai-compare { margin-top: 20px; padding: 16px; background: linear-gradient(135deg, #FFE0E5, #EDE9FE); border-radius: 12px; }
.comp-title { font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 8px; }
.comp-stat { text-align: center; }
.comp-val { font-family: 'JetBrains Mono', monospace; font-size: 20px; font-weight: 700; }
.comp-label { font-size: 11px; color: var(--cn-500); }
</style>
