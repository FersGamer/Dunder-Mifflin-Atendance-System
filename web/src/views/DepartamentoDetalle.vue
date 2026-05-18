<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <header class="flex items-center justify-between px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="flex items-center gap-4">
          <button @click="router.back()" class="text-on-surface-variant hover:text-primary transition-colors">
            <span class="material-symbols-outlined">arrow_back</span>
          </button>
          <h2 class="font-headline-md text-headline-md text-primary">{{ dept?.nombre_departamento || 'Departamento' }}</h2>
        </div>
        <button class="bg-primary text-on-primary font-label-caps text-label-caps px-4 py-2 hover:bg-primary-container hover:text-on-primary-container transition-colors shadow-[2px_2px_0_0_#8C8C8C]">
          Clock Out
        </button>
      </header>

      <div v-if="loading" class="text-center py-24 font-body-sm text-body-sm text-on-surface-variant">
        Cargando departamento...
      </div>

      <div v-else class="max-w-6xl mx-auto px-8 py-8">

        <!-- Breadcrumbs -->
        <nav class="flex items-center gap-2 mb-6 text-on-surface-variant font-label-caps text-label-caps">
          <RouterLink to="/dashboard" class="hover:text-primary transition-colors">Dashboard</RouterLink>
          <span class="material-symbols-outlined text-body-md">chevron_right</span>
          <RouterLink to="/departamentos" class="hover:text-primary transition-colors">Departamentos</RouterLink>
          <span class="material-symbols-outlined text-body-md">chevron_right</span>
          <span class="text-primary font-bold">{{ dept?.nombre_departamento }}</span>
        </nav>

        <!-- Header del departamento -->
        <div class="flex flex-col md:flex-row gap-gutter items-start md:items-center justify-between mb-8 p-8 bg-surface-container border border-outline-variant rounded relative overflow-hidden shadow-[2px_2px_0_0_#8C8C8C]">
          <div class="absolute top-0 left-0 bg-secondary-fixed px-4 py-1 border-b border-r border-outline-variant rounded-br font-label-caps text-label-caps text-on-secondary-fixed-variant">
            FILE: DEP-0{{ route.params.id }}
          </div>
          <div class="mt-6 md:mt-4">
            <h2 class="font-display-lg text-display-lg text-primary mb-2">{{ dept?.nombre_departamento }}</h2>
            <p class="font-body-md text-body-md text-on-surface-variant max-w-xl">
              Gestión de personal, seguimiento de asistencia y cumplimiento de horarios del equipo.
            </p>
          </div>

          <!-- Mini ring chart -->
          <div class="flex items-center gap-gutter bg-surface p-gutter rounded border border-outline-variant shadow-[2px_2px_0_0_#8C8C8C]">
            <div class="relative w-24 h-24 flex items-center justify-center">
              <svg class="w-full h-full transform -rotate-90 absolute" viewBox="0 0 36 36">
                <circle class="stroke-surface-container-high" cx="18" cy="18" fill="none" r="16" stroke-dasharray="100 100" stroke-width="4"/>
                <circle class="stroke-status-punctual" cx="18" cy="18" fill="none" r="16"
                  :stroke-dasharray="`${stats.pctPuntuales} 100`" stroke-width="4"/>
                <circle class="stroke-status-delay" cx="18" cy="18" fill="none" r="16"
                  :stroke-dasharray="`${stats.pctRetrasados} 100`"
                  :stroke-dashoffset="`-${stats.pctPuntuales}`" stroke-width="4"/>
                <circle class="stroke-status-absence" cx="18" cy="18" fill="none" r="16"
                  :stroke-dasharray="`${stats.pctAusentes} 100`"
                  :stroke-dashoffset="`-${stats.pctPuntuales + stats.pctRetrasados}`" stroke-width="4"/>
              </svg>
              <div class="flex flex-col items-center">
                <span class="font-headline-md text-headline-md text-primary">{{ stats.pctPuntuales }}%</span>
              </div>
            </div>
            <div>
              <h3 class="font-label-caps text-label-caps text-on-surface-variant mb-2">Estado Hoy</h3>
              <ul class="flex flex-col gap-1">
                <li class="flex items-center gap-2 font-body-sm text-body-sm">
                  <span class="w-3 h-3 rounded-full bg-status-punctual"></span> Puntuales: {{ stats.puntuales }}
                </li>
                <li class="flex items-center gap-2 font-body-sm text-body-sm">
                  <span class="w-3 h-3 rounded-full bg-status-delay"></span> Retrasos: {{ stats.retrasados }}
                </li>
                <li class="flex items-center gap-2 font-body-sm text-body-sm">
                  <span class="w-3 h-3 rounded-full bg-status-absence"></span> Ausencias: {{ stats.ausentes }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Controles -->
        <div class="flex flex-col md:flex-row gap-gutter mb-gutter justify-between items-end">
          <div class="w-full md:w-96 relative">
            <label class="block font-label-caps text-label-caps text-on-surface-variant mb-1">Buscar Empleado</label>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
              <input v-model="busqueda" class="w-full pl-10 pr-4 py-2 bg-surface border-b-2 border-outline-variant focus:border-primary focus:outline-none focus:ring-0 font-body-md transition-colors"
                placeholder="Nombre..." type="text"/>
            </div>
          </div>
          <div class="flex gap-2 overflow-x-auto pb-2 md:pb-0">
            <button v-for="filtro in filtros" :key="filtro.valor" @click="filtroActivo = filtro.valor"
              class="whitespace-nowrap px-4 py-2 border font-label-caps text-label-caps rounded transition-colors shadow-[2px_2px_0_0_#8C8C8C]"
              :class="filtroActivo === filtro.valor
                ? 'border-primary bg-primary-container text-on-primary-container'
                : 'border-outline-variant bg-surface text-on-surface-variant hover:bg-surface-container-low'">
              {{ filtro.label }}
            </button>
          </div>
        </div>

        <!-- Tabla de empleados -->
        <div class="bg-surface border border-outline-variant rounded overflow-hidden shadow-[2px_2px_0_0_#8C8C8C]">
          <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
              <thead>
                <tr class="bg-surface-container-low border-b border-outline-variant">
                  <th class="px-6 py-3 font-label-caps text-label-caps text-on-surface-variant">ID</th>
                  <th class="px-6 py-3 font-label-caps text-label-caps text-on-surface-variant">Empleado</th>
                  <th class="px-6 py-3 font-label-caps text-label-caps text-on-surface-variant hidden md:table-cell">Turno</th>
                  <th class="px-6 py-3 font-label-caps text-label-caps text-on-surface-variant">Estado Hoy</th>
                  <th class="px-6 py-3 font-label-caps text-label-caps text-on-surface-variant w-12"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="empleadosFiltrados.length === 0">
                  <td colspan="5" class="px-6 py-12 text-center font-body-sm text-body-sm text-on-surface-variant">
                    No se encontraron empleados.
                  </td>
                </tr>
                <tr v-for="emp in empleadosFiltrados" :key="emp.id_empleado"
                  @click="router.push(`/empleado/${emp.id_empleado}`)"
                  class="border-b border-outline-variant hover:bg-surface-container-lowest transition-colors group cursor-pointer">
                  <td class="px-6 py-4 text-outline font-memo-mono text-sm">{{ emp.id_empleado }}</td>
                  <td class="px-6 py-4">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded border border-outline-variant overflow-hidden bg-surface-variant flex items-center justify-center shrink-0">
                        <img v-if="emp.foto_url" :src="emp.foto_url" :alt="emp.nombres"
                          class="w-full h-full object-cover grayscale group-hover:grayscale-0 transition-all"/>
                        <span v-else class="font-headline-md text-primary text-sm">
                          {{ emp.nombres[0] }}{{ emp.apellido_paterno[0] }}
                        </span>
                      </div>
                      <span class="font-bold text-primary group-hover:underline">
                        {{ emp.nombres }} {{ emp.apellido_paterno }}
                      </span>
                    </div>
                  </td>
                  <td class="px-6 py-4 text-on-surface-variant font-body-sm text-body-sm hidden md:table-cell">
                    {{ emp.turno || '—' }}
                  </td>
                  <td class="px-6 py-4">
                    <div v-if="emp.estadoHoy" class="inline-flex items-center gap-1.5 px-2 py-1 rounded font-label-caps text-label-caps border"
                      :class="{
                        'bg-status-punctual/10 text-status-punctual border-status-punctual/30': emp.estadoHoy === 'activo',
                        'bg-status-delay/10 text-status-delay border-status-delay/30': emp.estadoHoy === 'leve_retraso',
                        'bg-status-absence/10 text-status-absence border-status-absence/30': emp.estadoHoy === 'falta',
                      }">
                      <span class="material-symbols-outlined text-[14px]">
                        {{ emp.estadoHoy === 'activo' ? 'check_circle' : emp.estadoHoy === 'leve_retraso' ? 'schedule' : 'warning' }}
                      </span>
                      {{ emp.estadoHoy === 'activo' ? 'En Cumplimiento' : emp.estadoHoy === 'leve_retraso' ? 'Retraso' : 'Ausencia' }}
                    </div>
                    <span v-else class="font-body-sm text-body-sm text-on-surface-variant">Sin registro</span>
                  </td>
                  <td class="px-6 py-4">
                    <span class="material-symbols-outlined text-on-surface-variant group-hover:text-primary transition-colors">chevron_right</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="px-6 py-3 bg-surface-container-low border-t border-outline-variant flex justify-between items-center">
            <span class="font-label-caps text-label-caps text-on-surface-variant">
              {{ empleadosFiltrados.length }} de {{ empleados.length }} empleados
            </span>
          </div>
        </div>

        <!-- HR Notice -->
        <div class="mt-8 border-t border-outline-variant pt-4 flex items-center gap-2 text-on-surface-variant opacity-70">
          <span class="material-symbols-outlined text-body-lg">info</span>
          <p class="font-memo-mono text-memo-mono">HR-Notice: Su eficiencia ha sido notada. Las ausencias no justificadas requieren la Forma 302-B.</p>
        </div>

      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const dept = ref(null)
