import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('@/views/user/UserLayout.vue'),
    meta: { requiresAuth: true, role: 'USER' },
    children: [
      {
        path: '',
        redirect: '/user/dashboard'
      },
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/user/Dashboard.vue'),
        meta: { title: '用户中心' }
      },
      {
        path: 'documents',
        name: 'DocumentList',
        component: () => import('@/views/user/DocumentList.vue'),
        meta: { title: '文档列表' }
      },
      {
        path: 'reader/:id',
        name: 'DocumentReader',
        component: () => import('@/views/user/DocumentReader.vue'),
        meta: { title: '阅读文档' }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/views/user/Settings.vue'),
        meta: { title: '个人设置' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理控制台' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'documents',
        name: 'DocumentManagement',
        component: () => import('@/views/admin/DocumentManagement.vue'),
        meta: { title: '文档管理' }
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: () => import('@/views/admin/Analytics.vue'),
        meta: { title: '数据分析' }
      },
      {
        path: 'accessibility',
        name: 'AccessibilitySettings',
        component: () => import('@/views/admin/AccessibilityAudit.vue'),
        meta: { title: '无障碍审计' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters.isLoggedIn
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiredRole = to.meta.role

  if (requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    if (store.getters.isAdmin) {
      next('/admin/dashboard')
    } else {
      next('/user/dashboard')
    }
  } else if (requiresAuth && isLoggedIn && requiredRole) {
    const userRole = store.getters.currentUser?.role
    if (userRole === requiredRole) {
      next()
    } else {
      if (store.getters.isAdmin) {
        next('/admin/dashboard')
      } else {
        next('/user/dashboard')
      }
    }
  } else {
    next()
  }
})

export default router
