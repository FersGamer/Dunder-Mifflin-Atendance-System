<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-surface min-h-screen pb-20 md:pb-0">

      <!-- TopAppBar -->
      <header
        class="flex justify-between items-center w-full px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="md:hidden font-headline-md text-headline-md text-primary uppercase tracking-tighter">Dunder Mifflin
        </div>
        <div class="hidden md:flex flex-col">
          <h2 class="font-headline-md text-headline-md text-primary">Buenos días, {{ nombreUsuario }}.</h2>
          <p class="font-body-sm text-body-sm text-on-surface-variant capitalize">{{ fechaHoy }}</p>
        </div>
        <div class="relative group">
          <RouterLink to="/notificaciones" @click="marcarComoVistas"
            class="relative flex items-center justify-center text-primary hover:bg-surface-container-high transition-colors p-2 rounded-full cursor-pointer"
            :class="{ 'animate-pulse shadow-[0_0_15px_rgba(220,38,38,0.5)] bg-red-50/50': debaBrillar }">
            <span class="material-symbols-outlined">notifications</span>

            <span v-if="alertas.length > 0"
              class="absolute top-1 right-1 flex items-center justify-center w-4 h-4 bg-status-absence text-white text-[9px] font-bold rounded-full border-2 border-surface shadow-sm">
              {{ alertas.length }}
            </span>

            <span v-if="debaBrillar"
              class="absolute top-1 right-1 w-4 h-4 bg-status-absence rounded-full animate-ping opacity-75 pointer-events-none">
            </span>
          </RouterLink>

          <div
            class="absolute right-0 mt-2 w-72 bg-surface border border-outline-variant shadow-[2px_2px_0_0_rgba(140,140,140,1)] rounded hidden group-hover:block z-50">
            <div
              class="p-3 border-b border-outline-variant font-label-caps text-label-caps text-primary bg-surface-container-lowest">
              Notificaciones Recientes
            </div>

            <div class="max-h-64 overflow-y-auto">
              <template v-if="alertas.length > 0">
                <div v-for="alerta in alertas" :key="alerta.id"
                  class="p-3 border-b border-outline-variant hover:bg-surface-container-low transition-colors">
                  <div class="flex items-start gap-2">
                    <span class="material-symbols-outlined text-base mt-0.5"
                      :class="alerta.color === 'status-delay' ? 'text-status-delay' : 'text-primary'">
                      {{ alerta.icono }}
                    </span>
                    <div>
                      <p class="font-label-caps text-[10px] uppercase"
                        :class="alerta.color === 'status-delay' ? 'text-status-delay' : 'text-primary'">
                        {{ alerta.titulo }}
                      </p>
                      <p class="font-body-sm text-body-sm text-on-surface leading-tight mt-1">
                        {{ alerta.descripcion }}
                      </p>
                    </div>
                  </div>
                </div>
              </template>
              <div v-else class="p-4 text-center">
                <p class="font-body-sm text-body-sm text-on-surface-variant">No hay notificaciones nuevas.</p>
              </div>
            </div>
          </div>
        </div>
      </header>

      <div class="p-8 max-w-container-max mx-auto space-y-8">

        <!-- Memorandum -->
        <section
          class="bg-surface-container-low border border-outline-variant p-6 rounded relative shadow-[2px_2px_0_0_rgba(140,140,140,1)]">
          <div
            class="absolute top-0 left-0 bg-secondary-fixed text-on-secondary-fixed font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">
            MEMORANDUM DIARIO</div>
          <div class="mt-4 pl-4 border-l-2 border-primary">
            <p class="font-headline-lg text-headline-lg text-primary italic">"¿Preferiría ser temido o amado? Fácil.
              Ambos. Quiero que la gente tenga miedo de cuánto me aman."</p>
            <p class="font-body-md text-body-md text-on-surface-variant mt-2">— Michael Scott</p>
          </div>
        </section>

        <!-- KPI Cards -->
        <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div
            class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div
              class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
              PERSONAL</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Total Empleados
                </p>
                <p class="font-display-lg text-display-lg text-primary">{{ loading ? '—' : stats.total }}</p>
              </div>
              <span class="material-symbols-outlined text-outline text-3xl">groups</span>
            </div>
          </div>
          <div
            class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div
              class="absolute top-0 left-0 bg-status-punctual text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">
              COMPLIANCE</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Puntuales</p>
                <p class="font-display-lg text-display-lg text-status-punctual">{{ loading ? '—' : stats.puntuales }}
                </p>
              </div>
              <span class="material-symbols-outlined text-status-punctual text-3xl">check_circle</span>
            </div>
          </div>
          <div
            class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div
              class="absolute top-0 left-0 bg-status-delay text-on-background font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">
              PENDING</div>
            <div class="mt-6 flex justify-between items-end">
              <div>
                <p class="font-body-sm text-body-sm text-on-surface-variant uppercase tracking-wider">Retrasados</p>
                <p class="font-display-lg text-display-lg text-status-delay">{{ loading ? '—' : stats.retrasados }}</p>
              </div>
              <span class="material-symbols-outlined text-status-delay text-3xl">schedule</span>
            </div>
          </div>
          <div
            class="bg-surface border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div
              class="absolute top-0 left-0 bg-status-absence text-on-primary font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br">
              ACTION REQ</div>
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
          <div
            class="lg:col-span-1 bg-surface border border-outline-variant p-6 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative flex flex-col items-center justify-center">
            <div
              class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
              DESGLOSE DE ASISTENCIA</div>

            <!-- SVG Donut -->
            <div class="mt-6 relative">
              <svg viewBox="0 0 200 200" width="200" height="200" class="transform -rotate-90">
                <!-- Track -->
                <circle cx="100" cy="100" r="70" fill="none" stroke="#e7e2da" stroke-width="24" />

                <!-- Ausentes (rojo) — se dibuja primero, es la base -->
                <circle cx="100" cy="100" r="70" fill="none" stroke="#DC2626" stroke-width="24"
                  stroke-dasharray="439.82" stroke-dashoffset="0" stroke-linecap="butt" />
                <!-- Retrasados (amarillo) -->
                <circle cx="100" cy="100" r="70" fill="none" stroke="#F59E0B" stroke-width="24"
                  :stroke-dasharray="`${retrasadosArc} 439.82`" :stroke-dashoffset="`${-puntualOffset}`"
                  stroke-linecap="butt" />
                <!-- Puntuales (verde) -->
                <circle cx="100" cy="100" r="70" fill="none" stroke="#16A34A" stroke-width="24"
                  :stroke-dasharray="`${puntualArc} 439.82`" stroke-dashoffset="0" stroke-linecap="butt" />
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
          <div
            class="lg:col-span-2 bg-surface border border-outline-variant p-6 rounded shadow-[2px_2px_0_0_rgba(140,140,140,1)] relative">
            <div
              class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
              REPORTE POR DEPARTAMENTO</div>
            <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div v-if="loading" class="col-span-2 text-center font-body-sm text-body-sm text-on-surface-variant py-8">
                <span class="material-symbols-outlined animate-spin text-3xl block mb-2">autorenew</span>
                Cargando datos...
              </div>
              <RouterLink v-for="dept in departamentos" :key="dept.id" :to="`/departamentos/${dept.id}`"
                class="block border border-outline-variant p-4 rounded hover:bg-surface-container-low hover:border-primary transition-all cursor-pointer no-underline group">
                <div class="flex justify-between items-center mb-2">
                  <h3 class="font-label-caps text-label-caps text-primary group-hover:underline">{{ dept.nombre }}</h3>
                  <span class="px-2 py-0.5 rounded font-label-caps text-[10px] text-white"
                    :class="dept.puntualidad >= 80 ? 'bg-status-punctual' : dept.puntualidad >= 50 ? 'bg-status-delay' : 'bg-status-absence'">
                    {{ dept.puntualidad }}%
                  </span>
                </div>
                <div class="w-full bg-surface-variant h-2 rounded overflow-hidden mb-2">
                  <div class="h-full rounded transition-all duration-700"
                    :class="dept.puntualidad >= 80 ? 'bg-status-punctual' : dept.puntualidad >= 50 ? 'bg-status-delay' : 'bg-status-absence'"
                    :style="`width: ${dept.puntualidad}%`"></div>
                </div>
                <p class="font-body-sm text-body-sm text-on-surface-variant">{{ dept.nota }}</p>
              </RouterLink>
            </div>
          </div>
        </section>

        <!-- HR Notice -->
        <div class="pt-4 border-t border-outline-variant text-right">
          <p class="font-memo-mono text-memo-mono text-on-surface-variant">HR-Notice: Su nivel de productividad matutina
            ha sido catalogado como "Adecuado". Proceda con el protocolo de café estándar.</p>
        </div>
      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { supabase } from '@/lib/supabase'
