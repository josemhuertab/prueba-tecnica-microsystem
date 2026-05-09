import axios from 'axios'

// Creo una instancia de axios apuntando a la URL base del backend
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

// Antes de cada petición adjunto el JWT si existe en localStorage
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Después de cada respuesta verifico si el backend devolvió 401 en rutas protegidas.
// Si es así, limpio la sesión y redirijo al login.
// Excluyo /auth/login para que sus errores los maneje el componente directamente
// y no se produzca una redirección inesperada al intentar iniciar sesión.
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
