<template>
  <div>
    <!-- 页面标题 -->
    <div class="flex aic jbt mb6">
      <div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900)">会员管理</div>
        <div style="font-size:13px;color:var(--cn-500)">设定会员等级、定价与注册开通管理</div>
      </div>
      <div class="flex aic g3">
        <button class="btn btn-g" @click="openLevelModal">等级管理</button>
        <button class="btn btn-p" @click="saveAllChanges" :disabled="saving">{{ saving ? '保存中...' : '保存变更' }}</button>
      </div>
    </div>

    <!-- 会员套餐卡片 -->
    <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:20px;margin-bottom:24px">
      <div class="mem-card" v-for="plan in plans" :key="plan.id" :style="{ borderTop: `3px solid ${plan.color}` }">
        <div class="flex aic jbt" style="margin-bottom:16px">
          <span class="fw6" style="font-size:14px" :style="{color:plan.color}">{{ plan.name }}</span>
          <span class="tag" :class="'tag-'+planTagCls(plan.level)">当前{{ planUserCount(plan.level) }}人</span>
        </div>
        <div style="font-size:20px;font-weight:700;color:var(--cn-900);margin-bottom:4px">{{ plan.name }}</div>
        <div style="margin-bottom:16px">
          <span class="fnm" style="font-size:28px;font-weight:700" :style="{color: plan.price === 0 ? 'var(--cn-500)' : 'var(--cn-900)'}">¥{{ plan.price / 100 }}</span>
          <span style="font-size:11px;color:var(--cn-400);margin-left:4px">/{{ plan.period }}</span>
        </div>
        <ul style="list-style:none;margin-bottom:20px">
          <li v-for="(f, fi) in planFeatures(plan)" :key="fi"
              style="font-size:11px;color:var(--cn-600);padding:4px 0;display:flex;align-items:center;gap:8px"
              :style="{color: f.active ? 'var(--cn-600)' : 'var(--cn-300)'}">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M3 7L6 10L11 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
            {{ f.label }}
          </li>
        </ul>
        <button class="btn" style="width:100%" :class="plan.level === 0 ? 'btn-s' : 'btn-p'" @click="openEditPlan(plan)">编辑套餐</button>
      </div>
    </div>

    <!-- 注册设置 + 升降级规则 -->
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:24px">
      <!-- 注册设置 -->
      <div class="card">
        <div class="card-t" style="margin-bottom:20px">注册设置</div>
        <div style="margin-bottom:16px">
          <div class="flex aic jbt mb3">
            <label style="font-size:13px;font-weight:500;color:var(--cn-700)">注册方式</label>
            <span class="tag tag-g"><span class="sd sd-g"></span>开放注册</span>
          </div>
          <div class="flex aic g3" style="margin-bottom:12px">
            <label class="flex aic g2" style="font-size:13px;color:var(--cn-600);cursor:pointer">
              <input type="checkbox" v-model="settings.registerPhone"> 手机号注册
            </label>
            <label class="flex aic g2" style="font-size:13px;color:var(--cn-600);cursor:pointer">
              <input type="checkbox" v-model="settings.registerWechat"> 微信授权(小程序)
            </label>
            <label class="flex aic g2" style="font-size:13px;color:var(--cn-600);cursor:pointer">
              <input type="checkbox" v-model="settings.registerInvite"> 邀请码注册
            </label>
          </div>
        </div>
        <div style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">默认会员等级</label>
          <select class="ipt" v-model="settings.defaultMemberLevel" style="width:100%">
            <option v-for="p in plans" :key="p.id" :value="p.level">{{ p.name }}</option>
          </select>
          <div style="font-size:11px;color:var(--cn-400);margin-top:4px">新注册用户自动分配的会员等级</div>
        </div>
        <div style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">验证码有效期</label>
          <div class="flex aic g2">
            <input class="ipt" type="number" v-model="settings.smsCodeExpire" style="width:80px">
            <span style="font-size:13px;color:var(--cn-600)">分钟</span>
          </div>
        </div>
        <div>
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">邀请码</label>
          <div class="flex aic g2">
            <div class="tgl" :class="settings.inviteEnabled ? 'on' : ''" @click="settings.inviteEnabled = !settings.inviteEnabled"></div>
            <span style="font-size:13px;color:var(--cn-600)">{{ settings.inviteEnabled ? '已启用' : '未启用' }}</span>
          </div>
          <div style="font-size:11px;color:var(--cn-400);margin-top:4px">开启后需输入邀请码方可注册</div>
        </div>
      </div>

      <!-- 会员升降级规则 -->
      <div class="card">
        <div class="card-t" style="margin-bottom:20px">会员升降级规则</div>
        <div style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">升级策略</label>
          <select class="ipt" v-model="settings.upgradeStrategy" style="width:100%">
            <option value="immediate">即时生效</option>
            <option value="next_cycle">次周期生效</option>
            <option value="manual">手动审核</option>
          </select>
          <div style="font-size:11px;color:var(--cn-400);margin-top:4px">用户付费升级后的生效时间</div>
        </div>
        <div style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">降级策略</label>
          <select class="ipt" v-model="settings.downgradeStrategy" style="width:100%">
            <option value="expire">到期后降级</option>
            <option value="immediate">立即降级</option>
            <option value="grace">宽限期降级</option>
          </select>
          <div style="font-size:11px;color:var(--cn-400);margin-top:4px">会员到期后的处理方式</div>
        </div>
        <div style="margin-bottom:16px">
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">到期提醒</label>
          <div class="flex aic g2">
            <input class="ipt" type="number" v-model="settings.expireReminderDays" style="width:80px">
            <span style="font-size:13px;color:var(--cn-600)">天前通知</span>
          </div>
        </div>
        <div>
          <label style="display:block;font-size:13px;font-weight:500;color:var(--cn-700);margin-bottom:6px">续费优惠</label>
          <div class="flex aic g2">
            <input class="ipt" type="number" v-model="settings.renewalDiscount" style="width:80px">
            <span style="font-size:13px;color:var(--cn-600)">折续费</span>
          </div>
          <div style="font-size:11px;color:var(--cn-400);margin-top:4px">在到期提醒期间续费可享受的折扣</div>
        </div>
      </div>
    </div>

    <!-- 会员订单管理 -->
    <div class="card" style="margin-bottom:24px">
      <div class="card-h">
        <div class="card-t">会员订单管理</div>
        <div class="flex aic g2">
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
        </div>
      </div>
      <table class="tbl">
        <thead>
          <tr>
            <th>订单号</th>
            <th>时间</th>
            <th>订单名称</th>
            <th>用户名称</th>
            <th>支付金额</th>
            <th>状态</th>
            <th>备注</th>
            <th>操作</th>
          </tr>
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
          <tr v-if="orders.length === 0">
            <td colspan="8" style="text-align:center;color:var(--cn-400);padding:40px">暂无订单数据</td>
          </tr>
        </tbody>
      </table>
      <!-- 分页 -->
      <div v-if="orderTotal > orderPageSize" class="flex aic jbt" style="padding:12px 20px;border-top:1px solid var(--cn-100)">
        <span style="font-size:11px;color:var(--cn-400)">第 {{ orderPage }}/{{ orderTotalPages }} 页，共 {{ orderTotal }} 条</span>
        <div class="flex aic g2">
          <button class="btn btn-sm btn-g" :disabled="orderPage <= 1" @click="goOrderPage(orderPage - 1)">上一页</button>
          <button class="btn btn-sm btn-g" :disabled="orderPage >= orderTotalPages" @click="goOrderPage(orderPage + 1)">下一页</button>
        </div>
      </div>
    </div>

    <!-- 等级管理弹窗 -->
    <div v-if="showLevelModal" class="modal-overlay" @click.self="closeLevelModal">
      <div class="modal-card" style="width:600px">
        <div class="flex aic jbt mb4">
          <div class="card-t">等级管理</div>
          <button class="btn btn-sm btn-g" @click="closeLevelModal" style="font-size:16px;width:28px;height:28px">&times;</button>
        </div>
        <div style="max-height:400px;overflow-y:auto;margin-bottom:16px">
          <table class="tbl">
            <thead>
              <tr><th>等级</th><th>名称</th><th>排序</th><th>状态</th><th>操作</th></tr>
            </thead>
            <tbody>
              <tr v-for="p in plans" :key="p.id">
                <td>{{ p.level }}</td>
                <td><input class="ipt" v-model="p.name" style="padding:4px 8px;font-size:12px"></td>
                <td><input class="ipt" v-model.number="p.sortOrder" type="number" style="width:60px;padding:4px 8px;font-size:12px"></td>
                <td>
                  <div class="tgl" :class="p.status === 1 ? 'on' : ''" @click="p.status = p.status === 1 ? 0 : 1"></div>
                </td>
                <td>
                  <button class="btn btn-sm btn-d" @click="deletePlan(p)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="flex aic jbt">
          <button class="btn btn-s" @click="addNewPlan">+ 新增等级</button>
          <div class="flex aic g2">
            <button class="btn btn-g" @click="closeLevelModal">取消</button>
            <button class="btn btn-p" @click="savePlans" :disabled="savingPlans">{{ savingPlans ? '保存中...' : '保存' }}</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑套餐弹窗 -->
    <div v-if="showPlanModal" class="modal-overlay" @click.self="closePlanModal">
      <div class="modal-card" style="width:520px">
        <div class="flex aic jbt mb4">
          <div class="card-t">编辑套餐 - {{ editingPlan.name }}</div>
          <button class="btn btn-sm btn-g" @click="closePlanModal" style="font-size:16px;width:28px;height:28px">&times;</button>
        </div>
        <div style="max-height:500px;overflow-y:auto">
          <div class="flex aic g3 mb3"><span class="hb-l">套餐名称</span><input v-model="editingPlan.name" class="ipt"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">描述</span><input v-model="editingPlan.description" class="ipt"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">价格(分)</span><input v-model.number="editingPlan.price" class="ipt" type="number"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">周期</span>
            <select v-model="editingPlan.period" class="ipt" style="width:160px">
              <option value="永久">永久</option><option value="月">月</option><option value="季">季</option><option value="年">年</option>
            </select>
          </div>
          <div class="flex aic g3 mb3"><span class="hb-l">每日AI创作</span><input v-model.number="editingPlan.dailyAiCreate" class="ipt" type="number" placeholder="-1表示无限"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">最大账号数</span><input v-model.number="editingPlan.maxAccounts" class="ipt" type="number"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">最大Agent数</span><input v-model.number="editingPlan.maxAgents" class="ipt" type="number"></div>
          <div class="flex aic g3 mb3"><span class="hb-l">分析级别</span>
            <select v-model="editingPlan.analysisLevel" class="ipt" style="width:160px">
              <option value="基础">基础</option><option value="高级">高级</option><option value="全功能">全功能</option>
            </select>
          </div>
          <div class="flex aic g3 mb3"><span class="hb-l">批量操作</span>
            <div class="tgl" :class="editingPlan.batchEnabled === 1 ? 'on' : ''" @click="editingPlan.batchEnabled = editingPlan.batchEnabled === 1 ? 0 : 1"></div>
          </div>
          <div class="flex aic g3 mb3"><span class="hb-l">定时发布</span>
            <div class="tgl" :class="editingPlan.scheduleEnabled === 1 ? 'on' : ''" @click="editingPlan.scheduleEnabled = editingPlan.scheduleEnabled === 1 ? 0 : 1"></div>
          </div>
          <div class="flex aic g3 mb3"><span class="hb-l">数据导出</span>
            <div class="tgl" :class="editingPlan.exportEnabled === 1 ? 'on' : ''" @click="editingPlan.exportEnabled = editingPlan.exportEnabled === 1 ? 0 : 1"></div>
          </div>
          <div class="flex aic g3 mb3"><span class="hb-l">自定义Agent</span>
            <div class="tgl" :class="editingPlan.customAgentEnabled === 1 ? 'on' : ''" @click="editingPlan.customAgentEnabled = editingPlan.customAgentEnabled === 1 ? 0 : 1"></div>
          </div>
        </div>
        <div class="flex aic jbt" style="margin-top:20px">
          <div></div>
          <div class="flex aic g2">
            <button class="btn btn-g" @click="closePlanModal">取消</button>
            <button class="btn btn-p" @click="savePlan" :disabled="savingPlan">{{ savingPlan ? '保存中...' : '保存' }}</button>
          </div>
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

