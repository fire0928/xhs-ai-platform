<template>
  <div class="membership-page">
    <div class="page-header">
      <h1>会员中心</h1>
      <p>选择适合你的会员方案，解锁更多创作能力</p>
    </div>

    <!-- 当前会员状态 -->
    <div class="current-plan-card" v-if="currentUser">
      <div class="flex aic jbt">
        <div>
          <span class="plan-badge" :style="{background: currentPlan?.color||'var(--cn-400)'}">{{ currentPlan?.name || '免费版' }}</span>
          <span style="font-size:13px;color:var(--cn-500);margin-left:12px">
            到期时间: {{ currentUser.memberExpireTime ? currentUser.memberExpireTime.substring(0,10) : '永久' }}
          </span>
        </div>
        <div class="flex aic g3">
          <span style="font-size:13px;color:var(--cn-500)">今日AI创作: {{ todayCount || 0 }}/{{ currentPlan?.dailyAiCreate===-1?'∞':(currentPlan?.dailyAiCreate||3) }}</span>
        </div>
      </div>
    </div>

    <!-- 套餐选择 -->
    <h2 style="font-size:18px;font-weight:700;color:var(--cn-900);margin-bottom:16px;margin-top:32px">选择套餐</h2>
    <div class="plans-grid">
      <div v-for="plan in allPlans" :key="plan.id" class="plan-card" :class="{active:currentUser?.memberLevel===plan.level,recommended:plan.level===1}" :style="{borderColor: currentUser?.memberLevel===plan.level ? plan.color : ''}">
        <div v-if="currentUser?.memberLevel===plan.level" class="current-badge" :style="{background: plan.color}">已开通</div>
        <div v-else-if="plan.level===1" class="rec-badge">推荐</div>
        <div class="plan-name" :style="{color:plan.color}">{{ plan.name }}</div>
        <div class="plan-price">
          <span class="price-num">{{ plan.price > 0 ? '¥'+(plan.price/100).toFixed(0) : '免费' }}</span>
          <span class="price-period" v-if="plan.price>0">/{{ plan.period }}</span>
        </div>
        <div class="plan-desc">{{ plan.description }}</div>
        <ul class="plan-features">
          <li><span class="feat-label">每日AI创作</span><span class="feat-val">{{ plan.dailyAiCreate===-1?'无限次':plan.dailyAiCreate+'次' }}</span></li>
          <li><span class="feat-label">绑定账号</span><span class="feat-val">{{ plan.maxAccounts }}个</span></li>
          <li><span class="feat-label">可用Agent</span><span class="feat-val">{{ plan.maxAgents }}个</span></li>
          <li><span class="feat-label">分析级别</span><span class="feat-val">{{ plan.analysisLevel }}</span></li>
          <li :class="{disabled: !plan.batchEnabled}"><span class="feat-label">批量操作</span><span class="feat-val check" v-if="plan.batchEnabled">✓</span><span v-else class="feat-val x">✕</span></li>
          <li :class="{disabled: !plan.scheduleEnabled}"><span class="feat-label">定时发布</span><span class="feat-val check" v-if="plan.scheduleEnabled">✓</span><span v-else class="feat-val x">✕</span></li>
          <li :class="{disabled: !plan.exportEnabled}"><span class="feat-label">数据导出</span><span class="feat-val check" v-if="plan.exportEnabled">✓</span><span v-else class="feat-val x">✕</span></li>
          <li :class="{disabled: !plan.customAgentEnabled}"><span class="feat-label">自定义Agent</span><span class="feat-val check" v-if="plan.customAgentEnabled">✓</span><span v-else class="feat-val x">✕</span></li>
        </ul>
        <div class="plan-action">
          <button v-if="currentUser?.memberLevel===plan.level" class="btn-current" disabled>当前套餐</button>
          <button v-else-if="plan.price===0" class="btn-free" disabled>免费套餐</button>
          <button v-else class="btn-upgrade" @click="handleUpgrade(plan)">立即升级  ¥{{ (plan.price/100).toFixed(0) }}</button>
        </div>
      </div>
    </div>

    <!-- 支付弹窗 -->
    <div v-if="showPay" class="modal-overlay" @click.self="showPay=false">
      <div class="modal-card">
        <div class="flex aic jbt mb4">
          <div class="modal-title">确认支付</div>
          <button class="btn-close" @click="showPay=false">&times;</button>
        </div>
        <div class="pay-info">
          <div class="flex aic jbt"><span>套餐</span><span style="font-weight:600">{{ payPlan?.name }}</span></div>
          <div class="flex aic jbt"><span>金额</span><span style="font-weight:600;color:var(--ce);font-size:20px">¥{{ payPlan ? (payPlan.price/100).toFixed(0) : 0 }}</span></div>
          <div class="flex aic jbt"><span>周期</span><span>{{ payPlan?.period }}</span></div>
        </div>
        <div style="margin-top:12px">
          <label style="font-size:13px;font-weight:500;color:var(--cn-700);display:block;margin-bottom:6px">支付方式</label>
          <div class="pay-methods">
            <label v-if="payMethods.wechat" class="pay-method" :class="{selected:payType==='wechat'}" @click="payType='wechat'">
              <svg width="28" height="28" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="12" fill="#07C160"/><path d="M8 12c0-3 3-5 6-5s6 2 6 5-3 5-6 5c-.5 0-1-.1-1.5-.2l-2.5 1.5.5-2C9 15.5 8 13.8 8 12z" fill="white"/></svg>
              <span style="font-size:12px;margin-top:4px">微信支付</span>
            </label>
            <label v-if="payMethods.alipay" class="pay-method" :class="{selected:payType==='alipay'}" @click="payType='alipay'">
              <svg width="28" height="28" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="12" fill="#1677FF"/><text x="14" y="19" text-anchor="middle" fill="white" font-size="14" font-weight="bold">支</text></svg>
              <span style="font-size:12px;margin-top:4px">支付宝</span>
            </label>
          </div>
          <div v-if="!payMethods.wechat && !payMethods.alipay" style="font-size:12px;color:var(--cn-400);margin-top:8px;text-align:center">暂无可用支付方式，请联系管理员配置</div>
        </div>
        <button class="btn-pay" @click="doPay" :disabled="paying || (!payMethods.wechat && !payMethods.alipay)">{{ paying?'支付处理中...':'确认支付' }}</button>
      </div>
    </div>

    <!-- 支付二维码弹窗 -->
    <div v-if="showQr" class="modal-overlay" @click.self="showQr=false">
      <div class="modal-card">
        <div class="flex aic jbt mb4">
          <div class="modal-title">扫码支付</div>
          <button class="btn-close" @click="cancelPay">&times;</button>
        </div>
        <div style="text-align:center;padding:20px 0">
          <div style="font-size:14px;color:var(--cn-600);margin-bottom:16px">
            请使用{{ payType==='wechat'?'微信':'支付宝' }}扫描下方二维码完成支付
          </div>
          <div style="width:200px;height:200px;margin:0 auto;background:var(--cn-100);border-radius:12px;display:flex;align-items:center;justify-content:center;border:1px solid var(--cn-200)">
            <div style="text-align:center">
              <div style="margin-bottom:8px">
            <svg v-if="payType==='wechat'" width="48" height="48" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="12" fill="#07C160"/><path d="M8 12c0-3 3-5 6-5s6 2 6 5-3 5-6 5c-.5 0-1-.1-1.5-.2l-2.5 1.5.5-2C9 15.5 8 13.8 8 12z" fill="white"/></svg>
            <svg v-else width="48" height="48" viewBox="0 0 28 28" fill="none"><circle cx="14" cy="14" r="12" fill="#1677FF"/><text x="14" y="19" text-anchor="middle" fill="white" font-size="14" font-weight="bold">支</text></svg>
          </div>
              <div style="font-size:12px;color:var(--cn-500)">模拟支付二维码</div>
            </div>
          </div>
          <div style="font-size:20px;font-weight:700;color:var(--ce);margin-top:16px">¥{{ payPlan ? (payPlan.price/100).toFixed(0) : 0 }}</div>
          <div style="font-size:12px;color:var(--cn-400);margin-top:4px">订单号: {{ currentOrderNo }}</div>
        </div>
        <div style="display:flex;gap:10px;margin-top:16px">
          <button class="btn-pay" style="background:var(--cn-400)" @click="cancelPay">取消支付</button>
          <button class="btn-pay" @click="confirmPayMock" :disabled="paying">{{ paying?'处理中...':'我已完成支付' }}</button>
        </div>
      </div>
    </div>

    <!-- 订单记录 -->
    <h2 style="font-size:18px;font-weight:700;color:var(--cn-900);margin-bottom:16px;margin-top:32px">订单记录</h2>
    <div v-if="orders.length===0" style="text-align:center;padding:40px;color:var(--cn-400);font-size:13px">暂无订单记录</div>
    <div v-else class="orders-list">
      <div v-for="o in orders" :key="o.id" class="order-item">
        <div class="flex aic jbt">
          <div>
            <span style="font-weight:600;color:var(--cn-800)">{{ o.planName }}</span>
            <span class="tag" :class="o.status===1?'g':o.status===2?'d':'o'" style="margin-left:8px">{{ {0:'待支付',1:'已支付',2:'已取消'}[o.status] }}</span>
          </div>
          <span style="font-weight:600;color:var(--cn-800)">¥{{ (o.amount/100).toFixed(0) }}</span>
        </div>
        <div class="flex aic jbt" style="margin-top:4px;font-size:11px;color:var(--cn-400)">
          <span>{{ o.orderNo }}</span>
          <span>{{ o.createTime ? o.createTime.substring(0,10) : '-' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { userApi } from '@/api'
import api from '@/api'

const currentUser = ref(null)
const currentPlan = ref(null)
const plans = ref([])
const orders = ref([])

// 默认套餐数据（当后端无数据时显示）
const defaultPlans = [
  { id: 1, name: '免费版', level: 0, price: 0, period: '永久', description: '适合个人用户体验', dailyAiCreate: 3, maxAccounts: 1, maxAgents: 2, analysisLevel: '基础', batchEnabled: false, scheduleEnabled: false, exportEnabled: false, customAgentEnabled: false, color: '#9CA3AF' },
  { id: 2, name: '标准版', level: 1, price: 4900, period: '年', description: '适合内容创作者日常使用', dailyAiCreate: 20, maxAccounts: 2, maxAgents: 10, analysisLevel: '高级', batchEnabled: true, scheduleEnabled: true, exportEnabled: false, customAgentEnabled: false, color: '#F59E0B' },
  { id: 3, name: '专业版', level: 2, price: 9900, period: '年', description: '适合专业运营团队', dailyAiCreate: -1, maxAccounts: 3, maxAgents: 50, analysisLevel: '全功能', batchEnabled: true, scheduleEnabled: true, exportEnabled: true, customAgentEnabled: true, color: '#8B5CF6' }
]
const showPay = ref(false)
const showQr = ref(false)
const payPlan = ref(null)
const payType = ref('wechat')
const paying = ref(false)
const payMethods = reactive({ wechat: false, alipay: false })
const currentOrderNo = ref('')
const todayCount = ref(0)

async function loadData() {
  try {
    // 用户信息
    const u = await userApi.info()
    if (u.data?.code===200) {
      currentUser.value = u.data.data
    }
    // 套餐列表
    const p = await api.get('/member/plans')
    if (p.data?.code===200 && p.data.data && p.data.data.length > 0) {
      plans.value = p.data.data
    } else {
      plans.value = defaultPlans
    }
    if (currentUser.value) {
      currentPlan.value = plans.value.find(pl => pl.level === currentUser.value.memberLevel)
    }
    // 支付方式
    const m = await api.get('/member/payment-methods')
    if (m.data?.code===200) Object.assign(payMethods, m.data.data)
    // 订单
    const o = await api.get('/member/my-orders', { params: { pageSize: 20 } })
    if (o.data?.code===200) orders.value = o.data?.data?.records || []
  } catch(e) { console.error(e) }
}

const allPlans = computed(() => {
  // 显示所有套餐，当前套餐排第一个
  const sorted = [...defaultPlans].sort((a, b) => {
    if (a.level === currentUser.value?.memberLevel) return -1
    if (b.level === currentUser.value?.memberLevel) return 1
    return a.level - b.level
  })
  return sorted
})

function handleUpgrade(plan) {
  payPlan.value = plan
  showPay.value = true
}

async function doPay() {
  if (!payPlan.value) return
  if (!payMethods.wechat && !payMethods.alipay) {
    alert('暂无可用支付方式，请联系管理员配置')
    return
  }
  paying.value = true
  try {
    const res = await api.post('/member/orders', { planId: payPlan.value.id, payType: payType.value })
    if (res.data?.code===200) {
      currentOrderNo.value = res.data.data.orderNo
      showPay.value = false
      showQr.value = true
    } else {
      alert('创建订单失败: ' + (res.data?.message || '未知错误'))
    }
  } catch(e) {
    alert('创建订单失败: ' + (e.response?.data?.message || e.message))
  } finally { paying.value = false }
}

async function confirmPayMock() {
  if (!currentOrderNo.value) return
  paying.value = true
  try {
    const payRes = await api.post(`/member/orders/${currentOrderNo.value}/pay`)
    if (payRes.data?.code===200) {
      alert('支付成功！会员已升级')
      showQr.value = false
      currentOrderNo.value = ''
      loadData()
    } else {
      alert('支付确认失败: ' + (payRes.data?.message || '未知错误'))
    }
  } catch(e) {
    alert('支付确认失败: ' + (e.response?.data?.message || e.message))
  } finally { paying.value = false }
}

async function cancelPay() {
  if (currentOrderNo.value) {
    try { await api.post(`/member/orders/${currentOrderNo.value}/cancel`) } catch {}
  }
  showQr.value = false
  currentOrderNo.value = ''
}

onMounted(loadData)
</script>

<style scoped>
.membership-page{max-width:1200px;margin:0 auto;padding:32px 24px}
.page-header{margin-bottom:24px}
.page-header h1{font-size:24px;font-weight:700;color:var(--cn-900);margin-bottom:4px}
.page-header p{font-size:13px;color:var(--cn-500)}
.current-plan-card{background:linear-gradient(135deg,var(--cp-50),var(--cp-100));padding:16px 24px;border-radius:12px;margin-bottom:8px}
.plan-badge{padding:4px 16px;border-radius:20px;font-size:12px;font-weight:600;color:#fff}
.plans-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:20px}
.plan-card{background:#fff;border-radius:16px;padding:28px 24px;border:2px solid var(--cn-100);position:relative;transition:transform .2s,box-shadow .2s}
.plan-card:hover{transform:translateY(-4px);box-shadow:0 8px 30px rgba(0,0,0,.1)}
.plan-card.active{border-color:var(--cp-300)}
.plan-card.recommended{border-color:var(--ca-orange);box-shadow:0 4px 20px rgba(255,152,0,.15)}
.rec-badge{position:absolute;top:12px;right:12px;background:var(--ca-orange);color:#fff;font-size:11px;font-weight:600;padding:3px 10px;border-radius:10px}
.current-badge{position:absolute;top:12px;right:12px;color:#fff;font-size:11px;font-weight:600;padding:3px 10px;border-radius:10px}
.plan-name{font-size:20px;font-weight:700;margin-bottom:8px}
.plan-price{margin-bottom:12px}
.price-num{font-size:36px;font-weight:800;color:var(--cn-900)}
.price-period{font-size:14px;color:var(--cn-400)}
.plan-desc{font-size:12px;color:var(--cn-500);margin-bottom:16px}
.plan-features{list-style:none;padding:0;margin-bottom:20px}
.plan-features li{display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid var(--cn-50);font-size:13px;color:var(--cn-600)}
.plan-features li.disabled{color:var(--cn-300)}
.feat-val{font-weight:600;color:var(--cn-800)}.feat-val.check{color:var(--cs)}.feat-val.x{color:var(--cn-300)}
.plan-action{text-align:center}
.btn-current,.btn-free{padding:10px 0;border-radius:8px;font-size:14px;font-weight:600;width:100%;border:none;background:var(--cn-100);color:var(--cn-400)}
.btn-upgrade{padding:10px 32px;border-radius:8px;font-size:14px;font-weight:600;border:none;background:var(--cp-500);color:#fff;cursor:pointer;width:100%;transition:background .2s}
.btn-upgrade:hover{background:var(--cp-600)}
.modal-overlay{position:fixed;inset:0;background:rgba(0,0,0,.4);z-index:1000;display:flex;align-items:center;justify-content:center}
.modal-card{background:#fff;border-radius:16px;padding:24px 28px;width:420px;max-width:90vw;box-shadow:0 20px 60px rgba(0,0,0,.2)}
.modal-title{font-size:18px;font-weight:700;color:var(--cn-900)}
.btn-close{padding:4px 10px;border:none;background:var(--cn-100);border-radius:6px;font-size:18px;cursor:pointer;color:var(--cn-600)}
.pay-info{background:var(--cn-50);padding:16px;border-radius:12px;line-height:2.5;font-size:14px;color:var(--cn-600)}
.pay-methods{display:flex;gap:12px}
.pay-method{flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;padding:16px;border:2px solid var(--cn-200);border-radius:12px;cursor:pointer;transition:border-color .2s}
.pay-method.selected{border-color:var(--cp-500);background:var(--cp-50)}
.btn-pay{padding:12px 0;border-radius:10px;font-size:15px;font-weight:700;border:none;background:var(--cp-500);color:#fff;cursor:pointer;width:100%;margin-top:16px;transition:background .2s}
.btn-pay:disabled{opacity:.5;cursor:not-allowed}
.btn-pay:hover:not(:disabled){background:var(--cp-600)}
.orders-list{margin-top:8px}
.order-item{background:#fff;border:1px solid var(--cn-100);border-radius:10px;padding:14px 18px;margin-bottom:8px}
.tag{padding:2px 8px;border-radius:4px;font-size:11px;font-weight:600}.tag.g{background:#e8f5e9;color:#2e7d32}.tag.d{background:#ffebee;color:#c62828}.tag.o{background:#fff3e0;color:#e65100}
@media (max-width:768px){.plans-grid{grid-template-columns:1fr}}
</style>
