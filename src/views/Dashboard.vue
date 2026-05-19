<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-surface min-h-screen pb-20 md:pb-0">

      <!-- TopAppBar -->
      <header class="flex justify-between items-center w-full px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="md:hidden font-headline-md text-headline-md text-primary uppercase tracking-tighter">Dunder Mifflin</div>
        <div class="hidden md:flex flex-col">
          <h2 class="font-headline-md text-headline-md text-primary">Buenos días, {{ nombreUsuario }}.</h2>
          <p class="font-body-sm text-body-sm text-on-surface-variant capitalize">{{ fechaHoy }}</p>
        </div>
        <div class="flex items-center gap-4">
          <div class="relative group cursor-pointer">
            <span class="material-symbols-outlined text-primary hover:bg-surface-container-high transition-colors p-2 rounded-full">notifications</span>
            <div class="absolute right-0 mt-2 w-64 bg-surface border border-outline-variant shadow-[2px_2px_0_0_rgba(140,140,140,1)] rounded hidden group-hover:block z-50">
              <div class="p-3 border-b border-outline-variant font-label-caps text-label-caps text-primary">Notificaciones Recientes</div>
              <div class="p-3 border-b border-outline-variant hover:bg-surface-container-low">
                <p class="font-body-sm text-body-sm">Jim H. ha registrado un retraso (12 min).</p>
              </div>
              <div class="p-3 hover:bg-surface-container-low">
                <p class="font-body-sm text-body-sm">Pam B. solicita material de oficina.</p>
              </div>
            </div>
          </div>
        </div>
      </header>

      <div class="p-8 max-w-container-max mx-auto space-y-8">

        <!-- Memorandum -->
        <section class="bg-surface-container-low border border-outline-variant p-6 rounded relative shadow-[2px_2px_0_0_rgba(140,140,140,1)]">
          <div class="absolute top-0 left-0 bg-secondary-fixed text-on-secondary-fixed font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">MEMORANDUM DIARIO</div>
          <div class="mt-4 pl-4 border-l-2 border-primary">
            <p class="font-headline-lg text-headline-lg text-primary italic">"¿Preferiría ser temido o amado? Fácil. Ambos. Quiero que la gente tenga miedo de cuánto me aman."</p>
            <p class="font-body-md text-body-md text-on-surface-variant mt-2">— Michael Scott</p>
          </div>
        </section>

        <!-- KPI Cards -->
        <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">PERSONAL</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Total Empleados</p>
                <p class="font-display-lg text-display-lg text-primary">{{ loading ? '—' : stats.total }}</p>
              </div>
              <span class="material-symbols-outlined text-outline text-3xl">groups</span>
            </div>
          </div>
          <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div class="absolute top-0 left-0 bg-status-punctual text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">COMPLIANCE</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Puntuales</p>
                <p class="font-display-lg text-display-lg text-status-punctual">{{ loading ? '—' : stats.puntuales }}</p>
              </div>
              <span class="material-symbols-outlined text-status-punctual text-3xl">check_circle</span>
            </div>
          </div>
          <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div class="absolute top-0 left-0 bg-status-delay text-on-background font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">PENDING</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Retrasados</p>
                <p class="font-display-lg text-display-lg text-status-delay">{{ loading ? '—' : stats.retrasados }}</p>
              </div>
              <span class="material-symbols-outlined text-status-delay text-3xl">schedule</span>
            </div>
          </div>
          <div class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div class="absolute top-0 left-0 bg-status-absence text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">ACTION REQ</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Ausentes</p>
                <p class="font-display-lg text-display-lg text-status-absence">{{ loading ? '—' : stats.ausentes }}</p>
              </div>
              <span class="material-symbols-outlined text-status-absence text-3xl">warning</span>
            </div>
          </div>
        </section>

        <!-- Gráfico + Departamentos -->
        <section class="grid grid-cols-1 lg:grid-cols-3 gap-6">

          <!-- Donut Chart -->
          <div class="lg:col-span-1 bg-surface border border-outline-variant p-6 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative flex flex-col items-center justify-center">
            <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">DESGLOSE DE ASISTENCIA</div>

            <!-- SVG Donut -->
            <div class="mt-6 relative">
              <svg viewBox="0 0 200 200" width="200" height="200" class="transform -rotate-90">
                <!-- Track -->
                <circle cx="100" cy="100" r="70" fill="none" stroke="#e7e2da" stroke-width="24" />

                <!-- Ausentes (rojo) — se dibuja primero, es la base -->
                <circle
                  cx="100" cy="100" r="70" fill="none"
                  stroke="#DC2626" stroke-width="24"
                  stroke-dasharray="439.82"
                  stroke-dashoffset="0"
                  stroke-linecap="butt"
                />
                <!-- Retrasados (amarillo) -->
                <circle
                  cx="100" cy="100" r="70" fill="none"
                  stroke="#F59E0B" stroke-width="24"
                  :stroke-dasharray="`${retrasadosArc} 439.82`"
                  :stroke-dashoffset="`${-puntualOffset}`"
                  stroke-linecap="butt"
                />
                <!-- Puntuales (verde) -->
                <circle
                  cx="100" cy="100" r="70" fill="none"
                  stroke="#16A34A" stroke-width="24"
                  :stroke-dasharray="`${puntualArc} 439.82`"
                  stroke-dashoffset="0"
                  stroke-linecap="butt"
                />
              </svg>

              <!-- Centro -->
              <div class="absolute inset-0 flex flex-col items-center justify-center">
                <span class="font-headline-lg text-headline-lg text-primary leading-none">{{ eficiencia }}%</span>
                <span class="font-label-caps text-label-caps text-on-surface-variant mt-1">EFICIENCIA</span>
              </div>
            </div>

            <!-- Leyenda -->
            <div class="mt-6 flex flex-col gap-2 w-full px-4">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-sm bg-status-punctual"></div>
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Puntuales</span>
                </div>
                <span class="font-bold text-on-surface text-[14px]">{{ stats.puntuales }}</span>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-sm bg-status-delay"></div>
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Retrasados</span>
                </div>
                <span class="font-bold text-on-surface text-[14px]">{{ stats.retrasados }}</span>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-2">
                  <div class="w-3 h-3 rounded-sm bg-status-absence"></div>
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Ausentes</span>
                </div>
                <span class="font-bold text-on-surface text-[14px]">{{ stats.ausentes }}</span>
              </div>
            </div>
          </div>

          <!-- Departamentos -->
          <div class="lg:col-span-2 bg-surface border border-outline-variant p-6 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">REPORTE POR DEPARTAMENTO</div>
            <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div v-if="loading" class="col-span-2 text-center font-body-sm text-body-sm text-on-surface-variant py-8">
                <span class="material-symbols-outlined animate-spin text-3xl block mb-2">autorenew</span>
                Cargando datos...
              </div>
              <div v-for="dept in departamentos" :key="dept.id" class="border border-outline-variant p-4 rounded hover:bg-surface-container-low transition-colors">
                <div class="flex justify-between items-center mb-2">
                  <h3 class="font-label-caps text-label-caps text-primary">{{ dept.nombre }}</h3>
                  <span
                    class="px-2 py-0.5 rounded font-label-caps text-[10px] text-white"
                    :class="dept.puntualidad >= 80 ? 'bg-status-punctual' : dept.puntualidad >= 50 ? 'bg-status-delay' : 'bg-status-absence'"
                  >
                    {{ dept.puntualidad }}%
                  </span>
                </div>
                <div class="w-full bg-surface-variant h-2 rounded overflow-hidden mb-2">
                  <div
                    class="h-full rounded transition-all duration-700"
                    :class="dept.puntualidad >= 80 ? 'bg-status-punctual' : dept.puntualidad >= 50 ? 'bg-status-delay' : 'bg-status-absence'"
                    :style="`width: ${dept.puntualidad}%`"
                  ></div>
                </div>
                <p class="font-body-sm text-body-sm text-on-surface-variant">{{ dept.nota }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- HR Notice -->
        <div class="pt-4 border-t border-outline-variant text-right">
          <p class="font-memo-mono text-memo-mono text-on-surface-variant">HR-Notice: Su nivel de productividad matutina ha sido catalogado como "Adecuado". Proceda con el protocolo de café estándar.</p>
        </div>
      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { supabase } from '@/lib/supabase'
import SideNavBar  from '@/components/SideNavBar.vue'
import BottomNavBar from '@/components/BottomNavBar.vue'

const loading       = ref(true)
const departamentos = ref([])
const stats         = ref({ total: 0, puntuales: 0, retrasados: 0, ausentes: 0 })
const nombreUsuario = ref('Michael')

const fechaHoy = new Date().toLocaleDateString('es-MX', {
  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
})

// ── Donut chart ───────────────────────────────────────────────────────────────
// Circunferencia del círculo r=70: 2π×70 ≈ 439.82
const CIRCUNFERENCIA = 2 * Math.PI * 70

const totalRegistros = computed(() =>
  stats.value.puntuales + stats.value.retrasados + stats.value.ausentes
)

const eficiencia = computed(() => {
  if (totalRegistros.value === 0) return 0
  return Math.round((stats.value.puntuales / totalRegistros.value) * 100)
})

// Arco de puntuales (verde) — empieza en 0
const puntualArc = computed(() => {
  if (totalRegistros.value === 0) return 0
  return (stats.value.puntuales / totalRegistros.value) * CIRCUNFERENCIA
})

// Arco de retrasados (amarillo) — empieza después del verde
const retrasadosArc = computed(() => {
  if (totalRegistros.value === 0) return 0
  return (stats.value.retrasados / totalRegistros.value) * CIRCUNFERENCIA
})

// Offset para que retrasados empiece justo después de puntuales
const puntualOffset = computed(() => puntualArc.value)

// ── Data fetching ─────────────────────────────────────────────────────────────
onMounted(async () => {
  const hoy = new Date().toISOString().split('T')[0]

  // Total empleados
  const { count } = await supabase
    .from('empleado')
    .select('*', { count: 'exact', head: true })
  stats.value.total = count ?? 0

  // Asistencias del día
  const { data: asistencias } = await supabase
    .from('asistencias')
    .select('estado, estatus')
    .eq('fecha', hoy)

  if (asistencias) {
    // Ajusta los valores del enum según tu tabla
    stats.value.puntuales  = asistencias.filter(a =>
      a.estado === 'activo' || a.estatus === 'completo'
    ).length
    stats.value.retrasados = asistencias.filter(a =>
      a.estado === 'leve_retraso' || a.estatus === 'tardanza'
    ).length
    stats.value.ausentes   = asistencias.filter(a =>
      a.estado === 'falta' || a.estatus === 'ausente'
    ).length
  }

  // Departamentos con puntualidad del día
  const { data: depts } = await supabase
    .from('departamento')
    .select(`
      id_departamento,
      nombre_departamento,
      empleado (
        id_empleado,
        nombres,
        apellido_paterno,
        asistencias ( estado, estatus, fecha )
      )
    `)

  if (depts) {
    departamentos.value = depts.map(dept => {
      const asistenciasHoy = dept.empleado.flatMap(emp =>
        (emp.asistencias ?? []).filter(a => a.fecha === hoy)
      )
      const total     = asistenciasHoy.length
      const puntuales = asistenciasHoy.filter(a =>
        a.estado === 'activo' || a.estatus === 'completo'
      ).length
      const puntualidad = total > 0 ? Math.round((puntuales / total) * 100) : 0

      const retrasados = dept.empleado
        .filter(emp => (emp.asistencias ?? []).some(a =>
          a.fecha === hoy && (a.estado === 'leve_retraso' || a.estatus === 'tardanza')
        ))
        .map(e => `${e.nombres} ${e.apellido_paterno}`)

      const ausentes = dept.empleado
        .filter(emp => (emp.asistencias ?? []).some(a =>
          a.fecha === hoy && (a.estado === 'falta' || a.estatus === 'ausente')
        ))
        .map(e => `${e.nombres} ${e.apellido_paterno}`)

      let nota = total === 0
        ? 'Sin registros hoy.'
        : ausentes.length > 0
          ? `Ausente(s): ${ausentes.join(', ')}.`
          : retrasados.length > 0
            ? `Tarde(s): ${retrasados.join(', ')}.`
            : 'Todos en cumplimiento.'

      return {
        id: dept.id_departamento,
        nombre: dept.nombre_departamento,
        puntualidad,
        nota,
      }
    })
  }

  loading.value = false
})
</script>