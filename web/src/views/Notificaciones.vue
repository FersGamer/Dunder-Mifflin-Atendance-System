<template>
  <div class="flex min-h-screen bg-surface">
    <!-- SideNavBar solo desktop -->
    <SideNavBar />

    <!-- Contenido principal -->
    <main class="flex-1 md:ml-64 flex flex-col min-h-screen pb-20 md:pb-0">

      <!-- TopBar -->
      <header class="flex justify-between items-center w-full px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <span class="font-headline-md text-headline-md text-on-surface">Centro de Operaciones</span>
      </header>

      <!-- Body -->
      <div class="flex-1 p-8 overflow-y-auto max-w-container-max mx-auto w-full">

        <!-- Page header -->
        <div class="mb-8 border-b border-outline-variant pb-4">
          <h2 class="font-headline-lg text-headline-lg text-on-surface mb-1">
            Bandeja de Entrada Administrativa
          </h2>
          <p class="text-body-md text-on-surface-variant">
            Revisión y procesamiento de solicitudes del personal. Forma 34-B requerida para todas las acciones.
          </p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">

          <!-- ── Notificaciones (8 cols) ──────────────────────────── -->
          <div class="lg:col-span-8 flex flex-col gap-3">

            <!-- Filtros -->
            <div class="flex flex-wrap gap-2 mb-2 bg-surface-container-low p-2 rounded-lg border border-outline-variant w-fit">
              <button
                v-for="f in filtros" :key="f.key"
                @click="filtroActivo = f.key"
                :class="[
                  'font-label-caps text-label-caps px-4 py-2 rounded transition-all border tracking-wider',
                  filtroActivo === f.key
                    ? 'bg-primary text-on-primary border-primary shadow-sm'
                    : 'text-on-surface-variant border-transparent hover:bg-surface hover:border-outline-variant hover:text-on-surface'
                ]"
              >
                {{ f.label }}
                <span
                  v-if="f.key === 'solicitudes' && notifStore.pendientesCount > 0"
                  class="ml-1.5 bg-on-primary text-primary px-1.5 py-0.5 rounded-full text-[10px] font-bold"
                  :class="filtroActivo === f.key ? 'bg-white text-primary' : 'bg-primary text-on-primary'"
                >{{ notifStore.pendientesCount }}</span>
              </button>
            </div>

            <!-- Loading -->
            <div v-if="notifStore.loading" class="text-center py-16 text-on-surface-variant">
              <span class="material-symbols-outlined text-5xl animate-spin">autorenew</span>
              <p class="mt-3 text-body-sm">Cargando notificaciones...</p>
            </div>

            <!-- Error -->
            <div v-else-if="notifStore.error" class="bg-error-container text-on-error-container rounded-lg p-4 text-body-sm border border-error/20">
              <span class="material-symbols-outlined align-middle mr-2 text-sm">error</span>
              {{ notifStore.error }}
            </div>

            <!-- Vacío -->
            <div
              v-else-if="notificacionesFiltradas.length === 0"
              class="text-center py-16 text-on-surface-variant border-2 border-dashed border-outline-variant rounded-lg"
            >
              <span class="material-symbols-outlined text-5xl opacity-40">inbox</span>
              <p class="mt-3 text-body-sm">Sin notificaciones en esta categoría.</p>
            </div>

            <!-- Lista -->
            <TransitionGroup name="notif" tag="div" class="flex flex-col gap-3">
              <div
                v-for="notif in notificacionesFiltradas"
                :key="notif.id"
                class="bg-surface border border-outline-variant rounded-lg overflow-hidden flex flex-col sm:flex-row items-stretch hover:shadow-md transition-all duration-200"
                :class="notif.aprobacion && notif.aprobacion !== 'Pendiente' ? 'opacity-60' : ''"
              >
                <!-- Barra lateral de color -->
                <div class="w-full sm:w-1.5 h-1.5 sm:h-auto shrink-0" :class="colorBorde(notif.color)"></div>

                <!-- Contenido -->
                <div class="flex flex-1 gap-4 p-5">
                  <!-- Ícono -->
                  <div class="p-2.5 rounded-lg h-fit shrink-0" :class="colorIconoBg(notif.color)">
                    <span class="material-symbols-outlined text-[22px]">{{ notif.icono }}</span>
                  </div>

                  <!-- Info -->
                  <div class="flex-1 min-w-0">
                    <div class="flex justify-between items-center mb-2 gap-2 flex-wrap">
                      <span class="font-label-caps text-[11px] tracking-wider bg-surface-container-high px-2.5 py-1 rounded text-on-surface border border-outline-variant">
                        {{ notif.etiqueta }}
                      </span>
                      <span class="font-mono text-label-caps text-on-surface-variant shrink-0">{{ notif.horaTexto }}</span>
                    </div>

                    <h3 class="font-bold text-on-surface text-body-md leading-tight mb-1">{{ notif.titulo }}</h3>
                    <p class="text-[13px] text-on-surface-variant leading-relaxed">{{ notif.descripcion }}</p>

                    <!-- Badge estado resuelto -->
                    <div v-if="notif.tipo === 'solicitud' && notif.aprobacion !== 'Pendiente'" class="mt-3">
                      <span
                        class="font-label-caps text-[11px] tracking-wider px-3 py-1.5 rounded-full border"
                        :class="badgeAprobacion(notif.aprobacion)"
                      >
                        {{ labelAprobacion(notif.aprobacion) }}
                      </span>
                    </div>

                    <!-- Acciones solicitud pendiente -->
                    <div v-if="notif.tipo === 'solicitud' && notif.aprobacion === 'Pendiente'" class="mt-4 flex flex-wrap gap-2">
                      <button
                        @click="responder(notif, 'Aprobada')"
                        :disabled="procesando === notif.id"
                        class="font-label-caps text-[11px] tracking-wider bg-primary text-on-primary px-4 py-2 rounded-lg border border-primary hover:bg-primary-container hover:text-on-primary-container transition-colors disabled:opacity-50 flex items-center gap-1.5"
                      >
                        <span class="material-symbols-outlined text-body-md">check_circle</span>
                        Aprobar
                      </button>
                      <button
                        @click="responder(notif, 'Cancelada')"
                        :disabled="procesando === notif.id"
                        class="font-label-caps text-[11px] tracking-wider bg-surface text-on-surface px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container-high transition-colors disabled:opacity-50 flex items-center gap-1.5"
                      >
                        <span class="material-symbols-outlined text-body-md">schedule</span>
                        Cancelar
                      </button>
                      <button
                        @click="responder(notif, 'Rechazada')"
                        :disabled="procesando === notif.id"
                        class="font-label-caps text-[11px] tracking-wider bg-surface text-error px-4 py-2 rounded-lg border border-outline-variant hover:bg-error-container transition-colors disabled:opacity-50 flex items-center gap-1.5"
                      >
                        <span class="material-symbols-outlined text-body-md">cancel</span>
                        Rechazar
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </TransitionGroup>
          </div>

          <!-- ── Reportes (4 cols) ───────────────────────────────── -->
          <div class="lg:col-span-4">
            <div class="bg-secondary-fixed border border-outline-variant rounded-lg p-6 shadow-sm sticky top-24">
              <div class="flex items-center gap-2 mb-6 border-b border-outline-variant pb-3">
                <span class="material-symbols-outlined text-primary">folder_open</span>
                <h3 class="font-headline-md text-headline-md text-on-surface">Generación de Reportes</h3>
              </div>

              <div class="space-y-5">
                <!-- Departamento -->
                <div>
                  <label class="block font-label-caps text-[11px] tracking-wider text-on-surface-variant mb-1.5">Departamento</label>
                  <select
                    v-model="form.departamento"
                    class="w-full bg-surface border border-outline-variant rounded-lg text-body-sm focus:border-primary focus:outline-none h-10 px-3"
                  >
                    <option value="todos">Todos los Departamentos</option>
                    <option v-for="d in departamentos" :key="d" :value="d">{{ d }}</option>
                  </select>
                </div>

                <!-- Fechas -->
                <div class="grid grid-cols-2 gap-3">
                  <div>
                    <label class="block font-label-caps text-[11px] tracking-wider text-on-surface-variant mb-1.5">Fecha Inicio</label>
                    <input v-model="form.fechaInicio" type="date" class="w-full bg-surface border border-outline-variant rounded-lg text-body-sm focus:border-primary focus:outline-none h-10 px-2" />
                  </div>
                  <div>
                    <label class="block font-label-caps text-[11px] tracking-wider text-on-surface-variant mb-1.5">Fecha Fin</label>
                    <input v-model="form.fechaFin" type="date" class="w-full bg-surface border border-outline-variant rounded-lg text-body-sm focus:border-primary focus:outline-none h-10 px-2" />
                  </div>
                </div>

                <!-- Tipo -->
                <div>
                  <label class="block font-label-caps text-[11px] tracking-wider text-on-surface-variant mb-2">Tipo de Reporte</label>
                  <div class="space-y-2.5">
                    <label v-for="t in tiposReporte" :key="t.value" class="flex items-center gap-3 cursor-pointer group">
                      <input v-model="form.tipo" :value="t.value" type="radio" class="text-primary focus:ring-primary" />
                      <span class="text-[14px] text-on-surface group-hover:text-primary transition-colors">{{ t.label }}</span>
                    </label>
                  </div>
                </div>

                <!-- Botón -->
                <div class="pt-2 border-t border-outline-variant">
                  <button
                    @click="exportar"
                    :disabled="reportesStore.generando"
                    class="w-full font-label-caps text-[11px] tracking-wider bg-primary text-on-primary border-2 border-primary py-2.5 px-4 rounded-lg hover:bg-primary-container hover:text-on-primary-container transition-colors flex items-center justify-center gap-2 disabled:opacity-50"
                  >
                    <span class="material-symbols-outlined text-sm" :class="{ 'animate-spin': reportesStore.generando }">
                      {{ reportesStore.generando ? 'autorenew' : 'download' }}
                    </span>
                    {{ reportesStore.generando ? 'Generando...' : 'Exportar a CSV' }}
                  </button>

                  <p v-if="feedbackExport" class="mt-2 text-label-caps text-center font-medium" :class="feedbackExport.ok ? 'text-status-punctual' : 'text-error'">
                    {{ feedbackExport.texto }}
                  </p>
                </div>
              </div>

              <!-- Aviso HR -->
              <div class="mt-5 p-3 bg-surface-container-low border border-outline-variant rounded">
                <p class="font-mono text-[11px] text-on-surface-variant leading-relaxed">
                  HR-Notice: La falsificación de reportes de asistencia constituye una violación directa de las políticas de la empresa y resultará en acciones disciplinarias severas.
                </p>
              </div>
            </div>
          </div>

        </div>
      </div>
    </main>

    <!-- BottomNavBar solo mobile -->
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useNotificaciones } from '@/stores/notificaciones'
import { useReportes }       from '@/stores/reportes'
import SideNavBar  from '@/components/SideNavBar.vue'
import BottomNavBar from '@/components/BottomNavBar.vue'

