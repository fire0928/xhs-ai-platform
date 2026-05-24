<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">内容管理</div><div style="font-size:13px;color:var(--cn-500)">全量内容查看与审核管理</div></div>
      <div class="flex aic g3">
        <input class="ipt" v-model="keyword" placeholder="搜索标题..." style="width:180px" @keydown.enter="search" />
        <select class="ipt" style="width:130px" v-model="auditFilter" @change="search">
          <option value="">全部状态</option>
          <option value="0">草稿</option>
          <option value="1">审核中</option>
          <option value="2">已通过</option>
          <option value="3">审核失败</option>
        </select>
        <button class="btn btn-p" @click="search">搜索</button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="am-row" style="margin-bottom:20px">
      <div class="am"><div class="am-lb">总内容数</div><div class="am-vl">{{ stats.total }}</div></div>
      <div class="am"><div class="am-lb">待审核</div><div class="am-vl" style="color:var(--ca-orange)">{{ stats.pending }}</div></div>
      <div class="am"><div class="am-lb">已通过</div><div class="am-vl" style="color:var(--cs)">{{ stats.approved }}</div></div>
      <div class="am"><div class="am-lb">已拒绝</div><div class="am-vl" style="color:var(--ce)">{{ stats.rejected }}</div></div>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px;color:var(--cn-400)">加载中...</div>

    <div v-else class="tbl-wrap">
      <div class="tbl-bar"><span style="font-size:11px;color:var(--cn-400)">共 {{ total }} 条</span></div>
      <table class="tbl">
        <thead><tr><th>ID</th><th>用户</th><th>标题</th><th>标签</th><th>审核</th><th>终端</th><th>时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="c in contents" :key="c.id">
            <td class="fnm">#{{ c.id }}</td>
            <td>{{ c.userName || ('用户'+c.userId) }}</td>
            <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;cursor:pointer" @click="viewDetail(c)">{{ c.title || '-' }}</td>
            <td><span v-if="c.tags" class="tag tag-r">{{ c.tags }}</span><span v-else class="fnm">-</span></td>
            <td><span class="tag" :class="auditTagCls(c.auditStatus)">{{ auditLabel(c.auditStatus) }}</span></td>
            <td><span class="tag tag-n">{{ terminalLabel(c.createTerminal) }}</span></td>
            <td style="font-size:11px">{{ fmtTime(c.createTime) }}</td>
            <td><div class="flex aic g2">
              <button class="btn btn-sm btn-g" @click="viewDetail(c)">查看</button>
              <button v-if="c.auditStatus===1" class="btn btn-sm btn-s" @click="quickApprove(c)">通过</button>
              <button v-if="c.auditStatus===1" class="btn btn-sm btn-o" @click="quickReject(c)">拒绝</button>
              <button class="btn btn-sm btn-d" @click="handleDelete(c)">删除</button>
            </div></td>
          </tr>
          <tr v-if="contents.length===0"><td colspan="8" style="text-align:center;color:var(--cn-400);padding:40px">暂无内容数据</td></tr>
        </tbody>
      </table>
      <div v-if="total>0" class="flex aic jbt" style="margin-top:16px">
        <span></span>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" :disabled="page<=1" @click="page--;loadData()">上一页</button>
          <span style="font-size:12px;color:var(--cn-500)">第 {{ page }} 页 / 共 {{ Math.ceil(total/pageSize) }} 页</span>
          <button class="btn btn-sm btn-g" :disabled="page*pageSize>=total" @click="page++;loadData()">下一页</button>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail=false">
      <div class="modal-card" style="width:600px;max-height:80vh;overflow-y:auto">
        <div class="flex aic jbt mb4">
          <div class="card-t">内容详情 #{{ detail.id }}</div>
          <button class="btn btn-sm btn-g" @click="showDetail=false">&times;</button>
        </div>
        <div style="font-size:13px;color:var(--cn-600);line-height:2">
          <div><strong>用户:</strong> {{ detail.userName || ('用户' + detail.userId) }}</div>
          <div><strong>标题:</strong> {{ detail.title }}</div>
          <div><strong>标签:</strong> {{ detail.tags || '-' }}</div>
          <div><strong>文案 Agent:</strong> {{ detail.textAgentName || detail.agentName || '-' }}</div>
          <div><strong>图片 Agent:</strong> {{ detail.imageAgentName || '-' }}</div>
          <div><strong>终端:</strong> {{ terminalLabel(detail.createTerminal) }}</div>
          <div><strong>审核:</strong> <span class="tag" :class="auditTagCls(detail.auditStatus)">{{ auditLabel(detail.auditStatus) }}</span></div>
          <div><strong>创建时间:</strong> {{ detail.createTime }}</div>
          <div style="margin-top:8px"><strong>内容:</strong></div>
          <pre style="white-space:pre-wrap;font-size:12px;background:var(--cn-50);padding:12px;border-radius:8px;max-height:300px;overflow-y:auto;font-family:inherit">{{ detail.content || '-' }}</pre>
          <div v-if="detail.imageUrls" style="margin-top:8px">
            <strong>图片:</strong>
            <div style="display:flex;gap:8px;flex-wrap:wrap;margin-top:4px">
              <img v-for="(url,i) in detail.imageUrls.split(',')" :key="i" :src="url" style="width:120px;height:120px;object-fit:cover;border-radius:8px" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/api'

