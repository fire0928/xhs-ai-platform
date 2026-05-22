<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">用户管理</div><div style="font-size:13px;color:var(--cn-500)">管理所有注册用户</div></div>
      <div class="flex aic g3">
        <button class="btn btn-g" @click="openCreateModal">新增用户</button>
        <button class="btn btn-p" @click="exportUsers">导出用户</button>
      </div>
    </div>

    <div class="am-row">
      <div class="am"><div class="am-lb">总用户</div><div class="am-vl">{{ fmtNum(stats.totalUsers) }}</div><div class="am-ch" style="color:var(--cn-500)">全部注册用户</div></div>
      <div class="am"><div class="am-lb">活跃(7天)</div><div class="am-vl">{{ fmtNum(stats.activeUsers) }}</div><div class="am-ch" style="color:var(--cn-500)">近期活跃</div></div>
      <div class="am"><div class="am-lb">今日新增</div><div class="am-vl">{{ fmtNum(stats.todayNew) }}</div><div class="am-ch" style="color:var(--cs)">实时</div></div>
      <div class="am"><div class="am-lb">已禁用</div><div class="am-vl" style="color:var(--ce)">{{ fmtNum(stats.disabledUsers) }}</div><div class="am-ch" style="color:var(--cn-500)">占比 {{ disabledRatio }}%</div></div>
    </div>

    <div class="tbl-wrap">
      <div class="tbl-bar">
        <div class="tbl-bar-l">
          <div style="display:flex;align-items:center;gap:8px;background:var(--cn-100);border-radius:8px;padding:4px 12px;width:200px">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
            <input v-model="searchKeyword" type="text" placeholder="搜索手机号..." style="border:none;background:none;outline:none;font-size:11px;width:100%" @keyup.enter="doSearch">
          </div>
          <select v-model="filterStatus" class="ipt" style="width:120px;padding:4px 12px" @change="doSearch">
            <option value="">全部状态</option><option value="1">正常</option><option value="0">已禁用</option>
          </select>
        </div>
        <span style="font-size:11px;color:var(--cn-400)">共 {{ total }} 条</span>
      </div>
      <table class="tbl">
        <thead><tr><th style="width:40px"><input type="checkbox"></th><th>用户ID</th><th>手机号</th><th>昵称</th><th>会员</th><th>终端</th><th>注册时间</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="u in users" :key="u.id">
            <td><input type="checkbox"></td>
            <td class="fnm" style="font-size:12px">{{ u.id }}</td>
            <td>{{ u.phone }}</td>
            <td>{{ u.nickname || '-' }}</td>
            <td><span class="tag" :class="'tag-' + memberCls(u.memberLevel)">{{ memberLabel(u.memberLevel) }}</span></td>
            <td><span class="tag" :class="'tag-' + terminalCls(u.registerTerminal)">{{ terminalLabel(u.registerTerminal) }}</span></td>
            <td style="font-size:12px;color:var(--cn-500)">{{ fmtTime(u.registerTime) }}</td>
            <td><span class="tag" :class="'tag-' + statusCls(u.status)"><span class="sd" :class="'sd-' + statusDot(u.status)"></span>{{ u.status === 1 ? '正常' : '已禁用' }}</span></td>
            <td>
              <div class="flex aic g2">
                <button class="btn btn-sm btn-g" @click="openEditModal(u)">编辑</button>
                <button class="btn btn-sm" :class="u.status === 1 ? 'btn-d' : 'btn-s'" @click="toggleStatus(u)">{{ u.status === 1 ? '禁用' : '启用' }}</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <!-- 分页 -->
      <div v-if="total > pageSize" class="flex aic jbt" style="padding:12px 20px;border-top:1px solid var(--cn-100)">
        <span style="font-size:11px;color:var(--cn-400)">第 {{ page }}/{{ totalPages }} 页</span>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" :disabled="page <= 1" @click="goPage(page - 1)">上一页</button>
          <button class="btn btn-sm btn-g" :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card">
        <div class="flex aic jbt mb4"><div class="card-t">{{ isEdit ? '编辑用户' : '新增用户' }}</div><button class="btn btn-sm btn-g" @click="closeModal" style="font-size:16px;width:28px;height:28px">&times;</button></div>
        <div class="flex aic g3 mb4"><span class="hb-l">手机号</span><input v-model="form.phone" class="ipt" placeholder="11位手机号" :disabled="isEdit"></div>
        <div class="flex aic g3 mb4"><span class="hb-l">密码</span><input v-model="form.password" class="ipt" type="password" :placeholder="isEdit ? '留空不修改' : '至少6位'"></div>
        <div class="flex aic g3 mb4"><span class="hb-l">昵称</span><input v-model="form.nickname" class="ipt" placeholder="用户昵称"></div>
        <div class="flex aic g3 mb4"><span class="hb-l">邮箱</span><input v-model="form.email" class="ipt" placeholder="邮箱地址"></div>
        <div class="flex aic g3 mb4"><span class="hb-l">会员等级</span>
          <select v-model="form.memberLevel" class="ipt" style="width:160px"><option :value="0">免费版</option><option :value="1">标准版</option><option :value="2">专业版</option></select>
        </div>
        <div class="flex aic g3 mb4"><span class="hb-l">注册终端</span>
          <select v-model="form.registerTerminal" class="ipt" style="width:160px"><option value="computer">电脑端</option><option value="harmony">鸿蒙端</option><option value="mini_program">小程序</option></select>
        </div>
        <div class="flex aic jbt" style="margin-top:20px">
          <div></div>
          <div class="flex aic g2">
            <button class="btn btn-g" @click="closeModal">取消</button>
            <button class="btn btn-p" @click="submitForm" :disabled="submitting">{{ submitting ? '提交中...' : '确认' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api'

// 列表
const users = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterStatus = ref('')
const loading = ref(false)

// 统计
const stats = ref({ totalUsers: 0, activeUsers: 0, todayNew: 0, disabledUsers: 0 })

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const disabledRatio = computed(() => {
  if (!stats.value.totalUsers) return '0.0'
  return ((stats.value.disabledUsers / stats.value.totalUsers) * 100).toFixed(1)
})

// 弹窗
const showModal = ref(false)
const isEdit = ref(false)
const editUserId = ref(null)
const submitting = ref(false)
const form = ref({ phone: '', password: '', nickname: '', email: '', memberLevel: 0, registerTerminal: 'computer' })

// 格式化
const fmtNum = (n) => (n || 0).toLocaleString()
const fmtTime = (t) => t ? t.replace('T', ' ').substring(0, 16) : '-'

const memberLabel = (lvl) => ({ 0: '免费版', 1: '标准版', 2: '专业版' }[lvl] || '免费版')
const memberCls = (lvl) => ({ 0: 'n', 1: 'o', 2: 'p' }[lvl] || 'n')
const terminalLabel = (t) => ({ computer: '电脑端', harmony: '鸿蒙端', mini_program: '小程序' }[t] || t || '-')
const terminalCls = (t) => ({ computer: 'b', harmony: 'r', mini_program: 'g' }[t] || 'n')
const statusCls = (s) => s === 1 ? 'g' : 'd'
const statusDot = (s) => s === 1 ? 'g' : 'r'

// 数据加载
async function fetchUsers() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filterStatus.value !== '') params.status = filterStatus.value
    const res = await api.get('/users', { params })
    if (res.data.code === 200) {
      users.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch { } finally { loading.value = false }
}

async function fetchStats() {
  try {
    const res = await api.get('/users/stats')
    if (res.data.code === 200) stats.value = res.data.data
  } catch { }
}

function doSearch() {
  page.value = 1
  fetchUsers()
}

function goPage(p) {
  page.value = p
  fetchUsers()
}

// 新增 / 编辑
function openCreateModal() {
  isEdit.value = false
  editUserId.value = null
  form.value = { phone: '', password: '', nickname: '', email: '', memberLevel: 0, registerTerminal: 'computer' }
  showModal.value = true
}

function openEditModal(u) {
  isEdit.value = true
  editUserId.value = u.id
  form.value = { phone: u.phone, password: '', nickname: u.nickname || '', email: u.email || '', memberLevel: u.memberLevel, registerTerminal: u.registerTerminal }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  isEdit.value = false
}

async function submitForm() {
  if (!isEdit.value) {
    if (!form.value.phone || !/^1\d{10}$/.test(form.value.phone)) { alert('请输入正确的11位手机号'); return }
    if (!form.value.password || form.value.password.length < 6) { alert('密码至少6位'); return }
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await api.put(`/users/${editUserId.value}`, {
        nickname: form.value.nickname,
        email: form.value.email,
        memberLevel: form.value.memberLevel,
        registerTerminal: form.value.registerTerminal
      })
      if (form.value.password && form.value.password.length >= 6) {
        await api.put(`/users/${editUserId.value}/reset-password?newPassword=${encodeURIComponent(form.value.password)}`)
      }
    } else {
      await api.post('/users', form.value)
    }
    closeModal()
    fetchUsers()
    fetchStats()
  } catch (e) {
    alert(e.response?.data?.message || '操作失败')
  } finally { submitting.value = false }
}

