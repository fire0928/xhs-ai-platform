import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/overview' },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/overview', name: 'Overview', component: () => import('@/views/Overview.vue') },
  { path: '/users', name: 'Users', component: () => import('@/views/Users.vue') },
  { path: '/membership', name: 'Membership', component: () => import('@/views/Membership.vue') },
  { path: '/orders', name: 'Orders', component: () => import('@/views/Orders.vue') },
  { path: '/ai-models', name: 'AiModels', component: () => import('@/views/AiModels.vue') },
  { path: '/agents', name: 'Agents', component: () => import('@/views/Agents.vue') },
  { path: '/content-directions', name: 'ContentDirections', component: () => import('@/views/ContentDirections.vue') },
  { path: '/monitor', name: 'Monitor', component: () => import('@/views/Monitor.vue') },
  { path: '/content', name: 'Content', component: () => import('@/views/Content.vue') },
  { path: '/analytics', name: 'Analytics', component: () => import('@/views/Analytics.vue') },
  { path: '/settings', name: 'Settings', component: () => import('@/views/Settings.vue') },
]

const router = createRouter({ history: createWebHashHistory(), routes })

function getRoleFromToken(token) {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.role || ''
  } catch {
    return ''
  }
}

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  const role = token ? getRoleFromToken(token) : ''

  if (!token && to.path !== '/login') {
    next('/login')
  } else if (token && to.path === '/login') {
    if (role === 'ADMIN') {
      next('/overview')
    } else {
      localStorage.removeItem('admin_token')
      next()
    }
  } else if (token && role !== 'ADMIN' && to.path !== '/login') {
    localStorage.removeItem('admin_token')
    next('/login')
  } else {
    next()
  }
})

export default router