// ========== 会员套餐 ==========
const plans = ref([])
const userStats = ref({})

// ========== 注册/升降级设置 ==========
const settings = ref({
  registerPhone: true,
  registerWechat: true,
  registerInvite: false,
  defaultMemberLevel: 0,
  smsCodeExpire: 5,
  inviteEnabled: false,
  upgradeStrategy: 'immediate',
  downgradeStrategy: 'expire',
  expireReminderDays: 7,
  renewalDiscount: 9
})

// ========== 订单 ==========
const orders = ref([])
const orderTotal = ref(0)
const orderPage = ref(1)
const orderPageSize = ref(10)
const orderSearch = ref('')
const orderStatus = ref('')

// ========== 弹窗状态 ==========
const showLevelModal = ref(false)
const showPlanModal = ref(false)
const showOrderModal = ref(false)
const saving = ref(false)
const savingPlans = ref(false)
const savingPlan = ref(false)
const savingOrder = ref(false)
const editingPlan = ref({})
const editingOrder = ref({})

// ========== 计算属性 ==========
const orderTotalPages = computed(() => Math.ceil(orderTotal.value / orderPageSize.value) || 1)

// ========== 格式化 ==========
const fmtTime = (t) => t ? t.replace('T', ' ').substring(0, 16) : '-'
const planTagCls = (lvl) => ({ 0: 'n', 1: 'o', 2: 'p' }[lvl] || 'n')
const orderStatusLabel = (s) => ({ 0: '待支付', 1: '已支付', 2: '已取消' }[s] || '未知')
const orderStatusCls = (s) => ({ 0: 'o', 1: 'g', 2: 'd' }[s] || 'n')

