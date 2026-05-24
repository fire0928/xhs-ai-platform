<template>
  <div class="msg-page">
    <div class="page-header">
      <div class="flex aic jbt">
        <div>
          <h1>消息中心</h1>
          <p v-if="unreadCount>0" style="font-size:12px;color:var(--ce)">{{ unreadCount }} 条未读消息</p>
        </div>
        <button v-if="unreadCount>0" class="btn-mark-all" @click="markAllRead">全部标为已读</button>
      </div>
    </div>

    <div v-if="loading" style="text-align:center;padding:60px;color:var(--cn-400)">加载中...</div>

    <div v-else-if="messages.length===0" class="empty">
      <div style="font-size:48px;margin-bottom:12px">📭</div>
      <div style="font-size:14px;color:var(--cn-400)">暂无消息</div>
    </div>

    <div v-else class="msg-list">
      <div v-for="msg in messages" :key="msg.readId" class="msg-item" :class="{unread:!msg.isRead}" @click="readMsg(msg)">
        <div class="msg-icon">
          <span v-if="msg.msgType==='system'" style="color:#1677ff">🔔</span>
          <span v-else-if="msg.msgType==='activity'" style="color:#ff9800">🎉</span>
          <span v-else style="color:#e91e63">🤖</span>
        </div>
        <div class="msg-body">
          <div class="flex aic jbt">
            <span class="msg-title" :class="{bold:!msg.isRead}">{{ msg.title }}</span>
            <span class="msg-time">{{ fmtTime(msg.sendTime) }}</span>
          </div>
          <div class="msg-content" v-html="stripHtml(msg.content,80)"></div>
        </div>
        <div v-if="!msg.isRead" class="unread-dot"></div>
      </div>
    </div>

    <!-- 消息详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail=false">
      <div class="modal-card">
        <div class="flex aic jbt mb4">
          <div class="modal-title">{{ detailMsg.title }}</div>
          <button class="btn-close" @click="showDetail=false">&times;</button>
        </div>
        <div style="font-size:11px;color:var(--cn-400);margin-bottom:12px">
          {{ detailMsg.sendTime ? detailMsg.sendTime.substring(0,16) : '' }}
          <span class="tag" style="margin-left:8px">{{ {system:'系统通知',activity:'活动通知',ai_agent:'AI创作'}[detailMsg.msgType] }}</span>
        </div>
        <div class="msg-detail-content" v-html="detailMsg.content"></div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="total>20" class="flex jc" style="margin-top:20px">
      <button class="btn-page" :disabled="page<=1" @click="page--;loadMessages()">上一页</button>
      <span style="font-size:12px;color:var(--cn-500);padding:0 16px">第 {{ page }} 页</span>
      <button class="btn-page" :disabled="page*20>=total" @click="page++;loadMessages()">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const messages = ref([])
const loading = ref(true)
const page = ref(1)
const total = ref(0)
const unreadCount = ref(0)
const showDetail = ref(false)
const detailMsg = ref({})

function fmtTime(t) { return t ? t.substring(0,16) : '-' }
function stripHtml(html, len) {
  if (!html) return ''
  const t = html.replace(/<[^>]*>/g, '').replace(/&nbsp;/g, ' ')
  return t.length > len ? t.substring(0, len) + '...' : t
}

async function loadMessages() {
  loading.value = true
  try {
    const res = await api.get('/user/messages', { params: { page: page.value, pageSize: 20 } })
    if (res.data.code===200 && res.data.data) {
      messages.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
    const c = await api.get('/user/messages/unread-count')
    if (c.data.code===200) unreadCount.value = c.data.data || 0
  } catch(e) { console.error(e) } finally { loading.value = false }
}

async function readMsg(msg) {
  if (!msg.isRead) {
    try { await api.post(`/user/messages/${msg.readId}/read`); msg.isRead = 1; unreadCount.value = Math.max(0, unreadCount.value-1) } catch(e) {}
  }
  detailMsg.value = msg
  showDetail.value = true
}

async function markAllRead() {
  try { await api.post('/user/messages/read-all'); loadMessages() } catch(e) {}
}

onMounted(loadMessages)
</script>

<style scoped>
.msg-page{max-width:800px;margin:0 auto;padding:32px 24px}
.page-header{margin-bottom:24px}
.page-header h1{font-size:24px;font-weight:700;color:var(--cn-900);margin-bottom:4px}
.btn-mark-all{padding:6px 16px;border-radius:6px;font-size:12px;border:1px solid var(--cn-200);background:#fff;color:var(--cn-500);cursor:pointer}
.btn-mark-all:hover{background:var(--cn-50)}
.empty{text-align:center;padding:80px 0}
.msg-list{}
.msg-item{display:flex;align-items:flex-start;gap:14px;padding:16px 20px;background:#fff;border:1px solid var(--cn-100);border-radius:12px;margin-bottom:8px;cursor:pointer;transition:box-shadow .2s}
.msg-item:hover{box-shadow:0 2px 12px rgba(0,0,0,.06)}
.msg-item.unread{background:#f8f9ff;border-color:#e3e8ff}
.msg-icon{font-size:24px;width:40px;height:40px;display:flex;align-items:center;justify-content:center;background:var(--cn-50);border-radius:10px;flex-shrink:0}
.msg-body{flex:1;min-width:0}
.msg-title{font-size:14px;color:var(--cn-800)}.msg-title.bold{font-weight:600}
.msg-time{font-size:11px;color:var(--cn-400);white-space:nowrap;margin-left:12px}
.msg-content{font-size:12px;color:var(--cn-500);margin-top:4px;line-height:1.6}
.unread-dot{width:8px;height:8px;border-radius:50%;background:var(--ce);flex-shrink:0;margin-top:6px}
.modal-overlay{position:fixed;inset:0;background:rgba(0,0,0,.4);z-index:1000;display:flex;align-items:center;justify-content:center}
.modal-card{background:#fff;border-radius:16px;padding:24px 28px;width:600px;max-width:90vw;max-height:80vh;overflow-y:auto;box-shadow:0 20px 60px rgba(0,0,0,.2)}
.modal-title{font-size:18px;font-weight:700;color:var(--cn-900)}
.btn-close{padding:4px 10px;border:none;background:var(--cn-100);border-radius:6px;font-size:18px;cursor:pointer;color:var(--cn-600)}
.msg-detail-content{font-size:14px;color:var(--cn-700);line-height:1.8}
.msg-detail-content :deep(img){max-width:100%;border-radius:8px;margin:12px 0}
.msg-detail-content :deep(h3){font-size:16px;margin:12px 0 6px}
.msg-detail-content :deep(ul){padding-left:20px;margin:8px 0}
.msg-detail-content :deep(p){margin:8px 0}
.tag{padding:2px 8px;border-radius:4px;font-size:11px;font-weight:600;background:var(--cn-100);color:var(--cn-500)}
.btn-page{padding:6px 14px;border-radius:6px;border:1px solid var(--cn-200);background:#fff;font-size:12px;cursor:pointer}.btn-page:disabled{opacity:.4;cursor:not-allowed}
.flex.jc{display:flex;justify-content:center}
</style>
