<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <!-- Header -->
      <header class="flex justify-between items-center px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="flex items-center gap-4">
          <div class="hidden md:flex items-center bg-surface-container-low px-4 py-1.5 border border-outline-variant rounded gap-2">
            <span class="material-symbols-outlined text-on-surface-variant text-[20px]">search</span>
            <input v-model="busqueda" class="bg-transparent border-none focus:ring-0 font-body-sm text-body-sm p-0 w-44 placeholder-outline-variant"
              placeholder="Buscar departamento..." type="text"/>
          </div>
        </div>
      </header>

      <div class="max-w-6xl mx-auto px-8 py-8">

        <!-- Título -->
        <div class="mb-10">
          <span class="font-label-caps text-label-caps text-secondary mb-2 block uppercase tracking-widest">Organización Corporativa</span>
          <h1 class="font-display-lg text-display-lg text-primary">Administración de Departamentos</h1>
          <p class="font-body-md text-body-md text-on-surface-variant max-w-2xl mt-3">
            Supervisión técnica de la fuerza laboral por sectores. Los datos de asistencia reflejan el estado del día actual.
          </p>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-24 font-body-sm text-body-sm text-on-surface-variant">
          Cargando departamentos...
        </div>

        <!-- Grid de departamentos -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-gutter">
          <div v-for="(dept, i) in departamentosFiltrados" :key="dept.id_departamento"
            @click="router.push(`/departamentos/${dept.id_departamento}`)"
            class="bg-surface-container-lowest rounded-xl border border-outline-variant relative group cursor-pointer shadow-[2px_2px_0_0_#8C8C8C] hover:shadow-[4px_4px_0_0_#8C8C8C] transition-all">

            <!-- Tab decorativo -->
            <div class="absolute -top-px left-4 bg-secondary-fixed px-3 py-0.5 border-x border-t border-outline-variant">
              <span class="font-label-caps text-[10px] text-on-secondary-fixed-variant">DM-SEC-0{{ i + 1 }}</span>
            </div>

            <div class="p-8 mt-4">
              <div class="flex justify-between items-start mb-6">
                <div>
                  <h2 class="font-headline-lg text-headline-lg text-primary">{{ dept.nombre_departamento }}</h2>
                  <p class="font-body-sm text-body-sm text-on-surface-variant italic mt-1">
                    {{ dept.empleadoDestacado || 'Sin jefe asignado' }}
                  </p>
                </div>

                <!-- Donut chart -->
                <div class="relative w-16 h-16 shrink-0">
                  <svg class="w-full h-full" viewBox="0 0 36 36">
                    <path class="text-surface-container-high" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none" stroke="currentColor" stroke-width="4"/>
                    <path :class="dept.puntualidad >= 80 ? 'text-status-punctual' : dept.puntualidad >= 50 ? 'text-status-delay' : 'text-status-absence'"
                      d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                      fill="none" stroke="currentColor" :stroke-dasharray="`${dept.puntualidad}, 100`"
                      stroke-linecap="round" stroke-width="4"/>
                  </svg>
                  <div class="absolute inset-0 flex items-center justify-center font-label-caps text-[10px]">
                    {{ dept.puntualidad }}%
                  </div>
                </div>
              </div>

              <div class="space-y-3 pt-4 border-t border-outline-variant/50">
                <div class="flex justify-between items-center">
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Personal</span>
                  <span class="px-2 py-0.5 bg-primary/10 text-primary font-label-caps text-[10px] rounded">
                    {{ dept.totalEmpleados }} empleado{{ dept.totalEmpleados !== 1 ? 's' : '' }}
                  </span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Puntuales hoy</span>
                  <span class="px-2 py-0.5 bg-status-punctual/10 text-status-punctual font-label-caps text-[10px] rounded">
                    {{ dept.puntuales }}
                  </span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="font-label-caps text-label-caps text-on-surface-variant">Ausencias</span>
                  <span v-if="dept.ausentes > 0" class="px-2 py-0.5 bg-status-absence/10 text-status-absence font-label-caps text-[10px] rounded">
                    {{ dept.ausentes }} pendiente{{ dept.ausentes !== 1 ? 's' : '' }}
                  </span>
                  <span v-else class="font-body-sm text-body-sm text-on-surface-variant">Ninguna</span>
                </div>
              </div>

              <div class="mt-6 flex justify-end">
                <span class="material-symbols-outlined text-primary group-hover:translate-x-1 transition-transform">arrow_forward</span>
              </div>
            </div>
          </div>
        </div>

        <!-- HR Notice -->
        <div class="mt-12 bg-surface-container border border-outline border-dashed p-6 flex items-start gap-4">
          <span class="material-symbols-outlined text-secondary">info</span>
          <p class="font-memo-mono text-memo-mono text-on-surface-variant">
            HR-NOTICE: El departamento de "Calidad" (Creed Bratton) no ha sido incluido en este reporte debido a una falla técnica en su servidor o falta de existencia física del mismo. Por favor, proceda con normalidad. Su eficiencia ha sido notada.
          </p>
        </div>

      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const loading = ref(true)
const busqueda = ref('')
const departamentos = ref([])

const departamentosFiltrados = computed(() => {
  if (!busqueda.value) return departamentos.value
  const q = busqueda.value.toLowerCase()
  return departamentos.value.filter(d => d.nombre_departamento.toLowerCase().includes(q))
})

onMounted(async () => {
  const hoy = new Date().toISOString().split('T')[0]

  const { data: depts } = await supabase
    .from('departamento')
    .select(`
      id_departamento, nombre_departamento,
      empleado (
        id_empleado, nombres, apellido_paterno,
        asistencias ( estado, fecha )
      )
    `)
    .order('id_departamento')

  if (depts) {
    departamentos.value = depts.map((dept, i) => {
      const empleados = dept.empleado || []
      const asistenciasHoy = empleados.flatMap(e =>
        (e.asistencias || []).filter(a => a.fecha === hoy)
      )
      const total = empleados.length
      const puntuales = asistenciasHoy.filter(a => a.estado === 'activo').length
      const retrasados = asistenciasHoy.filter(a => a.estado === 'leve_retraso').length
      const ausentes = asistenciasHoy.filter(a => a.estado === 'falta').length
      const puntualidad = asistenciasHoy.length > 0
        ? Math.round((puntuales / asistenciasHoy.length) * 100) : 0

      // Primer empleado como destacado
      const primero = empleados[0]
      const empleadoDestacado = primero
        ? `${primero.nombres} ${primero.apellido_paterno}`
        : null

      return {
        id_departamento: dept.id_departamento,
        nombre_departamento: dept.nombre_departamento,
        totalEmpleados: total,
        puntuales,
        retrasados,
        ausentes,
        puntualidad,
        empleadoDestacado,
        codigo: `DM-SEC-0${i + 1}`
      }
    })
  }

  loading.value = false
})
</script>