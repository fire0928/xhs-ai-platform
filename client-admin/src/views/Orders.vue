<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">订单管理</div><div style="font-size:13px;color:var(--cn-500)">全量会员订单查看与管理</div></div>
      <div class="flex aic g3">
        <div style="display:flex;align-items:center;gap:8px;background:var(--cn-100);border-radius:8px;padding:4px 12px;width:200px">
          <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
          <input v-model="orderSearch" type="text" placeholder="搜索订单号/用户/套餐..." style="border:none;background:none;outline:none;font-size:11px;width:100%" @keyup.enter="fetchOrders">
        </div>
        <select v-model="orderStatus" class="ipt" style="width:120px;padding:4px 12px" @change="fetchOrders">
          <option value="">全部状态</option>
          <option value="0">待支付</option>
          <option value="1">已支付</option>
          <option value="2">已取消</option>
        </select>
        <button class="btn btn-p" @click="fetchOrders">搜索</button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="am-row" style="margin-bottom:20px">
      <div class="am"><div class="am-lb">总订单数</div><div class="am-vl">{{ stats.total }}</div></div>
      <div class="am"><div class="am-lb">待支付</div><div class="am-vl" style="color:var(--ca-orange)">{{ stats.pending }}</div></div>
      <div class="am"><div class="am-lb">已支付</div><div class="am-vl" style="color:var(--cs)">{{ stats.paid }}</div></div>
      <div class="am"><div class="am-lb">总金额</div><div class="am-vl" style="color:var(--cp-500)">¥{{ (stats.totalAmount / 100).toFixed(2) }}</div></div>
    </div>

    <div class="tbl-wrap">
      <table class="tbl">
        <thead>
          <tr><th>订单号</th><th>时间</th><th>套餐</th><th>用户</th><th>金额</th><th>状态</th><th>备注</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td class="fnm" style="font-size:12px">{{ o.orderNo }}</td>
            <td style="font-size:12px;color:var(--cn-500)">{{ fmtTime(o.createTime) }}</td>
            <td>{{ o.planName }}</td>
            <td>{{ o.userName || '用户' + o.userId }}</td>
            <td class="fnm" style="font-weight:600">¥{{ (o.amount / 100).toFixed(2) }}</td>
            <td><span class="tag" :class="'tag-' + orderStatusCls(o.status)">{{ orderStatusLabel(o.status) }}</span></td>
            <td style="font-size:12px;color:var(--cn-500)">{{ o.remark || '-' }}</td>
            <td>
              <div class="flex aic g2">
                <button class="btn btn-sm btn-g" @click="editOrder(o)">编辑</button>
                <button class="btn btn-sm btn-d" @click="deleteOrder(o)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="orders.length === 0"><td colspan="8" style="text-align:center;color:var(--cn-400);padding:40px">暂无订单数据</td></tr>
        </tbody>
      </table>
      <div v-if="orderTotal > 0" class="flex aic jbt" style="margin-top:16px">
        <span style="font-size:12px;color:var(--cn-400)">第 {{ orderPage }}/{{ orderTotalPages }} 页，共 {{ orderTotal }} 条</span>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" :disabled="orderPage <= 1" @click="goOrderPage(orderPage - 1)">上一页</button>
          <button class="btn btn-sm btn-g" :disabled="orderPage >= orderTotalPages" @click="goOrderPage(orderPage + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 编辑订单弹窗 -->
    <div v-if="showOrderModal" class="modal-overlay" @click.self="closeOrderModal">
      <div class="modal-card" style="width:460px">
        <div class="flex aic jbt mb4">
          <div class="card-t">编辑订单</div>
          <button class="btn btn-sm btn-g" @click="closeOrderModal" style="font-size:16px;width:28px;height:28px">&times;</button>
        </div>
        <div class="flex aic g3 mb3"><span class="hb-l">订单号</span><input v-model="editingOrder.orderNo" class="ipt" disabled></div>
        <div class="flex aic g3 mb3"><span class="hb-l">用户名</span><input v-model="editingOrder.userName" class="ipt"></div>
        <div class="flex aic g3 mb3"><span class="hb-l">套餐</span><input v-model="editingOrder.planName" class="ipt"></div>
        <div class="flex aic g3 mb3"><span class="hb-l">金额(分)</span><input v-model.number="editingOrder.amount" class="ipt" type="number"></div>
        <div class="flex aic g3 mb3"><span class="hb-l">状态</span>
          <select v-model="editingOrder.status" class="ipt" style="width:160px">
            <option :value="0">待支付</option><option :value="1">已支付</option><option :value="2">已取消</option>
          </select>
        </div>
        <div class="flex aic g3 mb3"><span class="hb-l">支付方式</span><input v-model="editingOrder.payType" class="ipt" placeholder="微信/支付宝"></div>
        <div class="flex aic g3 mb3"><span class="hb-l">备注</span><input v-model="editingOrder.remark" class="ipt"></div>
        <div class="flex aic jbt" style="margin-top:20px">
          <div></div>
          <div class="flex aic g2">
            <button class="btn btn-g" @click="closeOrderModal">取消</button>
            <button class="btn btn-p" @click="saveOrder" :disabled="savingOrder">{{ savingOrder ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/api'

const orders = ref([])
const orderTotal = ref(0)
const orderPage = ref(1)
const orderPageSize = ref(10)
const orderSearch = ref('')
const orderStatus = ref('')
const showOrderModal = ref(false)
const savingOrder = ref(false)
const editingOrder = ref({})
const stats = ref({ total: 0, pending: 0, paid: 0, totalAmount: 0 })

const orderTotalPages = computed(() => Math.ceil(orderTotal.value / orderPageSize.value) || 1)

const fmtTime = (t) => t ? t.replace('T', ' ').substring(0, 16) : '-'
const orderStatusLabel = (s) => ({ 0: '待支付', 1: '已支付', 2: '已取消' }[s] || '未知')
const orderStatusCls = (s) => ({ 0: 'o', 1: 'g', 2: 'd' }[s] || 'n')

async function fetchOrders() {
  try {
    const params = { page: orderPage.value, pageSize: orderPageSize.value }
    if (orderSearch.value) params.keyword = orderSearch.value
    if (orderStatus.value !== '') params.status = orderStatus.value
    const res = await api.get('/membership/orders', { params })
    if (res.data.code === 200) {
      orders.value = res.data.data.records || []
      orderTotal.value = res.data.data.total || 0
    }
  } catch (e) { console.error('fetchOrders error', e) }
}

async function loadStats() {
  try {
    const res = await api.get('/membership/orders', { params: { page: 1, pageSize: 10000 } })
    if (res.data.code === 200) {
      const all = res.data.data.records || []
      stats.value = {
        total: all.length,
        pending: all.filter(o => o.status === 0).length,
        paid: all.filter(o => o.status === 1).length,
        totalAmount: all.filter(o => o.status === 1).reduce((sum, o) => sum + (o.amount || 0), 0)
      }
    }
  } catch (e) {}
}

function goOrderPage(p) {
  orderPage.value = p
  fetchOrders()
}

function editOrder(o) {
  editingOrder.value = JSON.parse(JSON.stringify(o))
  showOrderModal.value = true
}
function closeOrderModal() {
  showOrderModal.value = false
  editingOrder.value = {}
}
async function saveOrder() {
  savingOrder.value = true
  try {
    await api.put(`/membership/orders/${editingOrder.value.id}`, editingOrder.value)
    closeOrderModal()
    fetchOrders()
    loadStats()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally { savingOrder.value = false }
}
async function deleteOrder(o) {
  if (!confirm(`确认删除订单「${o.orderNo}」?`)) return
  try {
    await api.delete(`/membership/orders/${o.id}`)
    fetchOrders()
    loadStats()
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

onMounted(() => { fetchOrders(); loadStats() })
</script>

<style scoped>
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.4); z-index: 1000; display: flex; align-items: center; justify-content: center; }
.modal-card { background: #fff; border-radius: 16px; padding: 24px 28px; width: 460px; max-width: 90vw; box-shadow: var(--shl); }
.hb-l { font-size: 13px; color: var(--cn-600); width: 90px; flex-shrink: 0; text-align: right; }
</style>
