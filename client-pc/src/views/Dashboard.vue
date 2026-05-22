<template>
  <div>
    <div class="dash-header">
      <div class="dash-greeting">
        <div class="avatar">小</div>
        <div>
          <div class="greeting-text">早上好，创作者 ✨</div>
          <div class="greeting-sub">今日有 <strong>3 篇内容</strong> 待发布</div>
        </div>
      </div>
      <div class="dash-actions">
        <button class="btn btn-ghost"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 2V14M2 8H14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>导出报告</button>
        <button class="btn btn-ai" @click="$router.push('/ai-create')"><svg width="16" height="16" viewBox="0 0 16 16" fill="none"><path d="M8 1L10 6H15L11 9.5L12.5 15L8 11.5L3.5 15L5 9.5L1 6H6L8 1Z" fill="white"/></svg>AI 一键创作</button>
      </div>
    </div>

    <div class="met-row">
      <MetricCard label="今日发布" :value="7" trend="+2 较昨日" :up="true" color="#FE2C55" />
      <MetricCard label="总互动量" :value="`2,847`" trend="+18.5%" :up="true" color="#F59E0B" />
      <MetricCard label="粉丝增长" :value="`+156`" trend="+23 较上周" :up="true" color="#22C55E" />
      <MetricCard label="AI 创作次数" :value="42" trend="+8 本周" :up="true" color="#8B5CF6" />
    </div>

    <div class="chart-row">
      <div class="cc">
        <div class="cc-h">
          <div><div class="cc-t">近 7 天趋势</div><div class="cc-st">发布量 & 互动量双轴对比</div></div>
          <div class="legend"><span class="ld ld-pink"></span>发布量 <span class="ld ld-orange"></span>互动量</div>
        </div>
        <svg viewBox="0 0 520 180" style="width:100%">
          <line x1="40" y1="155" x2="510" y2="155" stroke="#E7E5E4" stroke-width=".5"/>
          <polyline points="107,120 174,95 241,108 308,75 375,62 442,82 509,48" fill="none" stroke="#FE2C55" stroke-width="2.5" stroke-linecap="round"/>
          <polyline points="107,130 174,110 241,115 308,88 375,72 442,92 509,55" fill="none" stroke="#F59E0B" stroke-width="2" stroke-dasharray="6 3"/>
          <text x="107" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">一</text>
          <text x="174" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">二</text>
          <text x="241" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">三</text>
          <text x="308" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">四</text>
          <text x="375" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">五</text>
          <text x="442" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">六</text>
          <text x="509" y="172" text-anchor="middle" fill="#A8A29E" font-size="10">日</text>
        </svg>
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">内容效果 Top 5</div></div>
        <HBar label="春日穿搭" :pct="92" val="892" />
        <HBar label="咖啡探店" :pct="75" val="723" />
        <HBar label="护肤分享" :pct="60" val="584" />
        <HBar label="周末出游" :pct="48" val="467" />
        <HBar label="美食教程" :pct="38" val="352" />
      </div>
    </div>

    <div class="bot-row">
      <div class="cc">
        <div class="cc-h"><div class="cc-t">待审核内容</div><button class="btn btn-ghost btn-sm" @click="$router.push('/review')">查看全部</button></div>
        <ReviewItem title="春日樱花限定甜品，颜值超绝" agent="生活分享Agent" time="2分钟前" />
        <ReviewItem title="周末city walk路线推荐，文艺感拉满" agent="旅行攻略Agent" time="15分钟前" />
        <ReviewItem title="居家好物分享，提升幸福感的小物件" agent="手动上传" time="1小时前" />
      </div>
      <div class="cc">
        <div class="cc-h"><div class="cc-t">快捷操作</div></div>
        <div class="quick-grid">
          <div class="quick-card highlight" @click="$router.push('/ai-create')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cp-500)"><path d="M12 2L14.5 8.5H21L15.75 12.5L17.75 19L12 15L6.25 19L8.25 12.5L3 8.5H9.5L12 2Z" fill="currentColor"/></svg>
            <span>新建 AI 创作</span>
          </div>
          <div class="quick-card" @click="$router.push('/queue')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><rect x="3" y="4" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/><rect x="3" y="10" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/><rect x="3" y="16" width="18" height="3" rx="1" fill="currentColor" opacity=".5"/></svg>
            <span>发布队列</span>
          </div>
          <div class="quick-card" @click="$router.push('/accounts')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2" fill="none"/><path d="M4 20C4 16.5 7 14 12 14C17 14 20 16.5 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
            <span>账号管理</span>
          </div>
          <div class="quick-card" @click="$router.push('/analytics')">
            <svg viewBox="0 0 24 24" fill="none" style="width:24px;height:24px;color:var(--cn-500)"><path d="M3 3H9V9H3V3ZM15 3H21V9H15V3ZM3 15H9V21H3V15ZM15 15H21V21H15V15Z" stroke="currentColor" stroke-width="2" fill="none"/></svg>
            <span>数据报告</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import MetricCard from './components/DashboardMetric.vue'
import ReviewItem from './components/DashboardReviewItem.vue'
import HBar from './components/HBar.vue'
</script>

<style scoped>
.dash-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.dash-greeting { display: flex; align-items: center; gap: 16px; }
.avatar { width: 44px; height: 44px; border-radius: 50%; background: var(--gb); display: flex; align-items: center; justify-content: center; color: #fff; font-size: 20px; font-weight: 700; }
.greeting-text { font-size: 20px; font-weight: 700; color: var(--cn-900); }
.greeting-sub { font-size: 13px; color: var(--cn-500); }
.greeting-sub strong { color: var(--cp-500); font-weight: 600; }
.dash-actions { display: flex; align-items: center; gap: 12px; }
.met-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }
.chart-row { display: grid; grid-template-columns: 1.6fr 1fr; gap: 20px; margin-bottom: 24px; }
.bot-row { display: grid; grid-template-columns: 1.4fr 1fr; gap: 20px; }

.btn { display: inline-flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 500; transition: all .15s; white-space: nowrap; }
.btn svg { width: 16px; height: 16px; }
.btn-ghost { color: var(--cn-600); }
.btn-ghost:hover { background: var(--cn-100); }
.btn-ai { background: var(--gai); color: #fff; }
.btn-sm { padding: 4px 12px; font-size: 11px; }

.cc { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.cc-h { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.cc-t { font-size: 14px; font-weight: 600; color: var(--cn-800); }
.cc-st { font-size: 11px; color: var(--cn-500); }
.legend { display: flex; align-items: center; gap: 12px; font-size: 11px; color: var(--cn-400); }
.ld { width: 8px; height: 3px; border-radius: 2px; display: inline-block; margin-right: 4px; }
.ld-pink { background: var(--cp-500); }
.ld-orange { background: var(--ca-orange); }

.quick-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.quick-card { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; border-radius: 12px; border: 1px solid var(--cn-100); gap: 8px; cursor: pointer; transition: all .15s; }
.quick-card:hover { background: var(--cn-50); }
.quick-card.highlight { border-color: var(--cp-200); background: linear-gradient(135deg, #FFE0E5, #EDE9FE); }
.quick-card span { font-size: 13px; font-weight: 500; color: var(--cn-700); }
</style>
