<template>
  <aside class="sb">
    <div class="sb-brand">
      <div class="sb-brand-icon"><span class="brand-diamond">◆</span></div>
      <span class="sb-brand-text">红书智创</span>
      <span class="sb-brand-badge">Admin</span>
    </div>
    <nav class="sb-nav">
      <div class="sb-lbl">总览</div>
      <router-link to="/overview" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><rect x="2" y="2" width="6" height="6" rx="1.5" fill="currentColor" opacity=".6"/><rect x="10" y="2" width="6" height="6" rx="1.5" fill="currentColor" opacity=".4"/><rect x="2" y="10" width="6" height="6" rx="1.5" fill="currentColor" opacity=".4"/><rect x="10" y="10" width="6" height="6" rx="1.5" fill="currentColor" opacity=".6"/></svg>
        <span>系统总览</span>
      </router-link>

      <div class="sb-lbl">用户与会员</div>
      <router-link to="/users" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><circle cx="7" cy="7" r="3.5" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M1 15C1 12 3.5 10 7 10C10.5 10 13 12 13 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" fill="none"/></svg>
        <span>用户管理</span>
        <span class="cnt-b">{{ fmtK(stats.totalUsers) }}</span>
      </router-link>
      <router-link to="/membership" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M9 2L11 7H16L12 10.5L13.5 16L9 12.5L4.5 16L6 10.5L2 7H7L9 2Z" fill="currentColor" opacity=".6"/></svg>
        <span>会员管理</span>
      </router-link>
      <router-link to="/orders" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><rect x="2" y="3" width="14" height="12" rx="2" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M2 7H16" stroke="currentColor" stroke-width="1.5"/><path d="M6 11H10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>订单管理</span>
      </router-link>

      <div class="sb-lbl">AI 与内容</div>
      <router-link to="/ai-models" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M6 3L6 6L3 6L3 12L6 12L6 15L12 15L12 12L15 12L15 6L12 6L12 3L6 3Z" stroke="currentColor" stroke-width="1.5" fill="none"/><circle cx="9" cy="9" r="2" fill="currentColor" opacity=".4"/></svg>
        <span>AI 大模型管理</span>
      </router-link>
      <router-link to="/agents" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M9 2L10.5 5.5H14.5L11.25 8L12.75 12L9 9.5L5.25 12L6.75 8L3.5 5.5H7.5L9 2Z" fill="currentColor" opacity=".6"/></svg>
        <span>Agent 管理</span>
      </router-link>
      <router-link to="/content-directions" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M3 4.5h12M3 8h8M3 11.5h5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>创作方向</span>
      </router-link>
      <router-link to="/monitor" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M2 14L6 8L10 11L16 4" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="none"/></svg>
        <span>调用监控</span>
      </router-link>
      <router-link to="/content" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M3 3H15C15.55 3 16 3.45 16 4V14C16 14.55 15.55 15 15 15H3C2.45 15 2 14.55 2 14V4C2 3.45 2.45 3 3 3Z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M6 7H12M6 10H10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>内容管理</span>
        <span v-if="stats.pendingContents > 0" class="cnt-b alert">{{ stats.pendingContents }}</span>
      </router-link>

      <div class="sb-lbl">数据</div>
      <router-link to="/analytics" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><path d="M3 15V8M7 15V5M11 15V10M15 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
        <span>数据分析</span>
      </router-link>
      <router-link to="/settings" class="sb-item">
        <svg viewBox="0 0 18 18" fill="none"><circle cx="9" cy="9" r="2.5" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M9 1.5V3.5M9 14.5V16.5M1.5 9H3.5M14.5 9H16.5M3.4 3.4L4.8 4.8M13.2 13.2L14.6 14.6M14.6 3.4L13.2 4.8M4.8 13.2L3.4 14.6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <span>系统设置</span>
      </router-link>
    </nav>
    <div class="sb-user">
      <div class="sb-avatar">{{ adminAvatar }}</div>
      <div class="sb-user-info">
        <div class="sb-user-name">{{ adminName }}</div>
        <div class="sb-user-plan">{{ adminEmail }}</div>
      </div>
      <button class="sb-logout" title="退出登录" @click="handleLogout">
        <svg viewBox="0 0 16 16" fill="none"><path d="M6 2H3C2.45 2 2 2.45 2 3V13C2 13.55 2.45 14 3 14H6M11 11L14 8L11 5M14 8H6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/></svg>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/store'