const notifStore    = useNotificaciones()
const reportesStore = useReportes()

// ── Notificaciones ────────────────────────────────────────────────────────────
const filtroActivo = ref('todos')
const procesando   = ref(null)

const filtros = [
  { key: 'todos',       label: 'Todos' },
  { key: 'entradas',    label: 'Entradas' },
  { key: 'salidas',     label: 'Salidas' },
  { key: 'solicitudes', label: 'Solicitudes' },
]

const notificacionesFiltradas = computed(() => notifStore.porFiltro(filtroActivo.value))

async function responder(notif, decision) {
  procesando.value = notif.id
  await notifStore.responderSolicitud(notif, decision)
  procesando.value = null
}

// ── Reportes ──────────────────────────────────────────────────────────────────
const form = ref({ departamento: 'todos', fechaInicio: '', fechaFin: '', tipo: 'general' })
const tiposReporte = [
  { value: 'general',     label: 'Asistencia General' },
  { value: 'excepciones', label: 'Excepciones y Retardos' },
  { value: 'solicitudes', label: 'Solicitudes de Ausencia' },
]
const departamentos  = ref(['Ventas', 'Contabilidad', 'Recepción', 'Almacén'])
const feedbackExport = ref(null)

async function exportar() {
  feedbackExport.value = null
  const ok = await reportesStore.exportarCSV(form.value)
  feedbackExport.value = ok
    ? { ok: true,  texto: `✓ ${reportesStore.ultimoTotal} registros exportados.` }
    : { ok: false, texto: reportesStore.error ?? 'Error al exportar.' }
  setTimeout(() => { feedbackExport.value = null }, 4000)
}