import SideNavBar from '@/components/SideNavBar.vue'
import BottomNavBar from '@/components/BottomNavBar.vue'

const loading = ref(true)
const departamentos = ref([])
const alertas = ref([])
const stats = ref({ total: 0, puntuales: 0, retrasados: 0, ausentes: 0 })
const nombreUsuario = ref('Michael')
const notificacionesLeidas = ref(false)

const debaBrillar = computed(() => {
  return alertas.value.length > 0 && !notificacionesLeidas.value
})

function marcarComoVistas() {
  notificacionesLeidas.value = true
  if (alertas.value.length > 0) {
    localStorage.setItem('ultima_alerta_leida', alertas.value[0].id)
  }
}

const fechaHoy = new Date().toLocaleDateString('es-MX', {
  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
})

// ── Donut chart ───────────────────────────────────────────────────────────────
const CIRCUNFERENCIA = 2 * Math.PI * 70

const totalRegistros = computed(() =>
  stats.value.puntuales + stats.value.retrasados + stats.value.ausentes
)

const eficiencia = computed(() => {
  if (stats.value.total === 0) return 0
  return Math.round((stats.value.puntuales / stats.value.total) * 100)
})

const puntualArc = computed(() => {
  if (stats.value.total === 0) return 0
  return (stats.value.puntuales / stats.value.total) * CIRCUNFERENCIA
})

