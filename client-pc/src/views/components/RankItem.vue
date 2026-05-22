<template>
  <div class="rki">
    <div :class="['rkn', rank <= 3 ? 'top' : 'nm']">{{ rank }}</div>
    <div class="rkth" :style="{ background: bg }">
      <svg viewBox="0 0 36 36"><rect width="36" height="36" :fill="bg"/></svg>
    </div>
    <div class="rkinfo"><div class="rktt">{{ title }}</div></div>
    <div class="rkvl">{{ value }}</div>
  </div>
</template>

<script setup>
defineProps({ rank: Number, title: String, value: String, bg: String })
</script>

<style scoped>
.rki { display: flex; align-items: center; gap: 12px; padding: 12px 0; border-bottom: 1px solid var(--cn-50); }
.rki:last-child { border-bottom: none; }
.rkn { width: 22px; height: 22px; border-radius: 6px; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; flex-shrink: 0; }
.rkn.top { background: var(--cp-500); color: #fff; }
.rkn.nm { background: var(--cn-100); color: var(--cn-500); }
.rkth { width: 36px; height: 36px; border-radius: 6px; flex-shrink: 0; overflow: hidden; }
.rkth svg { width: 100%; height: 100%; }
.rkinfo { flex: 1; min-width: 0; }
.rktt { font-size: 13px; font-weight: 500; color: var(--cn-700); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.rkvl { font-family: 'JetBrains Mono', monospace; font-size: 13px; font-weight: 600; color: var(--cn-800); }
</style>
