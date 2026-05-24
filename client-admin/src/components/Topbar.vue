<template>
  <header class="tb">
    <div class="tb-bc">管理后台 / <span>{{ pageName }}</span></div>
    <div class="tb-right">
      <!-- 搜索框 -->
      <div class="tb-search" :class="{ focused: searchFocused }">
        <svg viewBox="0 0 14 14" fill="none"><circle cx="6" cy="6" r="4.5" stroke="currentColor" stroke-width="1.5"/><path d="M9.5 9.5L13 13" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
        <input type="text" placeholder="搜索用户、内容、Agent..." v-model="searchText" @focus="searchFocused=true" @blur="onBlur" @keydown.enter="doSearch" @input="onSearchInput" />
        <!-- 搜索结果下拉 -->
        <div v-if="searchFocused && searchText.length >= 2" class="search-dropdown">
          <div v-if="searching" style="padding:12px;text-align:center;color:var(--cn-400)">搜索中...</div>
          <template v-else>
            <div v-if="searchResults.users?.length" class="sd-section">
              <div class="sd-title">用户</div>
              <div v-for="u in searchResults.users" :key="'u'+u.id" class="sd-item">{{ u.name }} <span style="font-size:11px;color:var(--cn-400)">{{ u.desc }}</span></div>
            </div>
            <div v-if="searchResults.agents?.length" class="sd-section">
              <div class="sd-title">Agent</div>
              <div v-for="a in searchResults.agents" :key="'a'+a.id" class="sd-item">{{ a.name }} <span class="tag tag-r" style="font-size:10px">{{ a.desc }}</span></div>
            </div>
            <div v-if="searchResults.contents?.length" class="sd-section">
              <div class="sd-title">内容</div>
              <div v-for="c in searchResults.contents" :key="'c'+c.id" class="sd-item">{{ c.name }}</div>
            </div>
            <div v-if="!searchResults.users?.length && !searchResults.agents?.length && !searchResults.contents?.length" style="padding:12px;text-align:center;color:var(--cn-400)">未找到结果</div>
          </template>
        </div>
      </div>
      <!-- 通知铃铛 -->
      <div class="tb-ib-wrap" @click="toggleNotifications">
        <button class="tb-ib">
          <svg viewBox="0 0 18 18" fill="none"><path d="M4 7C4 4.79 6.24 3 9 3C11.76 3 14 4.79 14 7V11L15 14H3L4 11V7Z" stroke="currentColor" stroke-width="1.5" fill="none"/><path d="M7 14C7 15.1 7.9 16 9 16C10.1 16 11 15.1 11 14" stroke="currentColor" stroke-width="1.5"/></svg>
          <span v-if="notifications.length>0" class="dot"></span>
        </button>
        <!-- 通知下拉 -->
        <div v-if="showNotifications" class="notif-dropdown">
          <div style="font-size:13px;font-weight:600;color:var(--cn-800);padding:8px 12px;border-bottom:1px solid var(--cn-100)">通知</div>
          <div v-if="notifications.length===0" style="padding:16px;text-align:center;color:var(--cn-400);font-size:13px">暂无通知</div>
          <div v-else>
            <div v-for="n in notifications" :key="n.time" class="notif-item">
              <div style="font-size:12px;color:var(--cn-700)">{{ n.message }}</div>
              <div style="font-size:10px;color:var(--cn-400);margin-top:2px">{{ n.time?.substring(0,10) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api'

const route = useRoute()
const names = {
  overview:'系统总览',users:'用户管理',membership:'会员管理','ai-models':'AI大模型管理',
  agents:'Agent管理',monitor:'调用监控',content:'内容管理',analytics:'数据分析',settings:'系统设置'
}
const pageName = computed(() => names[route.name?.toLowerCase()] || route.name)

// 搜索
const searchText = ref('')
const searchFocused = ref(false)
const searching = ref(false)
const searchResults = ref({})
let searchTimer = null

function onBlur() { setTimeout(() => searchFocused.value = false, 250) }
function doSearch() {
  if (searchText.value.length < 1) return
  searching.value = true
  api.get('/search', { params: { keyword: searchText.value, limit: 5 } }).then(res => {
    if (res.data.code === 200) searchResults.value = res.data.data || {}
  }).catch(console.error).finally(() => searching.value = false)
}
// 输入时自动搜索
function onSearchInput() {
  if (searchTimer) clearTimeout(searchTimer)
  if (searchText.value.length < 1) { searchResults.value = {}; return }
  searchTimer = setTimeout(doSearch, 300)
}

// 通知
const showNotifications = ref(false)
const notifications = ref([])

function toggleNotifications() {
  showNotifications.value = !showNotifications.value
  if (showNotifications.value) loadNotifications()
}

function loadNotifications() {
  api.get('/notifications', { params: { limit: 10 } }).then(res => {
    if (res.data.code === 200) notifications.value = res.data.data || []
  }).catch(console.error)
}

onMounted(loadNotifications)
</script>

<style scoped>
.tb{height:var(--hh);min-height:var(--hh);background:#fff;border-bottom:1px solid var(--cn-200);display:flex;align-items:center;justify-content:space-between;padding:0 32px}
.tb-bc{font-size:13px;color:var(--cn-400)}
.tb-bc span{color:var(--cn-800);font-weight:500}
.tb-right{display:flex;align-items:center;gap:16px}
.tb-search{position:relative;display:flex;align-items:center;gap:8px;background:var(--cn-100);border-radius:8px;padding:4px 12px;width:240px;border:1.5px solid transparent;transition:border-color .2s}
.tb-search.focused{border-color:var(--cp-500);background:#fff}
.tb-search input{border:none;background:none;outline:none;font-size:13px;color:var(--cn-700);width:100%}
.tb-search svg{width:14px;height:14px;color:var(--cn-400);flex-shrink:0}
.search-dropdown{position:absolute;top:100%;left:0;right:0;margin-top:6px;background:#fff;border-radius:10px;box-shadow:0 8px 30px rgba(0,0,0,.12);border:1px solid var(--cn-200);max-height:360px;overflow-y:auto;z-index:500}
.sd-section{padding:4px 0}
.sd-title{font-size:10px;font-weight:600;color:var(--cn-400);padding:4px 12px;text-transform:uppercase}
.sd-item{padding:6px 12px;font-size:13px;color:var(--cn-700);cursor:pointer}
.sd-item:hover{background:var(--cp-50)}
.tb-ib-wrap{position:relative}
.tb-ib{width:32px;height:32px;display:flex;align-items:center;justify-content:center;border-radius:6px;color:var(--cn-500);position:relative;cursor:pointer;border:none;background:none}
.tb-ib:hover{background:var(--cn-100)}
.tb-ib svg{width:18px;height:18px}
.tb-ib .dot{position:absolute;top:4px;right:4px;width:6px;height:6px;background:var(--cp-500);border-radius:50%;border:1.5px solid #fff}
.notif-dropdown{position:absolute;top:100%;right:0;margin-top:6px;background:#fff;border-radius:10px;box-shadow:0 8px 30px rgba(0,0,0,.12);border:1px solid var(--cn-200);width:280px;max-height:360px;overflow-y:auto;z-index:500}
.notif-item{padding:10px 12px;border-bottom:1px solid var(--cn-50);cursor:pointer}
.notif-item:hover{background:var(--cn-50)}
</style>