const retrasadosArc = computed(() => {
  if (stats.value.total === 0) return 0
  return (stats.value.retrasados / stats.value.total) * CIRCUNFERENCIA
})

const puntualOffset = computed(() => puntualArc.value)

let suscripcion;
let canalAlertas = null

// ── Helpers ───────────────────────────────────────────────────────────────────
function formatFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha + 'T12:00:00').toLocaleDateString('es-MX', {
    month: 'short', day: 'numeric'
  })
}

// ── Data fetching ─────────────────────────────────────────────────────────────
onMounted(async () => {
  await Promise.all([cargarDatos(), cargarAlertas()])

  // 3. Destruir cualquier conexión residual previa (vital para Vite/HMR)
  if (canalAlertas) {
    await supabase.removeChannel(canalAlertas)
  }

  // 4. Crear el canal
  canalAlertas = supabase.channel('dashboard-alertas')

  // 5. Encadenar todos los .on() PRIMERO
  canalAlertas
    .on('postgres_changes', { event: 'INSERT', schema: 'public', table: 'asistencias' }, () => {
      notificacionesLeidas.value = false
      cargarDatos()
      cargarAlertas()
    })
    .on('postgres_changes', { event: 'INSERT', schema: 'public', table: 'solicitudes_ausencia' }, () => {
      cargarAlertas()
    })

  // 6. Ejecutar el .subscribe() AL FINAL de la cadena
  canalAlertas.subscribe()
})

onUnmounted(() => {
  if (canalAlertas) {
    supabase.removeChannel(canalAlertas)
  }
})

