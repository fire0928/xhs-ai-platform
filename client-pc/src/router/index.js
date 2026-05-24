import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue'), meta: { fullScreen: true } },
  { path: '/dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
  { path: '/ai-create', name: 'AiCreate', component: () => import('@/views/AiCreate.vue') },
  { path: '/review', name: 'Review', component: () => import('@/views/Review.vue') },
  { path: '/queue', name: 'Queue', component: () => import('@/views/Queue.vue') },
  { path: '/analytics', name: 'Analytics', component: () => import('@/views/Analytics.vue') },
  { path: '/accounts', name: 'Accounts', component: () => import('@/views/Accounts.vue') },
  { path: '/membership', name: 'Membership', component: () => import('@/views/Membership.vue') },
  { path: '/orders', name: 'Orders', component: () => import('@/views/Orders.vue') },
  { path: '/creation-records', name: 'CreationRecords', component: () => import('@/views/CreationRecords.vue') },
  { path: '/content-edit/:id', name: 'ContentEdit', component: () => import('@/views/ContentEdit.vue') },
  { path: '/messages', name: 'MessageCenter', component: () => import('@/views/MessageCenter.vue') },
  { path: '/settings', name: 'Settings', component: () => import('@/views/Settings.vue') },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
