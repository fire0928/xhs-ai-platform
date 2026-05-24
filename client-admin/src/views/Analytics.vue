<template>
  <div>
    <div class="flex aic jbt mb6">
      <div><div style="font-size:20px;font-weight:700;color:var(--cn-900)">数据分析</div><div style="font-size:13px;color:var(--cn-500)">全局统计报表 — 近 {{ days }} 天</div></div>
      <div class="flex aic g3">
        <select class="ipt" style="width:120px" v-model.number="days" @change="loadData">
          <option :value="7">近7天</option><option :value="30">近30天</option><option :value="90">近90天</option>
        </select>
        <button class="btn btn-p" @click="loadData" :disabled="loading">{{ loading?'加载中...':'刷新' }}</button>
      </div>
    </div>

    <!-- 核心指标 -->
    <div class="am-row">
      <div class="am"><div class="am-lb">新增内容</div><div class="am-vl">{{ fmtNum(data.newContents) }}</div><div class="am-ch">较上期 {{ growthRate(data.newContents, data.prevNewContents) }}</div></div>
      <div class="am"><div class="am-lb">新增用户</div><div class="am-vl" style="color:var(--cp-500)">{{ fmtNum(data.newUsers) }}</div><div class="am-ch">{{ days }}天累计</div></div>
      <div class="am"><div class="am-lb">发布成功数</div><div class="am-vl" style="color:var(--cs)">{{ fmtNum(data.publishedCount) }}</div><div class="am-ch">成功率 {{ pubRate }}%</div></div>
      <div class="am"><div class="am-lb">人均创作</div><div class="am-vl">{{ fmtDecimal(data.avgContentPerUser) }}</div><div class="am-ch">内容/用户</div></div>
    </div>

    <!-- 订单指标 -->
    <div class="am-row" style="margin-bottom:24px">
      <div class="am"><div class="am-lb">订单数量</div><div class="am-vl" style="color:var(--ca-purple)">{{ fmtNum(data.orderCount) }}</div><div class="am-ch">{{ days }}天累计</div></div>
      <div class="am"><div class="am-lb">成交金额</div><div class="am-vl" style="color:var(--ca-orange)">{{ fmtNum(data.paidOrderCount) }}</div><div class="am-ch">已支付订单</div></div>
      <div class="am"><div class="am-lb">付费会员人数</div><div class="am-vl" style="color:var(--cs)">{{ fmtNum(data.paidMemberCount) }}</div><div class="am-ch">总付费用户</div></div>
      <div class="am"><div class="am-lb">付费转化率</div><div class="am-vl">{{ payRate }}%</div><div class="am-ch">付费/总用户</div></div>
    </div>

    <!-- 数据总览 + 漏斗 -->
    <div style="display:grid;grid-template-columns:1.5fr 1fr;gap:20px;margin-bottom:24px">
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">数据指标总览</div>
        <div style="font-size:13px;color:var(--cn-600);line-height:2.5">
          <div class="flex aic jbt"><span>新增内容</span><span class="fnm" style="color:var(--cn-800)">{{ fmtNum(data.newContents) }}</span></div>
          <div class="flex aic jbt"><span>新增用户</span><span class="fnm" style="color:var(--cn-800)">{{ fmtNum(data.newUsers) }}</span></div>
          <div class="flex aic jbt"><span>发布成功</span><span class="fnm" style="color:var(--cs)">{{ fmtNum(data.publishedCount) }}</span></div>
          <div class="flex aic jbt"><span>人均创作</span><span class="fnm" style="color:var(--ca-purple)">{{ fmtDecimal(data.avgContentPerUser) }}</span></div>
          <div class="flex aic jbt"><span>统计周期</span><span class="fnm" style="color:var(--cn-500)">{{ days }} 天</span></div>
        </div>
      </div>
      <div class="card">
        <div class="card-t" style="margin-bottom:16px">发布转化漏斗</div>
        <div class="hb" style="margin-bottom:10px"><div class="hb-l">内容创建</div><div class="hb-t"><div class="hb-f" style="width:100%;background:var(--gai)">{{ fmtNum(data.newContents) }}</div></div></div>
        <div class="hb" style="margin-bottom:10px"><div class="hb-l">发布成功</div><div class="hb-t"><div class="hb-f" :style="{width:pubRate+'%',background:'var(--cs)'}">{{ fmtNum(data.publishedCount) }}</div></div></div>
        <div style="font-size:11px;color:var(--cn-400);text-align:center;margin-top:8px">发布成功率 {{ pubRate }}%</div>
      </div>
    </div>

    <!-- 用户分析 -->
    <div class="card" style="margin-bottom:24px">
      <div class="card-t" style="margin-bottom:16px">用户分析</div>
      <div style="display:grid;grid-template-columns:1fr 1fr 1fr 1fr;gap:20px">
        <div class="am"><div class="am-lb">总用户数</div><div class="am-vl" style="font-size:28px">{{ fmtNum(userStats.totalUsers) }}</div></div>
        <div class="am"><div class="am-lb">活跃用户</div><div class="am-vl" style="font-size:28px;color:var(--cs)">{{ fmtNum(userStats.activeUsers) }}</div></div>
        <div class="am"><div class="am-lb">今日新增</div><div class="am-vl" style="font-size:28px;color:var(--cp-500)">{{ fmtNum(userStats.todayNew) }}</div></div>
        <div class="am"><div class="am-lb">禁用用户</div><div class="am-vl" style="font-size:28px;color:var(--ce)">{{ fmtNum(userStats.disabledUsers) }}</div></div>
      </div>
    </div>

    <!-- 系统概览 -->
    <div class="card">
      <div class="card-t" style="margin-bottom:16px">系统概览</div>
      <div style="display:grid;grid-template-columns:1fr 1fr 1fr 1fr;gap:20px">
        <div class="am"><div class="am-lb">总内容数</div><div class="am-vl" style="font-size:28px">{{ fmtNum(overview.totalContents) }}</div></div>
        <div class="am"><div class="am-lb">AI调用总量</div><div class="am-vl" style="font-size:28px;color:var(--ca-purple)">{{ fmtNum(overview.aiTotal) }}</div></div>
        <div class="am"><div class="am-lb">待审核内容</div><div class="am-vl" style="font-size:28px;color:var(--ca-orange)">{{ fmtNum(overview.pendingContents) }}</div></div>
        <div class="am"><div class="am-lb">今日发布</div><div class="am-vl" style="font-size:28px;color:var(--cs)">{{ fmtNum(overview.publishedToday) }}</div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api'