const empleados = ref([])
const busqueda = ref('')
const filtroActivo = ref('todos')
const stats = ref({ puntuales: 0, retrasados: 0, ausentes: 0, pctPuntuales: 0, pctRetrasados: 0, pctAusentes: 0 })

const filtros = [
  { valor: 'todos', label: 'Todos' },
  { valor: 'activo', label: 'Puntuales' },
  { valor: 'leve_retraso', label: 'Retrasos' },
  { valor: 'falta', label: 'Ausencias' },
]

const empleadosFiltrados = computed(() => {
  let lista = empleados.value
  if (busqueda.value) {
    const q = busqueda.value.toLowerCase()
    lista = lista.filter(e => `${e.nombres} ${e.apellido_paterno}`.toLowerCase().includes(q))
  }
  if (filtroActivo.value !== 'todos') {
    lista = lista.filter(e => e.estadoHoy === filtroActivo.value)
  }
  return lista
})

onMounted(async () => {
  const id = route.params.id
  const hoy = new Date().toISOString().split('T')[0]

  // Cargar departamento
  const { data: deptData } = await supabase
    .from('departamento')
    .select('id_departamento, nombre_departamento')
    .eq('id_departamento', id)
    .single()

  if (deptData) dept.value = deptData

  // Cargar empleados del departamento
  const { data: emps } = await supabase
    .from('empleado')
    .select(`
      id_empleado, nombres, apellido_paterno, foto_url,
      horario ( turno, hora_entrada, hora_salida ),
      asistencias ( estado, fecha )
    `)
    .eq('id_departamento', id)

  if (emps) {
    empleados.value = emps.map(emp => {
      const asistenciaHoy = (emp.asistencias || []).find(a => a.fecha === hoy)
      const hor = emp.horario?.[0]
      return {
        ...emp,
        estadoHoy: asistenciaHoy?.estado || null,
        turno: hor ? `${hor.hora_entrada} - ${hor.hora_salida}` : null
      }
    })

    const conAsistencia = empleados.value.filter(e => e.estadoHoy)
    const total = conAsistencia.length
    const p = conAsistencia.filter(e => e.estadoHoy === 'activo').length
    const r = conAsistencia.filter(e => e.estadoHoy === 'leve_retraso').length
    const a = conAsistencia.filter(e => e.estadoHoy === 'falta').length

    stats.value = {
      puntuales: p, retrasados: r, ausentes: a,
      pctPuntuales:  total > 0 ? Math.round((p / total) * 100) : 0,
      pctRetrasados: total > 0 ? Math.round((r / total) * 100) : 0,
      pctAusentes:   total > 0 ? Math.round((a / total) * 100) : 0,
    }
  }

  loading.value = false
})
</script>