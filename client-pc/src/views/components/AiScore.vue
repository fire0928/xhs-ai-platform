<template>
  <div class="ai-sr">
    <span class="ai-sr-l">{{ label }}</span>
    <div class="ai-sr-b"><div class="ai-sr-f" :style="{ width: score + '%', background: color }"></div></div>
    <span class="ai-sr-v">{{ score }}</span>
  </div>
</template>

<script setup>
defineProps({ label: String, score: Number, color: String })
</script>

<style scoped>
.ai-sr { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.ai-sr-l { font-size: 11px; color: var(--cn-600); width: 60px; flex-shrink: 0; }
.ai-sr-b { flex: 1; height: 6px; background: var(--cn-200); border-radius: 9999px; overflow: hidden; }
.ai-sr-f { height: 100%; border-radius: 9999px; }
.ai-sr-v { font-size: 11px; font-weight: 600; color: var(--cn-700); width: 32px; }
</style>
