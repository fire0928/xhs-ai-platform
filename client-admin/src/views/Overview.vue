<template>
  <div>
    <div class="flex aic jbt mb6">
      <div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900)">系统总览</div>
        <div style="font-size:13px;color:var(--cn-500)">实时监控平台运行状态与关键指标</div>
      </div>
      <div class="flex aic g3">
        <button class="btn btn-g" @click="refresh">
          <svg viewBox="0 0 16 16" fill="none"><path d="M3 8a5 5 0 0 1 9.3-2M13 8a5 5 0 0 1-9.3 2M2 8h2M12 8h2" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
        <button class="btn btn-p" @click="exportCSV">
          <svg viewBox="0 0 16 16" fill="none"><path d="M2 4H10L14 8V13A1 1 0 0 1 13 14H2A1 1 0 0 1 1 13V5A1 1 0 0 1 2 4Z" stroke="currentColor" stroke-width="1.3"/><path d="M10 4V8H14" stroke="currentColor" stroke-width="1.3"/><path d="M5 11H10M5 8H8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/></svg>
          导出报表
        </button>
      </div>
    </div>

    <div class="am-row">
      <div class="am">
        <div class="am-lb">总用户数</div>
        <div class="am-vl">{{ fmtNum(stats.totalUsers) }}</div>
        <div class="am-ch" style="color:var(--cs)">活跃用户 {{ fmtNum(stats.activeUsers) }}</div>
      </div>
      <div class="am">
        <div class="am-lb">日活用户</div>
        <div class="am-vl">{{ fmtNum(stats.dau) }}</div>
        <div class="am-ch" style="color:var(--cs)">今日活跃</div>
      </div>
      <div class="am">
        <div class="am-lb">AI 调用总量</div>
        <div class="am-vl">{{ fmtNum(stats.aiTotal) }}</div>
        <div class="am-ch" style="color:var(--cs)">累计生成内容 {{ fmtNum(stats.totalContents) }} 条</div>
      </div>
      <div class="am">
        <div class="am-lb">系统健康度</div>
        <div class="am-vl" :style="{ color: systemHealthy ? 'var(--cs)' : 'var(--cp-400)' }">{{ systemHealthy ? '正常' : '异常' }}</div>
        <div class="am-ch" style="color:var(--cn-500)">待发布 {{ fmtNum(stats.pendingPublishes) }} / 今日已发 {{ fmtNum(stats.publishedToday) }}</div>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:1.5fr 1fr;gap:20px;margin-bottom:24px">
      <div class="card">
        <div class="card-h">
          <div><div class="card-t">用户增长趋势</div><div class="card-st">近30天注册&活跃</div></div>
        </div>
        <svg viewBox="0 0 520 180" style="width:100%">
          <line x1="40" y1="155" x2="510" y2="155" stroke="#E7E5E4" stroke-width=".5"/>
          <line x1="40" y1="87" x2="510" y2="87" stroke="#E7E5E4" stroke-width=".5" stroke-dasharray="4"/>
          <line x1="40" y1="20" x2="510" y2="20" stroke="#E7E5E4" stroke-width=".5" stroke-dasharray="4"/>
          <polyline points="80,130 150,118 220,105 290,95 360,82 430,68 500,55" fill="none" stroke="#3B82F6" stroke-width="2.5" stroke-linecap="round"/>
          <polyline points="80,95 150,88 220,82 290,75 360,65 430,58 500,48" fill="none" stroke="#8B5CF6" stroke-width="2" stroke-dasharray="6 3"/>
          <circle cx="500" cy="55" r="4" fill="#3B82F6" stroke="#fff" stroke-width="2"/>
          <circle cx="500" cy="48" r="4" fill="#8B5CF6" stroke="#fff" stroke-width="2"/>
        </svg>
      </div>

      <div class="card">
        <div class="card-h"><div class="card-t">发布队列概览</div></div>
        <div class="hb"><div class="hb-l">待发布</div><div class="hb-t"><div class="hb-f" :style="{ width: pct(stats.pendingPublishes, stats.pendingPublishes + stats.publishedToday) + '%', background: 'var(--ci)' }">{{ fmtNum(stats.pendingPublishes) }} 条</div></div></div>
        <div class="hb"><div class="hb-l">今日已发</div><div class="hb-t"><div class="hb-f" :style="{ width: pct(stats.publishedToday, stats.pendingPublishes + stats.publishedToday) + '%', background: 'var(--cs)' }">{{ fmtNum(stats.publishedToday) }} 条</div></div></div>
        <div class="hb"><div class="hb-l">累计内容</div><div class="hb-t"><div class="hb-f" style="width:100%;background:var(--cp-500)">{{ fmtNum(stats.totalContents) }} 条</div></div></div>
      </div>
    </div>

    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px">
      <div class="card">
        <div class="card-h">
          <div class="card-t">服务状态</div>
          <span class="tag" :class="systemHealthy ? 'tag-g' : 'tag-d'">
            <span class="sd" :class="systemHealthy ? 'sd-g' : 'sd-d'"></span>
            {{ systemHealthy ? '全部正常' : '部分异常' }}
          </span>
        </div>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
          <div style="padding:12px;border-radius:8px;border:1px solid var(--cn-100)" v-for="s in stats.services" :key="s.name">
            <div style="font-size:11px;color:var(--cn-500);margin-bottom:4px">{{ s.name }}</div>
            <div class="flex aic jbt">
              <span style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ s.latency }}</span>
              <span class="sd" :class="s.status === 'up' ? 'sd-g' : 'sd-d'"></span>
            </div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-h"><div class="card-t">最近操作日志</div></div>
        <div style="font-size:11px;color:var(--cn-500)">
          <div v-if="logs.length === 0" style="padding:32px 0;text-align:center;color:var(--cn-400)">暂无操作日志</div>
          <div class="flex aic g3" v-for="log in logs" :key="log.id" style="padding:8px 0;border-bottom:1px solid var(--cn-50)">
            <span style="color:var(--cn-400);width:50px;flex-shrink:0">{{ fmtTime(log.createTime) }}</span>
            <span class="tag" :class="'tag-' + getTagClass(log.operation)">{{ log.operation }}</span>
            <span style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ log.content }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useAdminStore } from '@/store'

