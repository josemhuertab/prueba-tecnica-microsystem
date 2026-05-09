import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RecibosView from '../views/RecibosView.vue'
import DetalleReciboView from '../views/DetalleReciboView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    // Si el usuario ya tiene token guardado, lo mando directo a recibos sin pasar por el login
    beforeEnter: (to, from, next) => {
      if (localStorage.getItem('token')) {
        next('/recibos')
      } else {
        next()
      }
    }
  },
  {
    path: '/recibos',
    name: 'Recibos',
    component: RecibosView,
    meta: { requiresAuth: true }  // Marqué esta ruta como protegida
  },
  {
    path: '/recibos/:id',
    name: 'DetalleRecibo',
    component: DetalleReciboView,
    meta: { requiresAuth: true }  // Esta también requiere autenticación
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Guard global que se ejecuta antes de cada navegación.
// Si la ruta requiere auth y no hay token en localStorage, redirijo al login.
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
