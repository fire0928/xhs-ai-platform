<template>
  <div class="step">
    <div :class="['step-dot', status]">
      <svg v-if="status === 'done'" width="12" height="12" viewBox="0 0 12 12" fill="none"><path d="M2.5 6L5 8.5L9.5 3.5" stroke="white" stroke-width="1.5" stroke-linecap="round"/></svg>
      <span v-else>{{ num }}</span>
    </div>
    <span :class="['step-label', { active: status === 'active' || status === 'done' }]">{{ label }}</span>
  </div>
</template>

<script setup>
defineProps({ num: Number, label: String, status: { type: String, default: 'pending' } })
</script>

<style scoped>
.step { display: flex; align-items: center; gap: 8px; }
.step-dot { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 600; position: relative; flex-shrink: 0; }
.step-dot.done { background: var(--cs); color: #fff; }
.step-dot.active { background: var(--cp-500); color: #fff; }
.step-dot.active::after { content: ''; position: absolute; inset: -4px; border-radius: 50%; border: 2px solid var(--cp-300); animation: pulse 2s infinite; }
.step-dot.pending { background: var(--cn-200); color: var(--cn-400); }
.step-label { font-size: 13px; font-weight: 500; color: var(--cn-400); }
.step-label.active { color: var(--cn-800); }
@keyframes pulse { 0%, 100% { opacity: 1; transform: scale(1); } 50% { opacity: .5; transform: scale(1.15); } }
</style>
