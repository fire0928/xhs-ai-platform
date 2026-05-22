<template>
  <header class="tb">
    <div class="tb-bc">红书智创 / <span>{{ pageName }}</span></div>
    <div class="tb-right">
      <div class="tb-search">
        <svg viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <input type="text" placeholder="搜索内容、功能...">
      </div>
      <button class="tb-ib" title="收藏">
        <svg viewBox="0 0 18 18" fill="none"><path d="M9 2L11.5 7H16.5L12.5 10.5L14 16L9 12.5L4 16L5.5 10.5L1.5 7H6.5L9 2Z" stroke="currentColor" stroke-width="1.5" fill="none"/></svg>
      </button>
      <button class="tb-ib" title="通知">
        <svg viewBox="0 0 18 18" fill="none"><path d="M4 7C4 4.79 6.24 3 9 3C11.76 3 14 4.79 14 7V11L15 14H3L4 11V7Z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M7 14C7 15.1 7.9 16 9 16C10.1 16 11 15.1 11 14" stroke="currentColor" stroke-width="1.5"/></svg>
        <span class="dot"></span>
      </button>
      <button class="tb-ib" @click="$router.push('/settings')" title="设置">
        <svg viewBox="0 0 18 18" fill="none"><circle cx="9" cy="9" r="2.5" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M9 1.5V3.5M9 14.5V16.5M1.5 9H3.5M14.5 9H16.5M3.4 3.4L4.8 4.8M13.2 13.2L14.6 14.6M14.6 3.4L13.2 4.8M4.8 13.2L3.4 14.6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
      </button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const titles = {
  Dashboard: '数据看板', AiCreate: 'AI 创作', Review: '图文审核',
  Queue: '发布队列', Analytics: '数据分析', Accounts: '账号绑定', Settings: '个人设置'
}
const pageName = computed(() => titles[route.name] || '红书智创')
</script>

<style scoped>
.tb { height: var(--hh); min-height: var(--hh); background: #fff; border-bottom: 1px solid var(--cn-200); display: flex; align-items: center; justify-content: space-between; padding: 0 32px; }
.tb-bc { font-size: 13px; color: var(--cn-400); }
.tb-bc span { color: var(--cn-800); font-weight: 500; }
.tb-right { display: flex; align-items: center; gap: 16px; }
.tb-search { display: flex; align-items: center; gap: 8px; background: var(--cn-100); border-radius: 8px; padding: 4px 12px; width: 220px; }
.tb-search input { border: none; background: none; outline: none; font-size: 13px; color: var(--cn-700); width: 100%; }
.tb-search svg { width: 14px; height: 14px; color: var(--cn-400); }
.tb-ib { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border-radius: 6px; color: var(--cn-500); transition: all .15s; position: relative; }
.tb-ib:hover { background: var(--cn-100); color: var(--cn-700); }
.tb-ib svg { width: 18px; height: 18px; }
.tb-ib .dot { position: absolute; top: 4px; right: 4px; width: 6px; height: 6px; background: var(--cp-500); border-radius: 50%; border: 1.5px solid #fff; }
</style>