// 根据用户member_level统计人数
function planUserCount(level) {
  return userStats.value[level] || 0
}

// 生成套餐特性列表
function planFeatures(plan) {
  return [
    { label: `每日${plan.dailyAiCreate === -1 ? '无限' : plan.dailyAiCreate + '次'}AI创作`, active: true },
    { label: `绑定${plan.maxAccounts}个账号`, active: true },
    { label: `${plan.analysisLevel}数据分析`, active: true },
    { label: `${plan.maxAgents}个Agent`, active: true },
    { label: '批量操作', active: plan.batchEnabled === 1 },
    { label: '定时发布', active: plan.scheduleEnabled === 1 },
    { label: '数据导出', active: plan.exportEnabled === 1 },
    { label: '自定义Agent', active: plan.customAgentEnabled === 1 }
  ]
}

// ========== 数据加载 ==========
async function fetchPlans() {
  try {
    const res = await api.get('/membership/plans')
    if (res.data.code === 200) {
      plans.value = res.data.data
    }
  } catch (e) { console.error('fetchPlans error', e) }
}

async function fetchUserStats() {
  try {
    const res = await api.get('/users/stats')
    if (res.data.code === 200) {
      // 从用户列表统计各等级人数
      const usersRes = await api.get('/users', { params: { page: 1, pageSize: 10000 } })
      if (usersRes.data.code === 200) {
        const counts = {}
        usersRes.data.data.records.forEach(u => {
          counts[u.memberLevel] = (counts[u.memberLevel] || 0) + 1
        })
        userStats.value = counts
      }
    }
  } catch (e) { console.error('fetchUserStats error', e) }
}

