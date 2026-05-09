<template>
  <div class="login-page">

    <!-- Botón de tema en esquina superior derecha -->
    <button class="theme-btn" @click="toggleTheme" :title="isDark ? 'Modo claro' : 'Modo oscuro'">
      {{ isDark ? '☀️' : '🌙' }}
    </button>

    <div class="login-card card">

      <!-- Encabezado -->
      <div class="login-header">
        <div class="logo-icon">M</div>
        <h1>Microsystem</h1>
        <p>Portal de Recibos de Pago</p>
      </div>

      <!-- Formulario -->
      <form @submit.prevent="handleLogin" novalidate>

        <div class="form-group">
          <label for="username">Nombre de usuario</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            placeholder="Ej: NombreApellidoEmpresa001"
            :class="{ error: errors.username }"
            autocomplete="username"
          />
          <span v-if="errors.username" class="field-error">{{ errors.username }}</span>
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <div class="input-password">
            <input
              id="password"
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Ingresa tu contraseña"
              :class="{ error: errors.password }"
              autocomplete="current-password"
            />
            <button type="button" class="toggle-pwd" @click="showPassword = !showPassword" :title="showPassword ? 'Ocultar' : 'Mostrar'">
              {{ showPassword ? '🙈' : '👁️' }}
            </button>
          </div>
          <span v-if="errors.password" class="field-error">{{ errors.password }}</span>
        </div>

        <!-- Error del servidor -->
        <div v-if="serverError" class="error-msg">
          ⚠️ {{ serverError }}
        </div>

        <button type="submit" class="btn btn-primary btn-full" :disabled="loading">
          <span v-if="loading" class="loading-dots">Verificando</span>
          <span v-else>Ingresar al portal</span>
        </button>

      </form>

      <!-- Hint de formato -->
      <div class="format-hint">
        <span class="hint-icon">ℹ️</span>
        <div>
          El usuario debe comenzar con mayúscula, contener solo letras y números,
          tener al menos 15 caracteres y terminar con 3 dígitos.
          Ej: <code>NombreApellidoEmpresa001</code>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../services/api.js'
import { useTheme } from '../composables/useTheme.js'

const router = useRouter()
const { isDark, toggleTheme } = useTheme()

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })
const serverError = ref('')
const loading = ref(false)
const showPassword = ref(false)

/*
 * Regex del username según especificación:
 * - Empieza con mayúscula (UpperCamelCase)
 * - Mínimo 15 caracteres en total
 * - Termina con exactamente 3 dígitos
 */
const USERNAME_REGEX = /^[A-Z][a-zA-Z0-9]{11,}[0-9]{3}$/

function validarFormulario() {
  errors.username = ''
  errors.password = ''
  // No limpiamos serverError aquí — se limpia solo al hacer submit exitoso
  let valido = true

  if (!form.username.trim()) {
    errors.username = 'Ingresa tu nombre de usuario.'
    valido = false
  } else if (!USERNAME_REGEX.test(form.username.trim())) {
    errors.username = 'El usuario debe comenzar con mayúscula, tener al menos 15 caracteres y terminar con 3 números. Ej: NombreApellidoEmpresa001'
    valido = false
  }

  if (!form.password.trim()) {
    errors.password = 'Ingresa tu contraseña.'
    valido = false
  }

  return valido
}

async function handleLogin() {
  // Solo limpiamos el error del servidor al intentar de nuevo, no antes
  if (!validarFormulario()) return

  serverError.value = ''
  loading.value = true

  try {
    const response = await login(form.username.trim(), form.password)
    const { token, username } = response.data

    localStorage.setItem('token', token)
    localStorage.setItem('username', username)

    router.push('/recibos')

  } catch (err) {
    // Mostramos el mensaje del backend o uno genérico si no hay conexión
    serverError.value = err.response?.data?.error || 'No se pudo conectar con el servidor. Intenta nuevamente.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #355872 0%, #7AAACE 100%);
  padding: 20px;
  position: relative;
}

[data-theme="dark"] .login-page {
  background: linear-gradient(145deg, #0f1c26 0%, #172535 100%);
}

/* Botón de tema flotante */
.theme-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  cursor: pointer;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.theme-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.05);
}

/* Card */
.login-card {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  border: none;
  box-shadow: var(--shadow-lg);
}

/* Encabezado */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  width: 68px;
  height: 68px;
  background: linear-gradient(135deg, #355872, #7AAACE);
  color: #fff;
  font-family: var(--font-heading);
  font-size: 2rem;
  font-weight: 800;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 6px 20px rgba(53, 88, 114, 0.35);
}

.login-header h1 {
  font-family: var(--font-heading);
  font-size: 1.7rem;
  font-weight: 800;
  color: var(--color-primary);
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.login-header p {
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

/* Input contraseña */
.input-password {
  position: relative;
  display: flex;
}

.input-password input {
  flex: 1;
  padding-right: 44px;
}

.toggle-pwd {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 4px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.toggle-pwd:hover { opacity: 1; }

/* Error de campo */
.field-error {
  font-size: 0.78rem;
  color: var(--color-danger);
  margin-top: 2px;
}

/* Botón submit */
.btn-full {
  width: 100%;
  justify-content: center;
  padding: 13px;
  font-size: 0.95rem;
  margin-top: 4px;
  font-family: var(--font-heading);
  letter-spacing: 0.3px;
}

/* Animación de carga */
.loading-dots::after {
  content: '...';
  animation: dots 1.2s steps(4, end) infinite;
}

@keyframes dots {
  0%, 20%  { content: '.'; }
  40%      { content: '..'; }
  60%, 100%{ content: '...'; }
}

/* Hint de formato */
.format-hint {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  margin-top: 24px;
  padding: 12px 14px;
  background: var(--color-surface-2);
  border-radius: var(--radius);
  font-size: 0.78rem;
  color: var(--color-text-muted);
  border: 1px solid var(--color-border);
  line-height: 1.5;
}

.hint-icon { font-size: 0.9rem; flex-shrink: 0; margin-top: 1px; }

.format-hint code {
  background: var(--color-border);
  padding: 1px 5px;
  border-radius: 4px;
  font-size: 0.82rem;
  color: var(--color-primary);
}
</style>
