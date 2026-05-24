<template>
  <div>
    <div class="header">
      <div>
        <h2 class="h-xl">订单管理</h2>
        <p class="sub">查看会员升级订单记录</p>
      </div>
    </div>

    <div class="orders-table" v-if="orders.length > 0">
      <div class="table-header">
        <span>订单号</span>
        <span>套餐</span>
        <span>金额</span>
        <span>支付状态</span>
        <span>创建时间</span>
      </div>
      <div v-for="o in orders" :key="o.id" class="table-row">
        <span class="cell-mono">{{ o.orderNo }}</span>
        <span>{{ o.planName }}</span>
        <span style="font-weight:600">¥{{ (o.amount / 100).toFixed(0) }}</span>
        <span :class="['status-tag', statusClass(o.status)]">{{ statusText(o.status) }}</span>
        <span class="cell-time">{{ o.createTime ? o.createTime.substring(0, 10) : '-' }}</span>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
        <rect x="12" y="12" width="40" height="40" rx="6" stroke="var(--cn-300)" stroke-width="2" fill="var(--cn-50)"/>
        <path d="M20 28h24M20 36h16" stroke="var(--cn-300)" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <h3>暂无订单</h3>
      <p>前往会员中心升级套餐</p>
      <button class="btn btn-primary" @click="$router.push('/membership')">去升级</button>
    </div>

    <div v-if="loading" style="text-align:center;padding:40px;color:var(--cn-400)">加载中...</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'

const orders = ref([])
const loading = ref(false)

const statusText = { 0: '待支付', 1: '已支付', 2: '已取消' }
const statusClassMap = { 0: 'pending', 1: 'paid', 2: 'cancelled' }

function statusClass(s) { return statusClassMap[s] || 'pending' }

async function loadOrders() {
  loading.value = true
  try {
    const res = await api.get('/member/my-orders', { params: { pageSize: 50 } })
    if (res.data?.code === 200) {
      orders.value = res.data?.data?.records || []
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(loadOrders)
</script>

<style scoped>
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
.orders-table { background: #fff; border-radius: 12px; border: 1px solid var(--cn-100); overflow: hidden; }
.table-header { display: grid; grid-template-columns: 2fr 1.5fr 1fr 1fr 1.2fr; gap: 12px; padding: 12px 20px; background: var(--cn-50); font-size: 12px; font-weight: 600; color: var(--cn-500); border-bottom: 1px solid var(--cn-100); }
.table-row { display: grid; grid-template-columns: 2fr 1.5fr 1fr 1fr 1.2fr; gap: 12px; padding: 14px 20px; font-size: 13px; color: var(--cn-700); border-bottom: 1px solid var(--cn-50); align-items: center; }
.table-row:last-child { border-bottom: none; }
.cell-mono { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: var(--cn-500); }
.cell-time { color: var(--cn-400); font-size: 12px; }
.status-tag { font-size: 11px; padding: 2px 10px; border-radius: 9999px; font-weight: 500; display: inline-block; }
.status-tag.pending { background: #FFF3E0; color: #E65100; }
.status-tag.paid { background: #E8F5E9; color: #2E7D32; }
.status-tag.cancelled { background: var(--cn-100); color: var(--cn-400); }
@media (max-width: 768px) {
  .table-header, .table-row { grid-template-columns: 1fr 1fr 1fr; }
  .table-header span:nth-child(1), .table-row span:nth-child(1),
  .table-header span:nth-child(5), .table-row span:nth-child(5) { display: none; }
}
</style>
