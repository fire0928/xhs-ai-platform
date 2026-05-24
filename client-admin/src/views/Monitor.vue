<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">调用监控</div><div style="font-size:13px;color:var(--cn-500)">实时查看大模型 API 调用记录</div></div>
      <div class="flex aic g3">
        <select class="ipt" style="width:140px" v-model="filterApiId" @change="loadData">
          <option value="">全部 API</option>
          <option v-for="m in apiModels" :key="m.id" :value="m.id">{{ m.name }}</option>
        </select>
        <button class="btn btn-p" @click="loadData">刷新</button>
      </div>
    </div>

    <div class="am-row">
      <div class="am"><div class="am-lb">今日总调用</div><div class="am-vl">{{ fmtNum(stats.totalCalls) }}</div></div>
      <div class="am"><div class="am-lb">成功率</div><div class="am-vl" style="color:var(--cs)">{{ stats.successRate }}%</div></div>
      <div class="am"><div class="am-lb">平均响应</div><div class="am-vl">{{ stats.avgDuration }}ms</div></div>
      <div class="am"><div class="am-lb">失败次数</div><div class="am-vl" style="color:var(--ce)">{{ fmtNum(stats.failCount) }}</div></div>
    </div>

    <div class="tbl-wrap">
      <div v-if="loading" style="text-align:center;padding:60px;color:var(--cn-400)">加载中...</div>
      <table v-else class="tbl">
        <thead><tr><th>时间</th><th>用户ID</th><th>Agent ID</th><th>API ID</th><th>结果</th><th>耗时</th></tr></thead>
        <tbody>
          <tr v-for="r in records" :key="r.id">
            <td style="font-size:11px;color:var(--cn-400)">{{ fmtTime(r.createTime) }}</td>
            <td class="fnm" style="font-size:11px">{{ r.userId || '-' }}</td>
            <td class="fnm" style="font-size:11px">{{ r.agentId || '-' }}</td>
            <td class="fnm" style="font-size:11px">{{ r.apiId || '-' }}</td>
            <td><span class="tag" :class="r.status===1?'tag-g':'tag-d'">{{ r.status===1?'成功':'失败' }}</span></td>
            <td class="fnm" style="font-size:11px">{{ r.durationMs }}ms</td>
          </tr>
          <tr v-if="records.length===0"><td colspan="6" style="text-align:center;color:var(--cn-400);padding:40px">暂无调用记录</td></tr>
        </tbody>
      </table>
      <div v-if="total>0" class="flex aic jbt" style="margin-top:16px">
        <span style="font-size:12px;color:var(--cn-400)">共 {{ total }} 条</span>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" :disabled="page<=1" @click="page--;loadData()">上一页</button>
          <span style="font-size:12px;color:var(--cn-500)">第 {{ page }} 页</span>
          <button class="btn btn-sm btn-g" :disabled="page*pageSize>=total" @click="page++;loadData()">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

const records = ref([])
const apiModels = ref([])
const loading = ref(true)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const filterApiId = ref('')
const stats = reactive({ totalCalls: 0, successRate: 0, avgDuration: 0, failCount: 0 })

function fmtTime(t) {
  if (!t) return '--'
  return t.substring(11, 19)
}
function fmtNum(n) {
  if (n == null) return '0'
  return Number(n).toLocaleString()
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (filterApiId.value) params.apiId = filterApiId.value
    const res = await api.get('/ai-call-logs', { params })
    if (res.data.code === 200 && res.data.data) {
      records.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
    // 获取统计
    const apiRes = await api.get('/ai/apis')
    if (apiRes.data.code === 200) apiModels.value = apiRes.data.data || []
    // 计算统计
    if (records.value.length > 0) {
      const successCount = records.value.filter(r => r.status === 1).length
      stats.totalCalls = total.value
      stats.successRate = Math.round(successCount / records.value.length * 100)
      stats.avgDuration = Math.round(records.value.reduce((s, r) => s + (r.durationMs||0), 0) / records.value.length)
      stats.failCount = records.value.filter(r => r.status === 0).length
    }
  } catch (e) {
    console.error('Monitor load error', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