// ── Helpers visuales ──────────────────────────────────────────────────────────
const colorBorde = c => ({
  punctual: 'bg-status-punctual',
  delay:    'bg-status-delay',
  absence:  'bg-status-absence',
}[c] ?? 'bg-outline-variant')

const colorIconoBg = c => ({
  punctual: 'bg-surface-container-high text-on-surface',
  delay:    'bg-secondary-container text-on-secondary-container',
  absence:  'bg-error-container text-on-error-container',
}[c] ?? 'bg-surface-container-high text-on-surface')

const badgeAprobacion = ap => ({
  Aprobada:  'bg-green-50 text-green-700 border-green-200',
  Rechazada: 'bg-red-50 text-red-700 border-red-200',
  Cancelada: 'bg-surface-container-high text-on-surface-variant border-outline-variant',
}[ap] ?? '')

const labelAprobacion = ap => ({
  Aprobada:  '✓ Aprobada',
  Rechazada: '✗ Rechazada',
  Cancelada: '— Cancelada',
}[ap] ?? ap)

// ── Lifecycle ─────────────────────────────────────────────────────────────────
onMounted(() => notifStore.inicializar())
onUnmounted(() => notifStore.limpiar())
</script>

<style scoped>
.notif-enter-active { transition: all 0.3s ease; }
.notif-leave-active { transition: all 0.2s ease; }
.notif-enter-from   { opacity: 0; transform: translateY(-10px); }
.notif-leave-to     { opacity: 0; transform: translateX(16px); }
.notif-move         { transition: transform 0.3s ease; }
</style>