async function fetchOrders() {
  try {
    const params = { page: orderPage.value, pageSize: orderPageSize.value }
    if (orderSearch.value) params.keyword = orderSearch.value
    if (orderStatus.value !== '') params.status = orderStatus.value
    const res = await api.get('/membership/orders', { params })
    if (res.data.code === 200) {
      orders.value = res.data.data.records
      orderTotal.value = res.data.data.total
    }
  } catch (e) { console.error('fetchOrders error', e) }
}

function goOrderPage(p) {
  orderPage.value = p
  fetchOrders()
}

// ========== 等级管理 ==========
function openLevelModal() {
  showLevelModal.value = true
}
function closeLevelModal() {
  showLevelModal.value = false
}
async function savePlans() {
  savingPlans.value = true
  try {
    for (const plan of plans.value) {
      await api.put(`/membership/plans/${plan.id}`, plan)
    }
    closeLevelModal()
    fetchPlans()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally { savingPlans.value = false }
}
function addNewPlan() {
  const maxLevel = Math.max(...plans.value.map(p => p.level), -1)
  plans.value.push({
    id: null,
    name: '新等级',
    description: '',
    level: maxLevel + 1,
    price: 0,
    period: '月',
    dailyAiCreate: 3,
    maxAccounts: 1,
    maxAgents: 2,
    analysisLevel: '基础',
    batchEnabled: 0,
    scheduleEnabled: 0,
    exportEnabled: 0,
    customAgentEnabled: 0,
    sortOrder: plans.value.length + 1,
    status: 1,
    color: 'var(--cn-400)'
  })
}
async function deletePlan(plan) {
  if (!confirm(`确认删除等级「${plan.name}」?`)) return
  if (plan.id) {
    try {
      await api.delete(`/membership/plans/${plan.id}`)
    } catch (e) { alert(e.response?.data?.message || '删除失败'); return }
  }
  plans.value = plans.value.filter(p => p !== plan)
}

// ========== 编辑套餐 ==========
function openEditPlan(plan) {
  editingPlan.value = JSON.parse(JSON.stringify(plan))
  showPlanModal.value = true
}
function closePlanModal() {
  showPlanModal.value = false
  editingPlan.value = {}
}
async function savePlan() {
  savingPlan.value = true
  try {
    await api.put(`/membership/plans/${editingPlan.value.id}`, editingPlan.value)
    closePlanModal()
    fetchPlans()
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally { savingPlan.value = false }
}

// ========== 保存全部变更 ==========
async function saveAllChanges() {
  saving.value = true
  try {
    // 保存所有套餐
    for (const plan of plans.value) {
      await api.put(`/membership/plans/${plan.id}`, plan)
    }
    alert('保存成功')
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally { saving.value = false }
}

// ========== 订单操作 ==========
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
  } catch (e) {
    alert(e.response?.data?.message || '保存失败')
  } finally { savingOrder.value = false }
}
async function deleteOrder(o) {
  if (!confirm(`确认删除订单「${o.orderNo}」?`)) return
  try {
    await api.delete(`/membership/orders/${o.id}`)
    fetchOrders()
  } catch (e) {
    alert(e.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  fetchPlans()
  fetchUserStats()
  fetchOrders()
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
  font-size: 13px; color: var(--cn-600); width: 90px; flex-shrink: 0; text-align: right;
}
.mem-card {
  background: #fff; border-radius: 12px; padding: 20px;
  box-shadow: var(--shs); border: 1px solid var(--cn-100);
}
.tgl {
  width: 36px; height: 20px; background: var(--cn-300);
  border-radius: 10px; position: relative; cursor: pointer; transition: background .2s;
  flex-shrink: 0;
}
.tgl.on { background: var(--cp-500) }
.tgl::after {
  content: ''; width: 16px; height: 16px; background: #fff;
  border-radius: 50%; position: absolute; top: 2px; left: 2px;
  transition: transform .2s; box-shadow: 0 1px 2px rgba(0,0,0,.04);
}
.tgl.on::after { transform: translateX(16px) }
</style>
