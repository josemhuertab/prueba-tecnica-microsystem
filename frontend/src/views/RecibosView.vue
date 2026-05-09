<template>
  <div class="page-recibos">
    <NavBar />

    <main class="container">

      <!-- Encabezado de sección -->
      <div class="section-header">
        <div>
          <h2>Mis Recibos de Pago</h2>
          <p class="subtitle">Últimos 20 recibos disponibles · {{ recibosFiltrados.length }} resultado(s)</p>
        </div>
      </div>

      <!-- =============================================
           PANEL DE FILTROS
           Para agregar un filtro nuevo: añade un ref,
           un campo en el template y una condición en
           la computed "recibosFiltrados".
           ============================================= -->
      <div class="filtros-panel card">
        <div class="filtros-grid">

          <!-- Buscador por período -->
          <div class="form-group">
            <label>Buscar período</label>
            <input
              v-model="filtros.periodo"
              type="text"
              placeholder="Ej: 04-2025"
            />
          </div>

          <!-- Filtro por año -->
          <div class="form-group">
            <label>Año</label>
            <select v-model="filtros.anio">
              <option value="">Todos</option>
              <option v-for="anio in aniosDisponibles" :key="anio" :value="anio">{{ anio }}</option>
            </select>
          </div>

          <!-- Filtro sueldo líquido mínimo -->
          <div class="form-group">
            <label>Sueldo líquido mínimo</label>
            <input
              v-model.number="filtros.sueldoMin"
              type="number"
              placeholder="Ej: 900000"
              min="0"
            />
          </div>

          <!-- Botón limpiar filtros -->
          <div class="form-group form-group-action">
            <label>&nbsp;</label>
            <button class="btn btn-ghost" @click="limpiarFiltros">
              ✕ Limpiar filtros
            </button>
          </div>

        </div>
      </div>

      <!-- Estado de carga -->
      <div v-if="loading" class="spinner"></div>

      <!-- Error -->
      <div v-else-if="error" class="error-msg">{{ error }}</div>

      <!-- Tabla de recibos -->
      <div v-else class="card tabla-card">

        <div v-if="recibosFiltrados.length === 0" class="empty-state">
          <span class="empty-icon">📄</span>
          <p>No se encontraron recibos con los filtros aplicados.</p>
          <button class="btn btn-ghost" @click="limpiarFiltros">Limpiar filtros</button>
        </div>

        <div v-else class="table-wrapper">
          <table>
            <thead>
              <tr>
                <!-- Para agregar columna: añade <th> aquí y <td> en el tbody -->
                <th @click="ordenarPor('nroRecibo')" class="th-sortable">
                  N° Recibo {{ iconOrden('nroRecibo') }}
                </th>
                <th @click="ordenarPor('periodo')" class="th-sortable">
                  Período {{ iconOrden('periodo') }}
                </th>
                <th @click="ordenarPor('fechaPago')" class="th-sortable">
                  Fecha de Pago {{ iconOrden('fechaPago') }}
                </th>
                <th @click="ordenarPor('sueldoBase')" class="th-sortable">
                  Sueldo Base {{ iconOrden('sueldoBase') }}
                </th>
                <th @click="ordenarPor('bonoProduccion')" class="th-sortable">
                  Bono Producción {{ iconOrden('bonoProduccion') }}
                </th>
                <th @click="ordenarPor('sueldoLiquido')" class="th-sortable">
                  Sueldo Líquido {{ iconOrden('sueldoLiquido') }}
                </th>
                <th>Detalle</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="recibo in recibosFiltrados"
                :key="recibo.id"
                @click="verDetalle(recibo.id)"
              >
                <td>
                  <span class="badge-recibo">#{{ recibo.nroRecibo }}</span>
                </td>
                <td>{{ recibo.periodo }}</td>
                <td>{{ formatFecha(recibo.fechaPago) }}</td>
                <td class="monto">{{ formatMoneda(recibo.sueldoBase) }}</td>
                <td class="monto">{{ formatMoneda(recibo.bonoProduccion) }}</td>
                <td class="monto monto-liquido">{{ formatMoneda(recibo.sueldoLiquido) }}</td>
                <td>
                  <button class="btn-detalle" @click.stop="verDetalle(recibo.id)">
                    Ver →
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

      </div>

    </main>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import { getRecibos } from '../services/api.js'