const store = useAdminStore()
const stats = computed(() => store.stats)
const logs = computed(() => store.logs || [])
const loading = ref(false)
const systemHealthy = computed(() => {
  const svcs = stats.value.services || []
  if (svcs.length === 0) return true
  return svcs.every(s => s.status === 'up')
})

// 操作类型 → 标签颜色映射
const tagClassMap = {
  '用户': 'g', '注册': 'g', '登录': 'g',
  'AI': 'p', 'Agent': 'p', '创作': 'p',
  '内容': 'r', '发布': 'r',
  '会员': 'o', '升级': 'o',
  '违规': 'd', '删除': 'd', '禁用': 'd',
  '系统': 'b', '配置': 'b', '设置': 'b'
}

function getTagClass(op) {
  if (!op) return 'g'
  for (const [key, cls] of Object.entries(tagClassMap)) {
    if (op.includes(key)) return cls
  }
  return 'g'
}

function fmtTime(t) {
  if (!t) return '--'
  return t.substring(11, 16) // HH:mm from "yyyy-MM-dd HH:mm:ss"
}

function fmtNum(n) {
  if (n == null) return '--'
  if (n >= 10000) return (n / 10000).toFixed(1) + '万'
  return Number(n).toLocaleString()
}

function pct(val, total) {
  if (!total || total === 0) return 0
  return Math.round((val / total) * 100)
}

async function refresh() {
  loading.value = true
  await Promise.all([store.fetchStats(), store.fetchLogs()])
  loading.value = false
}

function exportCSV() {
  const s = stats.value
  const rows = [
    ['指标', '数值'],
    ['总用户数', s.totalUsers],
    ['活跃用户数', s.activeUsers],
    ['日活用户', s.dau],
    ['AI调用总量', s.aiTotal],
    ['累计内容数', s.totalContents],
    ['待发布数', s.pendingPublishes],
    ['今日已发布', s.publishedToday],
    ['系统健康度', systemHealthy.value ? '正常' : '异常'],
    [],
    ['服务', '状态', '延迟'],
    ...((s.services || []).map(sv => [sv.name, sv.status === 'up' ? '正常' : '异常', sv.latency]))
  ]
  const bom = '\uFEFF'
  const csv = bom + rows.map(r => r.join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `系统报表_${new Date().toISOString().substring(0, 10)}.csv`
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => {
  store.fetchStats()
  store.fetchLogs()
})
</script>
