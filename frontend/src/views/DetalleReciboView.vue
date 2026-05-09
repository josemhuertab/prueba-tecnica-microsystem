<template>
  <div class="page-detalle">
    <NavBar />

    <main class="container">

      <button class="btn btn-ghost btn-back" @click="router.back()">
        ← Volver a mis recibos
      </button>

      <div v-if="loading" class="spinner"></div>

      <div v-else-if="error" class="error-msg">{{ error }}</div>

      <div v-else-if="recibo" class="detalle-wrapper">

        <!-- Encabezado del recibo -->
        <div class="card recibo-header">
          <div class="recibo-titulo">
            <div class="recibo-id">
              <span class="recibo-label">Recibo de Pago</span>
              <span class="recibo-num">#{{ recibo.nroRecibo }}</span>
            </div>
            <span class="badge-periodo">{{ recibo.periodo }}</span>
          </div>
          <div class="recibo-meta">
            <div class="meta-item">
              <span class="meta-label">Fecha de pago</span>
              <span class="meta-value">{{ formatFecha(recibo.fechaPago) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">Detalle</span>
              <span class="meta-value">{{ recibo.detalle }}</span>
            </div>
          </div>
        </div>

        <!-- Grilla haberes / descuentos -->
        <div class="montos-grid">

          <div class="card montos-card">
            <h3 class="card-title">
              <span class="title-dot dot-green"></span>
              Haberes
            </h3>
            <div class="monto-row">
              <span>Sueldo Base</span>
              <span class="monto">{{ formatMoneda(recibo.sueldoBase) }}</span>
            </div>
            <div class="monto-row">
              <span>Bono de Producción</span>
              <span class="monto">{{ formatMoneda(recibo.bonoProduccion) }}</span>
            </div>
            <div class="monto-row total-row">
              <span>Total Haberes</span>
              <span class="monto">{{ formatMoneda(recibo.sueldoBase + recibo.bonoProduccion) }}</span>
            </div>
          </div>

          <div class="card montos-card">
            <h3 class="card-title">
              <span class="title-dot dot-red"></span>
              Descuentos
            </h3>
            <div class="monto-row">
              <span>Salud</span>
              <span class="monto monto-desc">{{ formatMoneda(recibo.descuentoSalud) }}</span>
            </div>
            <div class="monto-row">
              <span>AFP</span>
              <span class="monto monto-desc">{{ formatMoneda(recibo.descuentoAfp) }}</span>
            </div>
            <div class="monto-row">
              <span>Otros Descuentos</span>
              <span class="monto monto-desc">{{ formatMoneda(recibo.otrosDescuentos) }}</span>
            </div>
            <div class="monto-row total-row">
              <span>Total Descuentos</span>
              <span class="monto monto-desc">
                {{ formatMoneda(recibo.descuentoSalud + recibo.descuentoAfp + recibo.otrosDescuentos) }}
              </span>
            </div>
          </div>

        </div>

        <!-- Sueldo líquido -->
        <div class="liquido-card">
          <div class="liquido-info">
            <span class="liquido-label">Sueldo Líquido a Pagar</span>
            <span class="liquido-sub">Período {{ recibo.periodo }}</span>
          </div>
          <span class="liquido-monto">{{ formatMoneda(recibo.sueldoLiquido) }}</span>
        </div>

      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { getReciboDetalle } from '../services/api.js'

const route = useRoute()
const router = useRouter()

const recibo = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const response = await getReciboDetalle(route.params.id)
    recibo.value = response.data
  } catch (err) {
    error.value = err.response?.status === 403
      ? 'No tienes permiso para ver este recibo.'
      : 'No se pudo cargar el detalle del recibo.'
  } finally {
    loading.value = false
  }
})

function formatMoneda(valor) {
  if (valor == null) return '-'
  return new Intl.NumberFormat('es-CL', {
    style: 'currency',
    currency: 'CLP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(valor)
}

function formatFecha(fecha) {
  if (!fecha) return '-'
  const [year, month, day] = fecha.split('-')
  return `${day}/${month}/${year}`
}
</script>

<style scoped>
.page-detalle {
  min-height: 100vh;
  background: var(--color-bg);
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px;
}

.btn-back {
  margin-bottom: 24px;
  font-size: 0.875rem;
}

/* Encabezado */
.recibo-header {
  margin-bottom: 20px;
}

.recibo-titulo {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.recibo-id {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.recibo-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.8px;
  color: var(--color-text-muted);
  font-weight: 600;
}

.recibo-num {
  font-family: var(--font-heading);
  font-size: 1.6rem;
  font-weight: 800;
  color: var(--color-primary);
}

.badge-periodo {
  background: var(--color-primary);
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.recibo-meta {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.meta-label {
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.6px;
  color: var(--color-text-muted);
  font-weight: 600;
}

.meta-value {
  font-size: 0.9rem;
  color: var(--color-text);
  font-weight: 500;
}

/* Grilla */
.montos-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

@media (max-width: 600px) {
  .montos-grid { grid-template-columns: 1fr; }
}

.montos-card { padding: 20px; }

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: var(--font-heading);
  font-size: 0.9rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--color-text-muted);
  margin-bottom: 16px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-border);
}

.title-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dot-green { background: var(--color-success); }
.dot-red   { background: var(--color-danger); }

.monto-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 9px 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
}

.monto-row:last-child { border-bottom: none; }

.total-row {
  border-top: 2px solid var(--color-border);
  border-bottom: none;
  margin-top: 6px;
  padding-top: 12px;
  font-weight: 700;
}

.monto      { font-weight: 600; color: var(--color-primary); font-variant-numeric: tabular-nums; }
.monto-desc { color: var(--color-danger); }

/* Sueldo líquido */
.liquido-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #355872, #7AAACE);
  border-radius: var(--radius-lg);
  padding: 28px 32px;
  flex-wrap: wrap;
  gap: 12px;
  box-shadow: var(--shadow-lg);
}

[data-theme="dark"] .liquido-card {
  background: linear-gradient(135deg, #172535, #1e3045);
  border: 1px solid var(--color-border);
}

.liquido-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.liquido-label {
  font-family: var(--font-heading);
  font-size: 1rem;
  font-weight: 700;
  color: rgba(247, 248, 240, 0.9);
}

.liquido-sub {
  font-size: 0.8rem;
  color: rgba(247, 248, 240, 0.6);
}

.liquido-monto {
  font-family: var(--font-heading);
  font-size: 2.2rem;
  font-weight: 800;
  color: #9CD5FF;
  font-variant-numeric: tabular-nums;
}

@media (max-width: 480px) {
  .liquido-card { flex-direction: column; align-items: flex-start; }
  .liquido-monto { font-size: 1.8rem; }
}
</style>
