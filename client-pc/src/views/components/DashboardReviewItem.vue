<template>
  <div class="item">
    <div class="title">{{ title }}</div>
    <div class="meta">来自 {{ agent }} · {{ time }}</div>
  </div>
</template>

<script setup>
defineProps({ title: String, agent: String, time: String })
</script>

<style scoped>
.item { padding: 12px 0; border-bottom: 1px solid var(--cn-50); cursor: pointer; }
.item:hover { background: var(--cn-50); margin: 0 -12px; padding: 12px; }
.item:last-child { border-bottom: none; }
.title { font-size: 13px; font-weight: 600; color: var(--cn-800); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.meta { font-size: 11px; color: var(--cn-400); margin-top: 4px; }
</style>
