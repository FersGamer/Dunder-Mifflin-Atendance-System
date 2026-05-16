<template>
  <div class="flex min-h-screen bg-background">
    <SideNavBar />

    <main class="flex-1 md:ml-64 p-gutter pb-24 md:pb-gutter space-y-gutter">

      <!-- Header -->
      <header class="flex items-center justify-between pt-4">
        <div>
          <p class="font-label-caps text-label-caps text-on-surface-variant uppercase tracking-wider">
            {{ fechaHoy }}
          </p>
          <h2 class="font-headline-lg text-headline-lg text-primary">Good Morning, Scranton.</h2>
        </div>
        <button class="flex items-center gap-2 bg-surface border border-outline-variant px-4 py-2 rounded font-label-caps text-label-caps text-on-surface-variant hover:bg-surface-container transition-colors">
          <span class="material-symbols-outlined text-base">filter_list</span>
          Filtrar
        </button>
      </header>

      <!-- Tarjetas de estadísticas -->
      <section class="grid grid-cols-1 sm:grid-cols-3 gap-gutter">

        <!-- Puntuales -->
        <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
          <div class="absolute top-0 left-0 bg-status-punctual text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">ON TIME</div>
          <div class="mt-6 flex justify-between items-end">
            <div>
              <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Puntuales</p>
              <p class="font-display-lg text-display-lg text-status-punctual">{{ stats.puntuales }}</p>
            </div>
            <span class="material-symbols-outlined text-status-punctual text-3xl">check_circle</span>
          </div>
        </div>

        <!-- Retrasados -->
        <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
          <div class="absolute top-0 left-0 bg-status-delay text-on-background font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">PENDING</div>
          <div class="mt-6 flex justify-between items-end">
            <div>
              <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Retrasados</p>
              <p class="font-display-lg text-display-lg text-status-delay">{{ stats.retrasados }}</p>
            </div>
            <span class="material-symbols-outlined text-status-delay text-3xl">schedule</span>
          </div>
        </div>

        <!-- Ausentes -->
        <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
          <div class="absolute top-0 left-0 bg-status-absence text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">ACTION REQ</div>
          <div class="mt-6 flex justify-between items-end">
            <div>
              <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Ausentes</p>
              <p class="font-display-lg text-display-lg text-status-absence">{{ stats.ausentes }}</p>
            </div>
            <span class="material-symbols-outlined text-status-absence text-3xl">warning</span>
          </div>
        </div>

      </section>

      <!-- Reporte por departamento -->
      <section class="bg-surface border border-outline-variant p-6 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
        <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
          REPORTE POR DEPARTAMENTO
        </div>
        <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-if="loading" class="col-span-2 text-center font-body-sm text-body-sm text-on-surface-variant py-8">
            Cargando datos...
          </div>
          <div v-for="dept in departamentos" :key="dept.id" class="border border-outline p-3 rounded">
            <div class="flex justify-between items-center mb-2">
              <h3 class="font-label-caps text-label-caps text-primary">{{ dept.nombre }}</h3>
              <span class="px-2 py-0.5 rounded font-label-caps text-[10px] text-on-primary"
                :class="dept.puntualidad >= 80 ? 'bg-status-punctual' : dept.puntualidad >= 50 ? 'bg-status-delay' : 'bg-status-absence'">
                Puntualidad {{ dept.puntualidad }}%
              </span>
            </div>
            <div class="w-full bg-surface-variant h-2 rounded overflow-hidden">
              <div class="bg-status-punctual h-full transition-all" :style="`width: ${dept.puntualidad}%`"></div>
            </div>
            <p class="font-body-sm text-body-sm mt-2 text-on-surface-variant">{{ dept.nota }}</p>
          </div>
        </div>
      </section>

      <!-- HR Notice -->
      <div class="pt-4 border-t border-outline-variant text-right">
        <p class="font-memo-mono text-memo-mono text-on-surface-variant">
          HR-Notice: Su nivel de productividad matutina ha sido catalogado como "Adecuado". Proceda con el protocolo de café estándar.
        </p>
      </div>
    </main>

    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const loading = ref(true)
const departamentos = ref([])
const stats = ref({ puntuales: 0, retrasados: 0, ausentes: 0 })

const fechaHoy = new Date().toLocaleDateString('es-MX', {
  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
})

onMounted(async () => {
  await cargarDatos()
})

async function cargarDatos() {
  loading.value = true
  const hoy = new Date().toISOString().split('T')[0]

  // 1. Estadísticas del día usando los valores reales del enum estado_asistencia
  const { data: asistencias } = await supabase
    .from('asistencias')
    .select('estado')
    .eq('fecha', hoy)

  if (asistencias) {
    stats.value = {
      puntuales:  asistencias.filter(a => a.estado === 'activo').length,
      retrasados: asistencias.filter(a => a.estado === 'leve_retraso').length,
      ausentes:   asistencias.filter(a => a.estado === 'falta').length,
    }
  }

  // 2. Departamentos con asistencias del día
  const { data: depts } = await supabase
    .from('departamento')
    .select(`
      id_departamento,
      nombre_departamento,
      empleado (
        id_empleado,
        nombres,
        apellido_paterno,
        asistencias (
          estado,
          estatus,
          fecha
        )
      )
    `)

  if (depts) {
    departamentos.value = depts.map(dept => {
      const asistenciasHoy = dept.empleado.flatMap(emp =>
        (emp.asistencias || []).filter(a => a.fecha === hoy)
      )

      const total = asistenciasHoy.length
      const puntuales = asistenciasHoy.filter(a => a.estado === 'activo').length
      const puntualidad = total > 0 ? Math.round((puntuales / total) * 100) : 0

      // Empleados con retraso hoy
      const retrasados = dept.empleado
        .filter(emp => emp.asistencias.some(a => a.fecha === hoy && a.estado === 'leve_retraso'))
        .map(emp => `${emp.nombres} ${emp.apellido_paterno}`)

      // Empleados con falta hoy
      const ausentes = dept.empleado
        .filter(emp => emp.asistencias.some(a => a.fecha === hoy && a.estado === 'falta'))
        .map(emp => `${emp.nombres} ${emp.apellido_paterno}`)

      let nota = 'Todos en cumplimiento.'
      if (ausentes.length > 0) nota = `Ausente(s): ${ausentes.join(', ')}.`
      else if (retrasados.length > 0) nota = `Tarde(s): ${retrasados.join(', ')}.`
      else if (total === 0) nota = 'Sin registros hoy.'

      return {
        id: dept.id_departamento,
        nombre: dept.nombre_departamento,
        puntualidad,
        nota
      }
    })
  }

  loading.value = false
}
</script>