<template>
  <div>
    <template v-if="$route.meta.fullScreen">
      <router-view />
    </template>
    <template v-else>
      <div class="app">
        <Sidebar />
        <div class="mc">
          <Topbar />
          <div class="pc">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import Sidebar from '@/components/Sidebar.vue'
import Topbar from '@/components/Topbar.vue'
</script>

<style scoped>
.app { display: flex; height: 100vh; width: 100%; }
.mc { flex: 1; display: flex; flex-direction: column; overflow: hidden; min-width: 0; }
.pc { flex: 1; overflow-y: auto; padding: 32px; }
</style>