const contents = ref([])
const loading = ref(true)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const keyword = ref('')
const auditFilter = ref('')
const showDetail = ref(false)
const detail = ref({})
const stats = reactive({ total: 0, pending: 0, approved: 0, rejected: 0 })

function fmtTime(t) { return t ? t.substring(0, 16) : '--' }
function auditLabel(s) { const m={0:'草稿',1:'审核中',2:'已通过',3:'审核失败',4:'已删除'}; return m[s]||'未知' }
function auditTagCls(s) { const m={0:'n',1:'o',2:'g',3:'d',4:'r'}; return m[s]||'n' }
function terminalLabel(t) { const m={computer:'PC',mini_program:'小程序',harmony:'鸿蒙'}; return m[t]||t||'-' }

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (auditFilter.value !== '') params.auditStatus = auditFilter.value
    const res = await api.get('/contents', { params })
    if (res.data.code === 200 && res.data.data) {
      contents.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error('Content load error', e)
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  try {
    const res = await api.get('/contents', { params: { page: 1, pageSize: 1 } })
    if (res.data.code === 200 && res.data.data) {
      stats.total = res.data.data.total || 0
    }
    const p = await api.get('/contents', { params: { page: 1, pageSize: 1, auditStatus: 1 } })
    if (p.data.code === 200) stats.pending = p.data.data?.total || 0
    const a = await api.get('/contents', { params: { page: 1, pageSize: 1, auditStatus: 2 } })
    if (a.data.code === 200) stats.approved = a.data.data?.total || 0
    const r = await api.get('/contents', { params: { page: 1, pageSize: 1, auditStatus: 3 } })
    if (r.data.code === 200) stats.rejected = r.data.data?.total || 0
  } catch (e) {}
}

function search() { page.value = 1; loadData() }

function viewDetail(c) { detail.value = c; showDetail.value = true }

async function quickApprove(c) {
  try {
    await api.put(`/contents/${c.id}`, { auditStatus: 2 })
    loadData()
    loadStats()
  } catch (e) { alert('操作失败: ' + (e.response?.data?.message || e.message)) }
}

async function quickReject(c) {
  const reason = prompt('请输入拒绝原因（可选）：')
  try {
    await api.put(`/contents/${c.id}`, { auditStatus: 3, remark: reason })
    loadData()
    loadStats()
  } catch (e) { alert('操作失败: ' + (e.response?.data?.message || e.message)) }
}

async function handleDelete(c) {
  if (!confirm(`确认删除「${c.title || '#'+c.id}」?`)) return
  try {
    await api.delete(`/contents/${c.id}`)
    loadData()
    loadStats()
  } catch (e) {
    alert('删除失败: ' + (e.response?.data?.message || e.message))
  }
}

onMounted(() => { loadData(); loadStats() })
</script>

<style scoped>
.modal-overlay{position:fixed;inset:0;background:rgba(0,0,0,.4);z-index:1000;display:flex;align-items:center;justify-content:center}
.modal-card{background:#fff;border-radius:16px;padding:24px 28px;max-width:90vw;box-shadow:var(--shl)}
</style>
