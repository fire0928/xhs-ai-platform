<template>
  <div class="page">
    <div class="header">
      <div>
        <h2 class="h-xl">创作记录</h2>
        <p class="sub">查看和管理你的 AI 创作历史，点击记录可继续生成</p>
      </div>
    </div>

    <div v-if="records.length === 0 && !loading" class="empty-state">
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
        <rect x="12" y="8" width="40" height="48" rx="6" stroke="var(--cn-300)" stroke-width="2" fill="var(--cn-50)"/>
        <path d="M22 24h20M22 32h16M22 40h12" stroke="var(--cn-300)" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <h3>暂无创作记录</h3>
      <p>开始你的第一次 AI 创作吧</p>
      <button class="btn btn-primary" @click="$router.push('/ai-create')">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2v12M2 8h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        去创作
      </button>
    </div>

    <div v-else class="records-list">
      <div v-for="item in records" :key="item.id" class="record-card" @click="continueCreate(item)">
        <div class="record-main">
          <div class="record-title">{{ item.title || '无标题' }}</div>
          <div class="record-meta">
            <span class="record-time">{{ formatTime(item.createTime) }}</span>
            <span class="record-status" :class="statusClass(item.auditStatus)">{{ statusText(item.auditStatus) }}</span>
          </div>
          <div class="record-summary">{{ item.content ? item.content.substring(0, 80) + '...' : '暂无文案内容' }}</div>
          <div class="record-stats">
            <span class="stat-item">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 3h10v8H2z" stroke="currentColor" stroke-width="1.2" rx="1"/><path d="M2 6h10M5 3v8" stroke="currentColor" stroke-width="1.2"/></svg>
              文案 {{ item.hasContent ? '已生成' : '未生成' }}
            </span>
            <span class="stat-item">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><rect x="2" y="3" width="10" height="8" rx="1" stroke="currentColor" stroke-width="1.2"/><circle cx="7" cy="7" r="2" stroke="currentColor" stroke-width="1.2"/></svg>
              图片 {{ item.imageCount }} 张
            </span>
            <span class="stat-item" v-if="item.agentName">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M7 1L8.5 5H12L9 7.5L10 11.5L7 9L4 11.5L5 7.5L2 5H5.5L7 1Z" stroke="currentColor" stroke-width="1.2"/></svg>
              {{ item.agentName }}
            </span>
          </div>
        </div>
        <div class="record-arrow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M6 3l5 5-5 5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        </div>
      </div>
    </div>

    <div v-if="loading" style="text-align:center;padding:40px;color:var(--cn-400)">加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { contentApi, aiApi } from '@/api'

const router = useRouter()
const records = ref([])
const loading = ref(false)

const statusTextMap = { 0: '草稿', 1: '审核中', 2: '已通过', 3: '已拒绝', 4: '已删除' }
const statusClassMap = { 0: 'draft', 1: 'pending', 2: 'approved', 3: 'rejected', 4: 'deleted' }

function statusText(s) { return statusTextMap[s] || '草稿' }
function statusClass(s) { return statusClassMap[s] || 'draft' }

function formatTime(t) {
  if (!t) return '--'
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return t.substring(0, 10)
}

async function loadRecords() {
  loading.value = true
  try {
    const res = await contentApi.myList({ page: 1, pageSize: 50 })
    if (res.data.code === 200) {
      const list = res.data.data?.records || []
      // 获取agent信息用于显示名称
      let agents = []
      try {
        const agentRes = await aiApi.getAgents()
        if (agentRes.data.code === 200) agents = agentRes.data.data || []
      } catch {}
      records.value = list.map(item => {
        const agent = agents.find(a => a.id === item.agentId)
        const imageUrls = item.imageUrls ? item.imageUrls.split(',').filter(Boolean) : []
        return {
          id: item.id,
          title: item.title || '无标题',
          content: item.content || '',
          createTime: item.createTime,
          auditStatus: item.auditStatus,
          hasContent: !!(item.content && item.content.length > 10),
          imageCount: imageUrls.length,
          agentName: agent ? agent.name : '',
          imageUrls: imageUrls,
          agentId: item.agentId,
          tags: item.tags || ''
        }
      })
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

function continueCreate(item) {
  // 如果已有图文内容，进入编辑页面；否则进入 AI 创作继续生成
  if (item.hasContent && item.imageCount > 0) {
    const draftData = {
      id: item.id,
      title: item.title,
      content: item.content,
      agentId: item.agentId,
      imageUrls: item.imageUrls,
      tags: item.tags
    }
    sessionStorage.setItem('editDraft', JSON.stringify(draftData))
    router.push('/content-edit/' + item.id)
  } else {
    // 未完成的内容，回到 AI 创作继续生成
    const draftData = {
      id: item.id,
      title: item.title,
      content: item.content,
      agentId: item.agentId,
      imageUrls: item.imageUrls,
      tags: item.tags,
      fromRecord: true
    }
    sessionStorage.setItem('creationDraft', JSON.stringify(draftData))
    router.push('/ai-create')
  }
}

onMounted(loadRecords)
</script>

<style scoped>
.page { padding: 32px; }
.header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.h-xl { font-size: 20px; font-weight: 700; color: var(--cn-900); margin-bottom: 4px; }
.sub { font-size: 13px; color: var(--cn-500); }
.empty-state { text-align: center; padding: 64px 32px; }
.empty-state svg { margin-bottom: 16px; opacity: .4; }
.empty-state h3 { font-size: 16px; font-weight: 600; color: var(--cn-700); margin-bottom: 8px; }
.empty-state p { font-size: 13px; color: var(--cn-500); margin-bottom: 24px; }
.btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; white-space: nowrap; border: none; }
.btn-primary { background: var(--cp-500); color: #fff; }
.btn-primary:hover { background: var(--cp-600); }
.records-list { display: flex; flex-direction: column; gap: 12px; }
.record-card { background: #fff; border-radius: 12px; padding: 16px 20px; border: 1px solid var(--cn-100); cursor: pointer; transition: all .15s; display: flex; align-items: center; gap: 16px; }
.record-card:hover { border-color: var(--cp-300); box-shadow: 0 2px 12px rgba(0,0,0,.06); }
.record-main { flex: 1; min-width: 0; }
.record-title { font-size: 15px; font-weight: 600; color: var(--cn-800); margin-bottom: 6px; }
.record-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.record-time { font-size: 12px; color: var(--cn-400); }
.record-status { font-size: 11px; padding: 2px 8px; border-radius: 9999px; font-weight: 500; }
.record-status.draft { background: var(--cn-100); color: var(--cn-500); }
.record-status.pending { background: #DBEAFE; color: #1D4ED8; }
.record-status.approved { background: #DCFCE7; color: #15803D; }
.record-status.rejected { background: #FEE2E2; color: #B91C1C; }
.record-status.deleted { background: var(--cn-100); color: var(--cn-400); }
.record-summary { font-size: 13px; color: var(--cn-500); line-height: 1.5; margin-bottom: 10px; }
.record-stats { display: flex; gap: 16px; flex-wrap: wrap; }
.stat-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: var(--cn-400); }
.stat-item svg { color: var(--cn-400); }
.record-arrow { color: var(--cn-300); flex-shrink: 0; }
.record-card:hover .record-arrow { color: var(--cp-500); }
</style>