async function cargarDatos() {
  loading.value = true
  const hoy = new Date().toLocaleDateString('en-CA');

  // Total empleados de la empresa
  const { count: totalEmpleados } = await supabase
    .from('empleado')
    .select('*', { count: 'exact', head: true })

  stats.value.total = totalEmpleados ?? 0

  // Asistencias del día
  const { data: asistencias } = await supabase
    .from('asistencias')
    .select('estado')
    .eq('fecha', hoy)

  if (asistencias) {
    const puntuales = asistencias.filter(a => a.estado === 'activo').length
    const retrasados = asistencias.filter(a => a.estado === 'leve_retraso').length
    const faltas = asistencias.filter(a => a.estado === 'falta').length
    // Ausentes = faltas explícitas + los que no tienen registro aún
    const sinRegistro = (totalEmpleados ?? 0) - asistencias.length
    stats.value.puntuales = puntuales
    stats.value.retrasados = retrasados
    stats.value.ausentes = faltas + Math.max(0, sinRegistro)
  }

  // Departamentos con porcentaje sobre total real de empleados
  const { data: depts } = await supabase
    .from('departamento')
    .select(`
      id_departamento, nombre_departamento,
      empleado (
        id_empleado, nombres, apellido_paterno,
        asistencias ( estado, fecha )
      )
    `)

  if (depts) {
    departamentos.value = depts.map(dept => {
      const empleados = dept.empleado || []
      const totalDept = empleados.length  // total real del depto

      const asistenciasHoy = empleados.flatMap(emp =>
        (emp.asistencias ?? []).filter(a => a.fecha === hoy)
      )

      const puntuales = asistenciasHoy.filter(a => a.estado === 'activo').length

      // Puntualidad = puntuales / total empleados del depto
      const puntualidad = totalDept > 0
        ? Math.round((puntuales / totalDept) * 100) : 0

      const retrasados = empleados
        .filter(emp => (emp.asistencias ?? []).some(a =>
          a.fecha === hoy && a.estado === 'leve_retraso'
        ))
        .map(e => `${e.nombres} ${e.apellido_paterno}`)

      const ausentes = empleados
        .filter(emp => {
          const tieneRegistro = (emp.asistencias ?? []).some(a => a.fecha === hoy)
          const tieneFalta = (emp.asistencias ?? []).some(a => a.fecha === hoy && a.estado === 'falta')
          return !tieneRegistro || tieneFalta
        })
        .map(e => `${e.nombres} ${e.apellido_paterno}`)

      let nota = totalDept === 0 ? 'Sin empleados.'
        : ausentes.length > 0
          ? `Ausente(s): ${ausentes.slice(0, 2).join(', ')}${ausentes.length > 2 ? '...' : ''}.`
          : retrasados.length > 0
            ? `Tarde(s): ${retrasados.slice(0, 2).join(', ')}${retrasados.length > 2 ? '...' : ''}.`
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
}

async function cargarAlertas() {
  const hoy = new Date().toLocaleDateString('en-CA');

  const { data: incidencias, error } = await supabase
    .from('asistencias')
    .select(`
    id_asistencias, 
    estado, 
    estatus, 
    hora_entrada, 
    hora_salida,
    empleado ( nombres, apellido_paterno, foto_url )
  `)
    .eq('fecha', hoy)
    // Aquí ocurre la magia: Busca problemas en la entrada OR problemas en la salida
    .or('estado.in.(leve_retraso),estatus.in.(Salida Anticipada)')
    .order('id_asistencias', { ascending: false })
    .limit(5);

  if (error) {
    console.error("Error al cargar incidencias:", error.message);
  }

  const { data: solicitudes } = await supabase
    .from('solicitudes_ausencia')
    .select(`
      id_ausencia, fecha_inicio, fecha_fin,
      empleado ( nombres, apellido_paterno )
    `)
    .eq('aprobacion', 'Pendiente')
    .order('fecha_solicitud', { ascending: false })
    .limit(5)

  const lista = []

  if (error) {
    console.error('Error al cargar incidencias:', error)
    return
  } else if (incidencias) {
    incidencias.forEach(a => {
      const emp = a.empleado
      const nombre = emp ? `${emp.nombres} ${emp.apellido_paterno}` : 'Empleado'
      lista.push({
        id: `asist_${a.id_asistencias}`,
        icono: a.estatus?.includes('Salida') ? 'logout' : 'schedule',
        color: 'status-delay',
        titulo: a.estatus === 'Salida Anticipada' ? 'Salida anticipada' : 'Entrada tardía',
        descripcion: `${nombre} — ${a.hora_entrada || a.hora_salida || ''}`,
        foto: emp?.foto_url || null
      })
    })
  }

  if (solicitudes) {
    solicitudes.forEach(s => {
      const emp = s.empleado
      const nombre = emp ? `${emp.nombres} ${emp.apellido_paterno}` : 'Empleado'
      lista.push({
        id: `solic_${s.id_ausencia}`,
        icono: 'event_note',
        color: 'primary',
        titulo: 'Solicitud de permiso',
        descripcion: `${nombre} — ${formatFecha(s.fecha_inicio)}`,
        foto: null
      })
    })
  }

  alertas.value = lista

  // NUEVA LÓGICA DE CONTROL DE BRILLO:
  if (lista.length > 0) {
    const idMasReciente = lista[0].id
    const ultimoIdLeido = localStorage.getItem('ultima_alerta_leida')

    // Si el ID más reciente coincide con el guardado, significa que ya vio estas alertas
    if (idMasReciente === ultimoIdLeido) {
      notificacionesLeidas.value = true
    } else {
      notificacionesLeidas.value = false
    }
  } else {
    notificacionesLeidas.value = true
  }
}
</script>