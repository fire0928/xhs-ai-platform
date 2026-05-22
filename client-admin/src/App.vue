<template>
  <div id="admin-app">
    <!-- 登录页全屏显示，不展示侧栏和顶栏 -->
    <template v-if="isLoginPage">
      <router-view v-slot="{ Component }">
        <component :is="Component" />
      </router-view>
    </template>
    <!-- 内部页面显示完整布局 -->
    <template v-else>
      <Sidebar />
      <div class="main-content">
        <Topbar />
        <div class="page-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import Topbar from './components/Topbar.vue'

const route = useRoute()
const isLoginPage = computed(() => route.name === 'Login')
</script>
