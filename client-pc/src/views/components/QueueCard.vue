<template>
  <div class="qc">
    <div class="qc-drag">
      <svg viewBox="0 0 16 16" fill="none"><circle cx="4" cy="4" r="1.5" fill="currentColor"/><circle cx="12" cy="4" r="1.5" fill="currentColor"/><circle cx="4" cy="8" r="1.5" fill="currentColor"/><circle cx="12" cy="8" r="1.5" fill="currentColor"/><circle cx="4" cy="12" r="1.5" fill="currentColor"/><circle cx="12" cy="12" r="1.5" fill="currentColor"/></svg>
    </div>
    <div class="qc-th" :style="{ background: bg }">
      <svg viewBox="0 0 48 48"><rect width="48" height="48" :fill="bg"/><circle cx="24" cy="20" r="8" fill="var(--cp-300)" opacity=".5"/></svg>
    </div>
    <div class="qc-info">
      <div class="qc-tt">{{ title }}</div>
      <div class="qc-time">{{ time }}</div>
    </div>
    <span :class="['qc-st', status]">{{ statusText }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ title: String, time: String, status: String, bg: String })
const statusText = computed(() => {
  const map = { sched: '定时中', pend: '待发布', pubg: '发布中' }
  return map[props.status] || '待发布'
})
</script>

<style scoped>
.qc { display: flex; align-items: center; gap: 16px; padding: 12px 16px; background: #fff; border-radius: 12px; border: 1px solid var(--cn-100); margin-bottom: 8px; transition: all .15s; cursor: grab; }
.qc:hover { box-shadow: 0 4px 6px rgba(0,0,0,.07); border-color: var(--cn-200); }
.qc-drag { color: var(--cn-300); cursor: grab; }
.qc-drag svg { width: 16px; height: 16px; }
.qc-th { width: 48px; height: 48px; border-radius: 6px; overflow: hidden; flex-shrink: 0; }
.qc-th svg { width: 100%; height: 100%; }
.qc-info { flex: 1; min-width: 0; }
.qc-tt { font-size: 13px; font-weight: 600; color: var(--cn-800); margin-bottom: 2px; }
.qc-time { font-size: 11px; color: var(--cn-400); }
.qc-st { padding: 2px 8px; border-radius: 9999px; font-size: 11px; font-weight: 600; }
.qc-st.pend { background: #DBEAFE; color: #1D4ED8; }
.qc-st.sched { background: var(--ca-orange-l); color: #B45309; }
.qc-st.pubg { background: var(--cp-50); color: var(--cp-600); animation: glow 2s infinite; }
@keyframes glow { 0%, 100% { opacity: 1; } 50% { opacity: .6; } }
</style>
