<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <div class="brand-logo">M</div>
      <div class="brand-text">
        <span class="brand-name">Microsystem</span>
        <span class="brand-sub">Portal de Recibos</span>
      </div>
    </div>

    <div class="navbar-actions">
      <!-- Botón modo oscuro -->
      <button
        class="theme-toggle"
        :title="isDark ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
        @click="toggleTheme"
        aria-label="Cambiar tema"
      >
        <span class="theme-icon">{{ isDark ? '☀️' : '🌙' }}</span>
      </button>

      <!-- Separador -->
      <div class="nav-divider"></div>

      <!-- Info del usuario -->
      <div class="user-info">
        <div class="user-avatar">{{ inicialUsuario }}</div>
        <span class="user-name">{{ nombreMostrar }}</span>
      </div>

      <!-- Cerrar sesión -->
      <button class="btn-logout" @click="handleLogout">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
          <polyline points="16 17 21 12 16 7"/>
          <line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
        Salir
      </button>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { logout } from '../services/api.js'
import { useTheme } from '../composables/useTheme.js'

const router = useRouter()
const { isDark, toggleTheme } = useTheme()

const nombreMostrar = computed(() => localStorage.getItem('username') || 'Usuario')

// Primera letra del username para el avatar
const inicialUsuario = computed(() => {
  const name = localStorage.getItem('username') || 'U'
  return name.charAt(0).toUpperCase()
})

async function handleLogout() {
  try {
    await logout()
  } catch {
    // Si falla el backend, igual limpiamos la sesión local
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    router.push('/login')
  }
}
</script>

<style scoped>
.navbar {
  background: var(--color-nav-bg);
  color: var(--color-nav-text);
  padding: 0 28px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.18);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(156, 213, 255, 0.12);
}

/* Marca */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #9CD5FF, #7AAACE);
  color: #355872;
  font-family: var(--font-heading);
  font-weight: 800;
  font-size: 1.2rem;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-family: var(--font-heading);
  font-size: 1rem;
  font-weight: 700;
  color: var(--color-nav-text);
  letter-spacing: 0.3px;
}

.brand-sub {
  font-size: 0.7rem;
  opacity: 0.6;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

/* Acciones */
.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Botón tema */
.theme-toggle {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: 1px solid rgba(156, 213, 255, 0.25);
  background: rgba(156, 213, 255, 0.1);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.theme-toggle:hover {
  background: rgba(156, 213, 255, 0.2);
}

.theme-icon {
  font-size: 1rem;
  line-height: 1;
}

.nav-divider {
  width: 1px;
  height: 28px;
  background: rgba(156, 213, 255, 0.2);
}

/* Usuario */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #9CD5FF, #7AAACE);
  color: #355872;
  font-weight: 700;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 500;
  opacity: 0.9;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Botón salir */
.btn-logout {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: 8px;
  border: 1px solid rgba(156, 213, 255, 0.3);
  background: transparent;
  color: var(--color-nav-text);
  font-family: var(--font-body);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  opacity: 0.85;
}

.btn-logout:hover {
  background: rgba(192, 57, 43, 0.2);
  border-color: rgba(192, 57, 43, 0.5);
  opacity: 1;
}

@media (max-width: 600px) {
  .user-name, .brand-sub { display: none; }
  .navbar { padding: 0 16px; }
}
</style>
