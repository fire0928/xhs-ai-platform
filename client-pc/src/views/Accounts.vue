<template>
  <div class="accounts-page">
    <div class="page-header">
      <div>
        <h2>账号管理</h2>
        <p class="sub">绑定和管理小红书账号，查看账号状态</p>
      </div>
      <button class="btn btn-primary" @click="showAddModal = true">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2v12M2 8h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        添加账号
      </button>
    </div>

    <!-- 绑定统计 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-label">已绑定</div>
        <div class="stat-value">{{ accounts.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">正常在线</div>
        <div class="stat-value" style="color:var(--cs)">{{ onlineCount }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">会话过期</div>
        <div class="stat-value" style="color:var(--cw)">{{ expiredCount }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">已失效</div>
        <div class="stat-value" style="color:var(--ce)">{{ failedCount }}</div>
      </div>
    </div>

    <!-- 账号列表 -->
    <div class="accounts-list" v-if="accounts.length > 0">
      <div class="account-card" v-for="acc in accounts" :key="acc.id">
        <div class="acc-header">
          <div class="acc-avatar" :style="{ background: avatarColor(acc.id) }">
            {{ acc.nickname?.charAt(0) || '号' }}
          </div>
          <div class="acc-info">
            <div class="acc-name">{{ acc.nickname || '未命名账号' }}</div>
            <div class="acc-id">ID: {{ acc.xiaohongshuId || '---' }}</div>
          </div>
          <div class="acc-status">
            <span class="status-dot" :class="statusClass(acc.sessionStatus)"></span>
            {{ statusText(acc.sessionStatus) }}
          </div>
        </div>
        <div class="acc-body">
          <div class="acc-detail">
            <span class="detail-label">Cookie 状态</span>
            <span class="detail-value" :class="statusClass(acc.sessionStatus)">
              {{ cookieStatusText(acc.sessionStatus) }}
            </span>
          </div>
          <div class="acc-detail">
            <span class="detail-label">最后验证</span>
            <span class="detail-value text-muted">{{ acc.lastVerifyTime || '---' }}</span>
          </div>
          <div class="acc-detail">
            <span class="detail-label">绑定时间</span>
            <span class="detail-value text-muted">{{ acc.createTime || '---' }}</span>
          </div>
          <div class="acc-detail" v-if="acc.remark">
            <span class="detail-label">备注</span>
            <span class="detail-value text-muted">{{ acc.remark }}</span>
          </div>
        </div>
        <div class="acc-footer">
          <button class="btn btn-ghost btn-sm" @click="verifyAccount(acc.id)">验证Cookie</button>
          <button class="btn btn-ghost btn-sm" @click="editAccount(acc)">编辑</button>
          <button class="btn btn-ghost btn-sm danger" @click="deleteAccount(acc.id)">解绑</button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else>
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
        <rect x="12" y="8" width="40" height="48" rx="6" stroke="var(--cn-300)" stroke-width="2" fill="var(--cn-50)"/>
        <circle cx="32" cy="28" r="8" stroke="var(--cn-300)" stroke-width="2"/>
        <path d="M20 48c0-6.6 5.4-12 12-12s12 5.4 12 12" stroke="var(--cn-300)" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <h3>尚未绑定小红书账号</h3>
      <p>添加小红书账号后，即可使用自动发布功能</p>
      <button class="btn btn-primary" @click="showAddModal = true">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2v12M2 8h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        添加第一个账号
      </button>
    </div>

    <!-- 添加账号弹窗 -->
    <div class="modal-overlay" v-if="showAddModal" @click.self="showAddModal = false">
      <div class="modal">
        <div class="modal-h">
          <h3>{{ editingAccount ? '编辑账号' : '添加小红书账号' }}</h3>
          <button class="btn-icon" @click="closeModal">&times;</button>
        </div>
        <div class="modal-b">
          <div class="form-group">
            <label>账号昵称 <span class="req">*</span></label>
            <input class="ipt" v-model="form.nickname" placeholder="为账号取个名字，便于识别" />
          </div>
          <div class="form-group">
            <label>Cookie 字符串 <span class="req">*</span></label>
            <textarea class="ipt" v-model="form.cookie" rows="6" placeholder="粘贴从小红书网页版获取的完整 Cookie 字符串" style="font-family:var(--fm);font-size:11px"></textarea>
            <div class="hint">
              <svg width="12" height="12" viewBox="0 0 12 12" fill="none"><path d="M6 1A5 5 0 111 6a5 5 0 015-5z" stroke="var(--cp-400)" stroke-width="1.2"/><path d="M6 4v2.5M6 8.5h.005" stroke="var(--cp-400)" stroke-width="1.2" stroke-linecap="round"/></svg>
              Cookie 将使用 AES-256-GCM 加密存储，确保安全
            </div>
          </div>
          <div class="form-group">
            <label>备注</label>
            <input class="ipt" v-model="form.remark" placeholder="可选备注信息" />
          </div>
          <div class="form-group">
            <label>默认发布账号</label>
            <label class="toggle-row">
              <input type="checkbox" v-model="form.isDefault" />
              <span>设为默认发布账号</span>
            </label>
          </div>
        </div>
        <div class="modal-f">
          <button class="btn btn-ghost" @click="closeModal">取消</button>
          <button class="btn btn-primary" @click="submitAccount" :disabled="!form.nickname || !form.cookie">
            确认添加
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api'

const accounts = ref([])
const showAddModal = ref(false)
const editingAccount = ref(null)
const form = ref({ nickname: '', cookie: '', remark: '', isDefault: false })

const onlineCount = computed(() => accounts.value.filter(a => a.sessionStatus === 'valid').length)
const expiredCount = computed(() => accounts.value.filter(a => a.sessionStatus === 'expired').length)
const failedCount = computed(() => accounts.value.filter(a => a.sessionStatus === 'failed').length)

const colors = ['linear-gradient(135deg,#FE2C55,#FF6480)', 'linear-gradient(135deg,#8B5CF6,#A78BFA)', 'linear-gradient(135deg,#F59E0B,#FBBF24)', 'linear-gradient(135deg,#3B82F6,#60A5FA)']
function avatarColor(id) { return colors[id % colors.length] }

function statusClass(s) {
  if (s === 'valid') return 'online'
  if (s === 'expired') return 'warning'
  return 'error'
}
function statusText(s) {
  if (s === 'valid') return '在线'
  if (s === 'expired') return '过期'
  return '失效'
}
function cookieStatusText(s) {
  if (s === 'valid') return '有效'
  if (s === 'expired') return '已过期，需重新登录'
  return '已失效'
}

async function fetchAccounts() {
  try {
    const res = await api.get('/account/list')
    if (res.data.code === 200) accounts.value = res.data.data || []
  } catch { /* ignore */ }
}

function editAccount(acc) {
  editingAccount.value = acc
  form.value = { nickname: acc.nickname, cookie: '', remark: acc.remark || '', isDefault: acc.isDefault || false }
  showAddModal.value = true
}

function closeModal() {
  showAddModal.value = false
  editingAccount.value = null
  form.value = { nickname: '', cookie: '', remark: '', isDefault: false }
}

async function submitAccount() {
  try {
    const url = editingAccount.value ? `/account/${editingAccount.value.id}` : '/account/bind'
    const method = editingAccount.value ? 'put' : 'post'
    await api[method](url, form.value)
    closeModal()
    fetchAccounts()
  } catch { /* ignore */ }
}

async function verifyAccount(id) {
  try {
    await api.post(`/account/${id}/verify`)
    fetchAccounts()
  } catch { /* ignore */ }
}

async function deleteAccount(id) {
  if (!confirm('确定要解绑此账号吗？解绑后该账号的定时发布任务将被取消。')) return
  try {
    await api.delete(`/account/${id}`)
    fetchAccounts()
  } catch { /* ignore */ }
}

onMounted(fetchAccounts)
</script>

<style scoped>
.accounts-page { max-width: 1000px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-header h2 { font-size: 20px; font-weight: 700; color: var(--cn-900); }
.page-header .sub { font-size: 13px; color: var(--cn-500); margin-top: 4px; }
.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: 12px; padding: 16px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.stat-label { font-size: 12px; color: var(--cn-500); margin-bottom: 4px; }
.stat-value { font-family: 'JetBrains Mono', monospace; font-size: 28px; font-weight: 700; color: var(--cn-900); }
.accounts-list { display: flex; flex-direction: column; gap: 12px; }
.account-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.acc-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.acc-avatar { width: 44px; height: 44px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: 700; flex-shrink: 0; }
.acc-info { flex: 1; }
.acc-name { font-size: 15px; font-weight: 600; color: var(--cn-800); }
.acc-id { font-size: 12px; color: var(--cn-400); margin-top: 2px; font-family: 'JetBrains Mono', monospace; }
.acc-status { display: flex; align-items: center; gap: 6px; font-size: 12px; font-weight: 600; padding: 4px 12px; border-radius: 20px; }
.acc-status:has(.online) { background: #DCFCE7; color: #15803D; }
.acc-status:has(.warning) { background: var(--ca-orange-l); color: #B45309; }
.acc-status:has(.error) { background: #FEE2E2; color: #B91C1C; }
.status-dot { width: 6px; height: 6px; border-radius: 50%; }
.status-dot.online { background: var(--cs); }
.status-dot.warning { background: var(--cw); }
.status-dot.error { background: var(--ce); }
.acc-body { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 16px; padding: 12px; background: var(--cn-50); border-radius: 8px; }
.acc-detail { display: flex; justify-content: space-between; font-size: 13px; }
.detail-label { color: var(--cn-500); }
.detail-value { font-weight: 500; color: var(--cn-700); }
.detail-value.online { color: var(--cs); }
.detail-value.warning { color: var(--cw); }
.detail-value.error { color: var(--ce); }
.text-muted { color: var(--cn-400); }
.acc-footer { display: flex; gap: 8px; }
.acc-footer .danger { color: var(--ce); }
.acc-footer .danger:hover { background: #FEE2E2; }
.empty-state { text-align: center; padding: 64px 32px; }
.empty-state svg { margin-bottom: 16px; opacity: .4; }
.empty-state h3 { font-size: 16px; font-weight: 600; color: var(--cn-700); margin-bottom: 8px; }
.empty-state p { font-size: 13px; color: var(--cn-500); margin-bottom: 24px; }

.btn { display: inline-flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: all .15s; white-space: nowrap; }
.btn-primary { background: var(--gb); color: #fff; box-shadow: 0 2px 8px rgba(254,44,85,.25); }
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(254,44,85,.35); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; }
.btn-ghost { background: transparent; color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-100); }
.btn-sm { padding: 4px 10px; font-size: 12px; }
.btn-icon { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border-radius: 8px; font-size: 18px; color: var(--cn-400); cursor: pointer; }
.btn-icon:hover { background: var(--cn-100); color: var(--cn-600); }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); z-index: 200; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; border-radius: 16px; width: 520px; max-height: 80vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,.15); }
.modal-h { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px; border-bottom: 1px solid var(--cn-100); }
.modal-h h3 { font-size: 16px; font-weight: 600; color: var(--cn-900); }
.modal-b { padding: 24px; }
.modal-f { display: flex; justify-content: flex-end; gap: 12px; padding: 16px 24px; border-top: 1px solid var(--cn-100); }

.form-group { margin-bottom: 16px; }
.form-group label { display: block; font-size: 13px; font-weight: 600; color: var(--cn-700); margin-bottom: 6px; }
.form-group .req { color: var(--cp-500); }
.ipt { width: 100%; padding: 10px 12px; border: 1px solid var(--cn-300); border-radius: 8px; font-size: 13px; color: var(--cn-800); background: #fff; outline: none; }
.ipt:focus { border-color: var(--cp-400); box-shadow: 0 0 0 3px rgba(254,44,85,.1); }
textarea.ipt { resize: vertical; }
.hint { display: flex; align-items: center; gap: 6px; font-size: 11px; color: var(--cn-400); margin-top: 6px; }
.toggle-row { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--cn-600); cursor: pointer; }
.toggle-row input { accent-color: var(--cp-500); }
</style>
