import axios from 'axios'

// Instancia base de axios apuntando al backend
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

// Interceptor: adjunta el JWT en cada request si existe
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Interceptor: si el backend responde 401 en rutas protegidas, limpiamos sesión y redirigimos
// Excluimos /auth/login para que los errores de credenciales los maneje el componente
api.interceptors.response.use(
  response => response,
  error => {
    const esRutaLogin = error.config?.url?.includes('/auth/login')
    if (error.response?.status === 401 && !esRutaLogin) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// ---- Métodos de autenticación ----

export const login = (username, password) =>
  api.post('/auth/login', { username, password })

export const logout = () =>
  api.post('/auth/logout')

// ---- Métodos de recibos ----

export const getRecibos = () =>
  api.get('/recibos')

export const getReciboDetalle = (id) =>
  api.get(`/recibos/${id}`)

export default api
