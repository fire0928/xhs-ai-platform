<template>
  <div>
    <div class="flex aic jbt mb6">
      <div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900)">系统总览</div>
        <div style="font-size:13px;color:var(--cn-500)">实时监控平台运行状态与关键指标</div>
      </div>
      <div class="flex aic g3">
        <button class="btn btn-g" @click="refresh"><svg viewBox="0 0 16 16" fill="none"><path d="M2 8H14M8 2V14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>{{ loading ? '刷新中...' : '刷新' }}</button>
        <button class="btn btn-p"><svg viewBox="0 0 16 16" fill="none"><path d="M2 4H14M2 8H10M2 12H12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>导出报表</button>
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
        <div class="am-vl" style="color:var(--cs)">{{ stats.systemHealth }}</div>
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
        <div class="card-h"><div class="card-t">服务状态</div><span class="tag tag-g"><span class="sd sd-g"></span>全部正常</span></div>
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
          <div style="padding:12px;border-radius:8px;border:1px solid var(--cn-100)" v-for="s in services" :key="s.name">
            <div style="font-size:11px;color:var(--cn-500);margin-bottom:4px">{{ s.name }}</div>
            <div class="flex aic jbt"><span style="font-size:14px;font-weight:600;color:var(--cn-800)">{{ s.latency }}</span><span class="sd sd-g"></span></div>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-h"><div class="card-t">最近操作日志</div></div>
        <div style="font-size:11px;color:var(--cn-500)">
          <div class="flex aic g3 mb3" v-for="log in logs" :key="log.time" style="padding:8px 0;border-bottom:1px solid var(--cn-50)">
            <span style="color:var(--cn-400);width:50px">{{ log.time }}</span>
            <span class="tag" :class="'tag-' + log.tagClass">{{ log.tag }}</span><span>{{ log.msg }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useAdminStore } from '@/store'

const store = useAdminStore()
const loading = ref(false)

const services = ref([
  { name: 'API 服务', latency: '12ms' },{ name: '数据库', latency: '3ms' },
  { name: 'Redis', latency: '1ms' },{ name: '发布引擎', latency: '正常' }
])

const logs = ref([
  { time:'10:32',tag:'用户',tagClass:'g',msg:'新增用户 138****6721 (鸿蒙端)' },
  { time:'10:28',tag:'AI',tagClass:'p',msg:'Agent「美食探店」配置更新' },
  { time:'10:15',tag:'违规',tagClass:'d',msg:'检测到违规内容 #8234 已自动下架' },
  { time:'09:50',tag:'会员',tagClass:'r',msg:'用户 155****8832 升级为专业版' },
  { time:'09:30',tag:'系统',tagClass:'o',msg:'大模型 API 密钥轮换完成' }
])

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
  await store.fetchStats()
  loading.value = false
}

onMounted(() => {
  store.fetchStats()
})
</script>
