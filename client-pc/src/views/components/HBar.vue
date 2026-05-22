<template>
  <div class="hb">
    <div class="hb-l">{{ label }}</div>
    <div class="hb-t">
      <div class="hb-f" :style="{ width: pct + '%', background: gradient }">{{ val }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({ label: String, pct: Number, val: [String, Number] })
const gradient = computed(() => {
  if (props.pct > 80) return 'var(--gb)'
  if (props.pct > 50) return 'var(--cp-300)'
  return 'var(--cp-200)'
})
</script>

<style scoped>
.hb { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.hb-l { font-size: 11px; color: var(--cn-500); width: 90px; text-align: right; flex-shrink: 0; }
.hb-t { flex: 1; height: 20px; background: var(--cn-100); border-radius: 9999px; overflow: hidden; }
.hb-f { height: 100%; border-radius: 9999px; transition: width .5s ease; display: flex; align-items: center; padding-left: 8px; font-size: 11px; font-weight: 600; color: #fff; min-width: 24px; }
</style>
