<template>
  <div class="qc">
    <div class="qc-th">
      <img v-if="imageUrl" :src="imageUrl" alt="配图" @error="$event.target.style.display='none'">
      <div v-else class="qc-ph" :style="{ background: bg }">
        <svg viewBox="0 0 48 48"><rect width="48" height="48" :fill="bg"/><circle cx="24" cy="20" r="8" fill="var(--cp-300)" opacity=".5"/></svg>
      </div>
    </div>
    <div class="qc-info">
      <div class="qc-tt">{{ title }}</div>
      <div class="qc-time">{{ time }}</div>
    </div>
    <span :class="['qc-st', statusClass]">{{ statusText }}</span>
    <button v-if="showDelete" class="qc-del" @click.stop="$emit('delete')" title="删除">
      <svg width="14" height="14" viewBox="0 0 14 14" fill="none"><path d="M2 3.5H12M5.5 3.5V2.5C5.5 2.22386 5.72386 2 6 2H8C8.27614 2 8.5 2.22386 8.5 2.5V3.5M3 3.5V11.5C3 11.7761 3.22386 12 3.5 12H10.5C10.7761 12 11 11.7761 11 11.5V3.5M5.5 6V9.5M8.5 6V9.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ title: String, time: String, status: String, statusClass: String, bg: String, imageUrl: String, showDelete: Boolean })
defineEmits(['delete'])
const statusText = computed(() => {
  const map = { sched: '定时中', pend: '待发布', pubg: '发布中', done: '已发布', fail: '失败' }
  return map[props.statusClass] || '待发布'
})
</script>

<style scoped>
.qc { display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #fff; border-radius: 12px; border: 1px solid var(--cn-100); margin-bottom: 8px; transition: all .15s; cursor: pointer; position: relative; }
.qc:hover { box-shadow: 0 4px 6px rgba(0,0,0,.07); border-color: var(--cn-200); }
.qc-th { width: 48px; height: 48px; border-radius: 8px; overflow: hidden; flex-shrink: 0; background: var(--cn-100); }
.qc-th img { width: 100%; height: 100%; object-fit: cover; }
.qc-ph { width: 100%; height: 100%; }
.qc-ph svg { width: 100%; height: 100%; }
.qc-info { flex: 1; min-width: 0; }
.qc-tt { font-size: 13px; font-weight: 600; color: var(--cn-800); margin-bottom: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.qc-time { font-size: 11px; color: var(--cn-400); }
.qc-st { padding: 2px 8px; border-radius: 9999px; font-size: 11px; font-weight: 600; flex-shrink: 0; }
.qc-st.pend { background: #DBEAFE; color: #1D4ED8; }
.qc-st.sched { background: var(--ca-orange-l); color: #B45309; }
.qc-st.pubg { background: var(--cp-50); color: var(--cp-600); animation: glow 2s infinite; }
.qc-st.done { background: #DCFCE7; color: #15803D; }
.qc-st.fail { background: #FEE2E2; color: #B91C1C; }
@keyframes glow { 0%, 100% { opacity: 1; } 50% { opacity: .6; } }
.qc-del { width: 28px; height: 28px; border-radius: 6px; border: none; background: transparent; color: var(--cn-400); cursor: pointer; display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity .15s; }
.qc:hover .qc-del { opacity: 1; }
.qc-del:hover { background: #FEE2E2; color: #B91C1C; }
</style>
