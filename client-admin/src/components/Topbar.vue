<template>
  <header class="tb">
    <div class="tb-bc">管理后台 / <span>{{ pageName }}</span></div>
    <div class="tb-right">
      <div class="tb-search">
        <svg viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <input type="text" placeholder="搜索用户、内容、Agent..." />
      </div>
      <button class="tb-ib">
        <svg viewBox="0 0 18 18" fill="none"><path d="M4 7C4 4.79 6.24 3 9 3C11.76 3 14 4.79 14 7V11L15 14H3L4 11V7Z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M7 14C7 15.1 7.9 16 9 16C10.1 16 11 15.1 11 14" stroke="currentColor" stroke-width="1.5"/></svg>
        <span class="dot"></span>
      </button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const names = {
  overview:'系统总览',users:'用户管理',membership:'会员管理','ai-models':'AI大模型管理',
  agents:'Agent管理',monitor:'调用监控',content:'内容管理',analytics:'数据分析',settings:'系统设置'
}
const pageName = computed(() => names[route.name?.toLowerCase()] || route.name)
</script>

<style scoped>
.tb{height:var(--hh);min-height:var(--hh);background:#fff;border-bottom:1px solid var(--cn-200);display:flex;align-items:center;justify-content:space-between;padding:0 32px}
.tb-bc{font-size:13px;color:var(--cn-400)}
.tb-bc span{color:var(--cn-800);font-weight:500}
.tb-right{display:flex;align-items:center;gap:16px}
.tb-search{display:flex;align-items:center;gap:8px;background:var(--cn-100);border-radius:8px;padding:4px 12px;width:240px}
.tb-search input{border:none;background:none;outline:none;font-size:13px;color:var(--cn-700);width:100%}
.tb-search svg{width:14px;height:14px;color:var(--cn-400)}
.tb-ib{width:32px;height:32px;display:flex;align-items:center;justify-content:center;border-radius:6px;color:var(--cn-500);position:relative;cursor:pointer}
.tb-ib:hover{background:var(--cn-100)}
.tb-ib svg{width:18px;height:18px}
.tb-ib .dot{position:absolute;top:4px;right:4px;width:6px;height:6px;background:var(--cp-500);border-radius:50%;border:1.5px solid #fff}
</style>
