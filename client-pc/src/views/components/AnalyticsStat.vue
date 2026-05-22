<template>
  <div class="am">
    <div class="am-lb">{{ label }}</div>
    <div class="am-vl">{{ value }}</div>
    <div class="am-ch" :style="{ color }">{{ change }}</div>
  </div>
</template>

<script setup>
defineProps({ label: String, value: [String, Number], change: String, color: String })
</script>

<style scoped>
.am { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,.06); border: 1px solid var(--cn-100); }
.am-lb { font-size: 11px; color: var(--cn-500); font-weight: 500; margin-bottom: 8px; }
.am-vl { font-family: 'JetBrains Mono', monospace; font-size: 24px; font-weight: 700; color: var(--cn-900); }
.am-ch { font-size: 11px; font-weight: 500; margin-top: 4px; }
</style>
