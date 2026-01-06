import { createRouter, createWebHistory } from 'vue-router'
import ChatRoom from '../components/ChatRoom.vue'
import AdminLogin from '../components/AdminLogin.vue'
import AdminDashboard from '../components/AdminDashboard.vue'
import UserLogin from '../components/UserLogin.vue'
import LandingPage from '../components/LandingPage.vue'

const routes = [
  {
    path: '/',
    name: 'LandingPage',
    component: LandingPage
  },
  {
    path: '/chat',
    name: 'ChatRoom',
    component: ChatRoom,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'UserLogin',
    component: UserLogin
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin')) {
    const isAuthenticated = sessionStorage.getItem('adminToken')
    if (to.meta.requiresAuth && !isAuthenticated) {
      next('/admin/login')
    } else {
      next()
    }
  } else {
    const isAuthenticated = sessionStorage.getItem('userToken')
    if (to.meta.requiresAuth && !isAuthenticated) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
