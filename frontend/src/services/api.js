import axios from 'axios'

// Instancia de axios con la URL base del backend
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

// Antes de cada petición: adjunta el JWT si existe en localStorage
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Después de cada respuesta: si el backend devuelve 401 en rutas protegidas,
// limpiamos la sesión y redirigimos al login.
// Excluimos /auth/login para que sus errores los maneje el componente directamente.
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

// --- Autenticación ---
export const login = (username, password) =>
  api.post('/auth/login', { username, password })

export const logout = () =>
  api.post('/auth/logout')

// --- Recibos ---
export const getRecibos = () =>
  api.get('/recibos')

export const getReciboDetalle = (id) =>
  api.get(`/recibos/${id}`)

export default api