const loading = ref(false)
const days = ref(7)
const data = reactive({ newContents: 0, prevNewContents: 0, newUsers: 0, publishedCount: 0, avgContentPerUser: 0 })
const userStats = reactive({ totalUsers: 0, activeUsers: 0, todayNew: 0, disabledUsers: 0 })
const overview = reactive({ totalContents: 0, aiTotal: 0, pendingContents: 0, publishedToday: 0 })

const pubRate = computed(() => {
  if (!data.newContents) return 0
  return Math.round(data.publishedCount / data.newContents * 100)
})
const payRate = computed(() => {
  if (!userStats.totalUsers) return 0
  return Math.round((data.paidMemberCount || 0) / userStats.totalUsers * 100)
})

function fmtNum(n) { return n == null ? '0' : Number(n).toLocaleString() }
function fmtDecimal(n) { return n == null ? '0' : Number(n).toFixed(1) }
function growthRate(cur, prev) {
  if (!prev) return '-'
  const r = Math.round((cur - prev) / prev * 100)
  return r >= 0 ? `↑ ${r}%` : `↓ ${Math.abs(r)}%`
}

async function loadData() {
  loading.value = true
  try {
    // 数据分析
    const res = await api.get('/analytics/summary', { params: { days: days.value } })
    if (res.data.code === 200 && res.data.data) {
      Object.assign(data, res.data.data)
    }
    // 前一周期对比
    const prev = await api.get('/analytics/summary', { params: { days: days.value * 2 } })
    if (prev.data.code === 200 && prev.data.data) {
      data.prevNewContents = prev.data.data.newContents - data.newContents
    }
    // 用户统计
    const us = await api.get('/users/stats')
    if (us.data.code === 200) { Object.assign(userStats, us.data.data) }
    // 系统概览
    const ov = await api.get('/overview')
    if (ov.data.code === 200) { Object.assign(overview, ov.data.data) }
  } catch (e) {
    console.error('Analytics error', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