const router = useRouter()
const store = useAdminStore()
const stats = computed(() => store.stats)

const adminName = computed(() => store.adminInfo?.name || '超级管理员')
const adminEmail = computed(() => store.adminInfo?.email || 'admin@hongshu.ai')
const adminAvatar = computed(() => (store.adminInfo?.name || '管').charAt(0))

function fmtK(v) {
  if (v == null) return '--'
  if (v >= 10000) return (v / 10000).toFixed(1) + '万'
  return Number(v).toLocaleString()
}

function handleLogout() {
  store.logout()
  router.push('/login')
}

onMounted(() => {
  store.fetchStats()
})
</script>

<style scoped>
.sb{width:var(--sw);min-width:var(--sw);background:var(--cn-900);display:flex;flex-direction:column;z-index:10}
.sb-brand{height:var(--hh);display:flex;align-items:center;padding:0 20px;gap:12px;border-bottom:1px solid rgba(255,255,255,.08)}
.sb-brand-icon{width:28px;height:28px;background:var(--gai);border-radius:6px;display:flex;align-items:center;justify-content:center}
.brand-diamond{color:#fff;font-size:16px}
.sb-brand-text{font-size:14px;font-weight:600;color:#fff}
.sb-brand-badge{font-size:11px;background:rgba(254,44,85,.2);color:var(--cp-400);padding:2px 6px;border-radius:16px;font-weight:500}
.sb-nav{flex:1;padding:12px;overflow-y:auto}
.sb-lbl{font-size:11px;font-weight:600;color:rgba(255,255,255,.35);text-transform:uppercase;letter-spacing:.06em;padding:16px 12px 4px}
.sb-item{display:flex;align-items:center;gap:12px;padding:8px 12px;border-radius:6px;color:rgba(255,255,255,.55);font-size:13px;font-weight:500;cursor:pointer;transition:all .15s;position:relative;margin-bottom:2px;text-decoration:none}
.sb-item:hover{background:rgba(255,255,255,.06);color:rgba(255,255,255,.85)}
.sb-item.active,.sb-item.router-link-exact-active{background:rgba(254,44,85,.15);color:var(--cp-400);font-weight:600}
.sb-item.active::before,.sb-item.router-link-exact-active::before{content:'';position:absolute;left:-12px;top:4px;bottom:4px;width:3px;background:var(--cp-500);border-radius:0 2px 2px 0}
.sb-item svg{width:18px;height:18px;flex-shrink:0}
.cnt-b{background:var(--cp-500);color:#fff;font-size:10px;min-width:18px;height:18px;border-radius:16px;display:flex;align-items:center;justify-content:center;font-weight:600;margin-left:auto}
.cnt-b.alert{background:var(--ce)}
.sb-user{padding:16px;border-top:1px solid rgba(255,255,255,.08);display:flex;align-items:center;gap:12px}
.sb-avatar{width:32px;height:32px;border-radius:8px;background:var(--gai);display:flex;align-items:center;justify-content:center;color:#fff;font-size:13px;font-weight:700}
.sb-user-info{flex:1;min-width:0}
.sb-user-name{font-size:13px;font-weight:600;color:rgba(255,255,255,.9)}
.sb-user-plan{font-size:11px;color:rgba(255,255,255,.4)}
.sb-logout{width:28px;height:28px;background:none;border:none;border-radius:6px;display:flex;align-items:center;justify-content:center;color:rgba(255,255,255,.35);cursor:pointer;transition:all .15s;flex-shrink:0}
.sb-logout:hover{background:rgba(254,44,85,.15);color:var(--cp-400)}
.sb-logout svg{width:14px;height:14px}
</style>