// 禁用/启用
async function toggleStatus(u) {
  const newStatus = u.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  if (!confirm(`确认${action}该用户?`)) return
  try {
    await api.put(`/users/${u.id}/toggle-status?status=${newStatus}`)
    fetchUsers()
    fetchStats()
  } catch (e) {
    alert(e.response?.data?.message || '操作失败')
  }
}

// 导出 CSV
async function exportUsers() {
  try {
    const params = { page: 1, pageSize: 10000 }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filterStatus.value !== '') params.status = filterStatus.value
    const res = await api.get('/users', { params })
    if (res.data.code !== 200) return

    const records = res.data.data.records
    const BOM = '\uFEFF'
    const header = '用户ID,手机号,昵称,邮箱,会员等级,注册终端,注册时间,状态\n'
    const rows = records.map(u => [
      u.id, u.phone, `"${u.nickname || ''}"`, u.email || '',
      memberLabel(u.memberLevel), terminalLabel(u.registerTerminal),
      fmtTime(u.registerTime), u.status === 1 ? '正常' : '已禁用'
    ].join(',')).join('\n')

    const blob = new Blob([BOM + header + rows], { type: 'text/csv;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a'); a.href = url; a.download = '用户列表.csv'
    document.body.appendChild(a); a.click()
    document.body.removeChild(a); URL.revokeObjectURL(url)
  } catch { }
}

onMounted(() => {
  fetchStats()
  fetchUsers()
})
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,.4); z-index: 1000;
  display: flex; align-items: center; justify-content: center;
}
.modal-card {
  background: #fff; border-radius: 16px; padding: 24px 28px;
  width: 460px; max-width: 90vw; box-shadow: var(--shl);
}
.hb-l {
  font-size: 13px; color: var(--cn-600); width: 70px; flex-shrink: 0; text-align: right;
}
</style>
