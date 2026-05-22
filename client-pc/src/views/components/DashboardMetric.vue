<template>
  <div class="mc-card" :style="`--mc-color:${color}`">
    <div class="mc-label">
      <svg viewBox="0 0 14 14" fill="none"><rect x="1" y="4" width="5" height="9" rx="1" fill="currentColor" opacity=".7"/><rect x="8" y="1" width="5" height="12" rx="1" fill="currentColor" opacity=".4"/></svg>
      {{ label }}
    </div>
    <div class="mc-val">{{ value }}</div>
    <div class="mc-trend" :class="{ up }">
      <svg viewBox="0 0 12 12" fill="none"><path d="M6 2L10 7H2L6 2Z" :fill="up ? 'currentColor' : 'none'" :stroke="up ? 'none' : 'currentColor'"/></svg>
      {{ trend }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  label: String, value: [String, Number], trend: String, up: Boolean, color: String
})
</script>

<style scoped>
.mc-card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); position: relative; overflow: hidden; transition: transform .2s, box-shadow .2s; }
.mc-card:hover { transform: translateY(-2px); box-shadow: 0 4px 6px rgba(0,0,0,.07); }
.mc-card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px; background: var(--mc-color, #FE2C55); }
.mc-label { font-size: 11px; color: var(--cn-500); font-weight: 500; margin-bottom: 8px; display: flex; align-items: center; gap: 4px; }
.mc-label svg { width: 14px; height: 14px; color: var(--mc-color, #FE2C55); }
.mc-val { font-family: 'JetBrains Mono', monospace; font-size: 28px; font-weight: 700; color: var(--cn-900); line-height: 1.1; margin-bottom: 8px; }
.mc-trend { display: flex; align-items: center; gap: 4px; font-size: 11px; font-weight: 500; color: var(--cn-400); }
.mc-trend.up { color: #22C55E; }
.mc-trend svg { width: 12px; height: 12px; }
</style>