const router = useRouter()
const recibos = ref([])
const loading = ref(true)
const error = ref('')

// ---- Estado de filtros ----
// Para agregar un filtro nuevo: añade una propiedad aquí y una condición en recibosFiltrados
const filtros = reactive({
  periodo:  '',
  anio:     '',
  sueldoMin: null
})

// ---- Ordenamiento ----
const orden = reactive({ campo: 'fechaPago', asc: false })

function ordenarPor(campo) {
  if (orden.campo === campo) {
    orden.asc = !orden.asc
  } else {
    orden.campo = campo
    orden.asc = false
  }
}

function iconOrden(campo) {
  if (orden.campo !== campo) return '↕'
  return orden.asc ? '↑' : '↓'
}

// ---- Años disponibles para el select ----
const aniosDisponibles = computed(() => {
  const set = new Set(recibos.value.map(r => r.fechaPago?.substring(0, 4)).filter(Boolean))
  return [...set].sort((a, b) => b - a)
})

// ---- Recibos filtrados y ordenados ----
const recibosFiltrados = computed(() => {
  let resultado = [...recibos.value]

  // Filtro por período (búsqueda parcial)
  if (filtros.periodo.trim()) {
    resultado = resultado.filter(r =>
      r.periodo?.toLowerCase().includes(filtros.periodo.trim().toLowerCase())
    )
  }

  // Filtro por año
  if (filtros.anio) {
    resultado = resultado.filter(r => r.fechaPago?.startsWith(filtros.anio))
  }

  // Filtro por sueldo mínimo
  if (filtros.sueldoMin != null && filtros.sueldoMin > 0) {
    resultado = resultado.filter(r => r.sueldoLiquido >= filtros.sueldoMin)
  }

  // Ordenamiento
  resultado.sort((a, b) => {
    const va = a[orden.campo] ?? ''
    const vb = b[orden.campo] ?? ''
    const cmp = va < vb ? -1 : va > vb ? 1 : 0
    return orden.asc ? cmp : -cmp
  })

  return resultado
})

function limpiarFiltros() {
  filtros.periodo  = ''
  filtros.anio     = ''
  filtros.sueldoMin = null
}

onMounted(async () => {
  try {
    const response = await getRecibos()
    recibos.value = response.data
  } catch {
    error.value = 'No se pudieron cargar los recibos. Intenta nuevamente.'
  } finally {
    loading.value = false
  }
})

function verDetalle(id) {
  router.push(`/recibos/${id}`)
}

// Formato moneda CLP sin decimales
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
.page-recibos {
  min-height: 100vh;
  background: var(--color-bg);
}

.container {
  max-width: 1140px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* Encabezado */
.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-family: var(--font-heading);
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--color-primary);
  letter-spacing: -0.3px;
}

.subtitle {
  color: var(--color-text-muted);
  font-size: 0.85rem;
  margin-top: 3px;
}

/* Panel de filtros */
.filtros-panel {
  margin-bottom: 20px;
  padding: 20px 24px;
}

.filtros-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  align-items: end;
}

.form-group {
  margin-bottom: 0;
}

.form-group-action {
  display: flex;
  flex-direction: column;
}

/* Tabla */
.tabla-card {
  padding: 0;
  overflow: hidden;
}

.th-sortable {
  cursor: pointer;
  user-select: none;
  transition: filter 0.2s ease;
}

.th-sortable:hover {
  filter: brightness(1.18);
}

.badge-recibo {
  display: inline-block;
  background: var(--color-surface-2);
  color: var(--color-primary);
  border: 1px solid var(--color-border);
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.monto-liquido {
  color: var(--color-success) !important;
  font-size: 0.95rem;
}

.btn-detalle {
  background: transparent;
  border: 1px solid var(--color-border);
  color: var(--color-primary);
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s;
}

.btn-detalle:hover {
  background: var(--color-primary);
  color: #fff;
  border-color: var(--color-primary);
}

/* Estado vacío */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 20px;
  color: var(--color-text-muted);
}

.empty-icon { font-size: 2.5rem; }

@media (max-width: 768px) {
  .filtros-grid { grid-template-columns: 1fr 1fr; }
  .container { padding: 20px 16px; }
}

@media (max-width: 480px) {
  .filtros-grid { grid-template-columns: 1fr; }
}
</style>
