<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <!-- Header -->
      <header class="flex items-center justify-between px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="flex items-center gap-4">
          <button @click="router.back()" class="text-on-surface-variant hover:text-primary transition-colors">
            <span class="material-symbols-outlined">arrow_back</span>
          </button>
          <div>
            <p class="font-label-caps text-label-caps text-on-surface-variant uppercase tracking-wider">EXPEDIENTE PERSONAL</p>
            <h2 v-if="empleado" class="font-headline-md text-headline-md text-primary">
              {{ empleado.nombres }} {{ empleado.apellido_paterno }} {{ empleado.apellido_materno }}
            </h2>
          </div>
        </div>
        <div class="flex gap-3">
          <RouterLink :to="`/expediente/${route.params.id}`"
            class="flex items-center gap-2 px-4 py-2 border border-secondary text-secondary font-label-caps text-label-caps hover:bg-secondary-container transition-colors rounded shadow-[2px_2px_0_0_#8C8C8C]">
            <span class="material-symbols-outlined text-sm">edit</span>
            Editar
          </RouterLink>
          <button @click="mostrarConfirmar = true"
            class="flex items-center gap-2 px-4 py-2 bg-error text-on-error font-label-caps text-label-caps hover:opacity-90 transition-colors rounded shadow-[2px_2px_0_0_#8C8C8C]">
            <span class="material-symbols-outlined text-sm">person_remove</span>
            Dar de Baja
          </button>
        </div>
      </header>

      <div v-if="loading" class="flex items-center justify-center py-24">
        <p class="font-body-sm text-body-sm text-on-surface-variant">Cargando expediente...</p>
      </div>

      <div v-else-if="empleado" class="p-8 max-w-6xl mx-auto">

        <!-- Confirmación baja -->
        <div v-if="mostrarConfirmar" class="mb-6 bg-error-container border border-error p-4 rounded flex items-start justify-between gap-4">
          <div>
            <p class="font-label-caps text-label-caps text-on-error-container uppercase mb-1">⚠ Confirmar Baja</p>
            <p class="font-body-sm text-body-sm text-on-error-container">
              ¿Confirmas dar de baja a <strong>{{ empleado.nombres }} {{ empleado.apellido_paterno }}</strong>? Esta acción no se puede deshacer.
            </p>
          </div>
          <div class="flex gap-2 shrink-0">
            <button @click="darDeBaja"
              class="bg-error text-on-error px-4 py-2 rounded font-label-caps text-label-caps hover:opacity-90">
              Confirmar
            </button>
            <button @click="mostrarConfirmar = false"
              class="border border-outline px-4 py-2 rounded font-label-caps text-label-caps text-on-surface-variant hover:bg-surface-container">
              Cancelar
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter">

          <!-- Tarjeta de identificación -->
          <div class="md:col-span-4 bg-surface-bright border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] relative p-6 flex flex-col">
            <div class="absolute -top-3 left-4 bg-surface-bright border border-outline-variant px-3 py-1 font-label-caps text-label-caps text-on-surface-variant uppercase shadow-[1px_1px_0_0_#8C8C8C]">
              Identificación
            </div>
            <div class="flex flex-col items-center mt-4 gap-4">
              <div class="w-48 h-48 bg-surface-variant border-4 border-surface-container-high overflow-hidden rounded-sm">
                <img v-if="empleado.foto_url" :src="empleado.foto_url" :alt="empleado.nombres"
                  class="w-full h-full object-cover"/>
                <div v-else class="w-full h-full flex items-center justify-center">
                  <span class="material-symbols-outlined text-6xl text-outline">account_circle</span>
                </div>
              </div>
              <div class="w-full space-y-3">
                <div class="border-b border-outline-variant pb-2">
                  <span class="font-label-caps text-label-caps text-on-surface-variant block">Departamento</span>
                  <span class="font-body-md text-body-md font-semibold">{{ empleado.departamento?.nombre_departamento || 'Gerencia' }}</span>
                </div>
                <div class="border-b border-outline-variant pb-2">
                  <span class="font-label-caps text-label-caps text-on-surface-variant block">Correo</span>
                  <span class="font-memo-mono text-memo-mono text-sm">{{ empleado.email }}</span>
                </div>
                <div class="border-b border-outline-variant pb-2">
                  <span class="font-label-caps text-label-caps text-on-surface-variant block">Fecha de Contratación</span>
                  <span class="font-body-md text-body-md">{{ formatFecha(empleado.fecha_contratacion) }}</span>
                </div>
                <div class="border-b border-outline-variant pb-2">
                  <span class="font-label-caps text-label-caps text-on-surface-variant block">Sueldo</span>
                  <span class="font-body-md text-body-md">${{ empleado.sueldo?.toLocaleString() }} MXN</span>
                </div>
                <div v-if="horario">
                  <span class="font-label-caps text-label-caps text-on-surface-variant block">Turno</span>
                  <span class="font-body-md text-body-md">{{ horario.turno }} — {{ horario.hora_entrada }} a {{ horario.hora_salida }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Columna derecha -->
          <div class="md:col-span-8 flex flex-col gap-gutter">

            <!-- Stats -->
            <div class="grid grid-cols-3 gap-gutter">
              <div class="bg-surface-bright border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_#8C8C8C]">
                <span class="font-label-caps text-label-caps text-on-surface-variant flex items-center gap-1 mb-2">
                  <span class="material-symbols-outlined text-sm">how_to_reg</span> Asistencias
                </span>
                <div class="font-headline-lg text-headline-lg text-primary">{{ stats.puntuales }}</div>
                <div class="mt-2 inline-flex items-center gap-1 bg-status-punctual text-on-primary font-label-caps text-[10px] px-2 py-1 rounded">
                  <span class="material-symbols-outlined text-[10px]">check_circle</span> Puntuales
                </div>
              </div>
              <div class="bg-surface-bright border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_#8C8C8C]">
                <span class="font-label-caps text-label-caps text-on-surface-variant flex items-center gap-1 mb-2">
                  <span class="material-symbols-outlined text-sm">schedule</span> Retrasos
                </span>
                <div class="font-headline-lg text-headline-lg text-status-delay">{{ stats.retrasados }}</div>
                <div class="mt-2 inline-flex items-center gap-1 bg-surface-container-high text-on-surface font-label-caps text-[10px] px-2 py-1 rounded border border-outline-variant">
                  <span class="material-symbols-outlined text-[10px]">info</span> Este mes
                </div>
              </div>
              <div class="bg-surface-bright border border-outline-variant p-4 rounded shadow-[2px_2px_0_0_#8C8C8C]">
                <span class="font-label-caps text-label-caps text-on-surface-variant flex items-center gap-1 mb-2">
                  <span class="material-symbols-outlined text-sm">assignment_late</span> Ausencias
                </span>
                <div class="font-headline-lg text-headline-lg text-status-absence">{{ stats.ausentes }}</div>
                <div class="mt-2 inline-flex items-center gap-1 bg-surface-container-high text-on-surface font-label-caps text-[10px] px-2 py-1 rounded border border-outline-variant">
                  <span class="material-symbols-outlined text-[10px]">info</span> Este mes
                </div>
              </div>
            </div>

            <!-- Calendario de asistencias -->
            <div class="bg-surface-bright border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] p-6 relative">
              <div class="absolute -top-3 left-4 bg-surface-bright border border-outline-variant px-3 py-1 font-label-caps text-label-caps text-on-surface-variant uppercase shadow-[1px_1px_0_0_#8C8C8C]">
                Registro de Asistencia — {{ mesActual }}
              </div>
              <div class="flex justify-between items-center mb-4 mt-2">
                <div class="flex flex-wrap gap-4">
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-status-punctual"></div><span class="font-label-caps text-[10px]">Puntual</span></div>
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-status-delay"></div><span class="font-label-caps text-[10px]">Retraso</span></div>
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-status-absence"></div><span class="font-label-caps text-[10px]">Ausencia</span></div>
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-blue-500"></div><span class="font-label-caps text-[10px]">Vacaciones</span></div>
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-purple-500"></div><span class="font-label-caps text-[10px]">Permiso</span></div>
                  <div class="flex items-center gap-1"><div class="w-3 h-3 bg-surface-container-high border border-outline-variant"></div><span class="font-label-caps text-[10px]">Fin de semana</span></div>
                </div>
              </div>
              <div class="grid grid-cols-7 gap-1 text-center">
                <div v-for="dia in ['LUN','MAR','MIÉ','JUE','VIE','SÁB','DOM']" :key="dia"
                  class="font-label-caps text-[10px] text-on-surface-variant py-2 border-b border-outline-variant">
                  {{ dia }}
                </div>
                <div v-for="(dia, i) in calendarioDias" :key="i"
                  class="p-2 border border-outline-variant font-memo-mono text-sm transition-all"
                  :class="dia.clase"
                  :title="dia.tooltip">
                  {{ dia.numero }}
                </div>
              </div>
            </div>

            <!-- Gestión de permisos -->
            <div class="bg-surface-bright border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] p-6 relative">
              <div class="absolute -top-3 left-4 bg-surface-bright border border-outline-variant px-3 py-1 font-label-caps text-label-caps text-on-surface-variant uppercase shadow-[1px_1px_0_0_#8C8C8C]">
                Gestión de Permisos
              </div>
              <div v-if="loadingPermisos" class="text-center py-8 font-body-sm text-body-sm text-on-surface-variant">
                Cargando solicitudes...
              </div>
              <div v-else-if="solicitudes.length === 0" class="text-center py-8">
                <span class="material-symbols-outlined text-4xl text-outline">event_available</span>
                <p class="font-body-sm text-body-sm text-on-surface-variant mt-2">Sin solicitudes de permiso.</p>
                <p class="font-memo-mono text-memo-mono text-on-surface-variant text-xs mt-1">
                  HR-Notice: Hasta Toby aprobaría esto.
                </p>
              </div>
              <div v-else class="mt-4 space-y-3">
                <div v-for="sol in solicitudes" :key="sol.id_ausencia"
                  class="border border-outline-variant rounded p-4 flex flex-col md:flex-row md:items-center justify-between gap-3 hover:bg-surface-container-low transition-colors">
                  <div class="flex gap-4 items-start">
                    <div class="bg-surface-container-highest border border-outline-variant px-3 py-2 text-center min-w-15">
                      <span class="block font-label-caps text-[10px] text-on-surface-variant">{{ formatMes(sol.fecha_inicio) }}</span>
                      <span class="block font-headline-md text-headline-md text-primary">{{ formatDia(sol.fecha_inicio) }}</span>
                    </div>
                    <div>
                      <p class="font-label-caps text-label-caps text-primary uppercase">{{ sol.faltas?.faltas || 'Permiso' }}</p>
                      <p class="font-body-sm text-body-sm text-on-surface-variant">
                        {{ formatFecha(sol.fecha_inicio) }} — {{ formatFecha(sol.fecha_fin) }}
                      </p>
                      <p class="font-memo-mono text-memo-mono text-xs text-on-surface-variant mt-1">
                        Solicitado: {{ formatFecha(sol.fecha_solicitud) }}
                        <span v-if="sol.faltas?.goce_sueldo" class="ml-2 text-status-punctual">• Con goce de sueldo</span>
                      </p>
                    </div>
                  </div>
                  <div class="flex items-center gap-3">
                    <!-- Estado actual -->
                    <span class="font-label-caps text-[10px] px-2 py-1 rounded border"
                      :class="{
                        'bg-status-punctual/10 text-status-punctual border-status-punctual/30': sol.aprobacion === 'Aprobada',
                        'bg-error-container text-on-error-container border-error/30': sol.aprobacion === 'Rechazada',
                        'bg-secondary-container text-on-secondary-container border-outline-variant': sol.aprobacion === 'Pendiente',
                        'bg-surface-container text-on-surface-variant border-outline-variant': sol.aprobacion === 'Cancelada',
                      }">
                      {{ sol.aprobacion }}
                    </span>
                    <!-- Acciones si está pendiente -->
                    <div v-if="sol.aprobacion === 'Pendiente'" class="flex gap-2">
                      <button @click="actualizarPermiso(sol.id_ausencia, 'Aprobada')"
                        class="flex items-center gap-1 bg-status-punctual text-on-primary px-3 py-1.5 rounded font-label-caps text-[10px] hover:opacity-90 transition-colors shadow-[1px_1px_0_0_#8C8C8C] cursor-pointer">
                        <span class="material-symbols-outlined text-sm">check</span> Aprobar
                      </button>
                      <button @click="actualizarPermiso(sol.id_ausencia, 'Rechazada')"
                        class="flex items-center gap-1 bg-error text-on-error px-3 py-1.5 rounded font-label-caps text-[10px] hover:opacity-90 transition-colors shadow-[1px_1px_0_0_#8C8C8C] cursor-pointer">
                        <span class="material-symbols-outlined text-sm">close</span> Rechazar
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="mt-4 pt-4 border-t border-outline-variant text-center">
                <p class="font-memo-mono text-memo-mono text-on-surface-variant text-xs opacity-70">
                  "Un permiso no aprobado es un permiso no merecido." — Nota administrativa 4.A
                </p>
              </div>
            </div>

          </div>
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
const loadingPermisos = ref(true)
const empleado = ref(null)
const horario = ref(null)
const solicitudes = ref([])
const mostrarConfirmar = ref(false)
const stats = ref({ puntuales: 0, retrasados: 0, ausentes: 0 })
const asistencias = ref([])

const mesActual = new Date().toLocaleDateString('es-MX', { month: 'long', year: 'numeric' })

onMounted(async () => {
  const id = route.params.id
  await Promise.all([
    cargarEmpleado(id),
    cargarAsistencias(id),
    cargarSolicitudes(id)
  ])
})

async function cargarEmpleado(id) {
  const { data } = await supabase
    .from('empleado')
    .select(`
      id_empleado, nombres, apellido_paterno, apellido_materno,
      email, fecha_contratacion, sueldo, foto_url,
      departamento ( nombre_departamento ),
      horario ( turno, hora_entrada, hora_salida )
    `)
    .eq('id_empleado', id)
    .single()

  if (data) {
    empleado.value = data
    horario.value = data.horario?.[0] || null
  }
  loading.value = false
}

async function cargarAsistencias(id) {
  const hoy = new Date()
  const primerDia = new Date(hoy.getFullYear(), hoy.getMonth(), 1).toISOString().split('T')[0]
  const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0).toISOString().split('T')[0]

  const { data } = await supabase
    .from('asistencias')
    .select('estado, fecha')
    .eq('id_empleado', id)
    .gte('fecha', primerDia)
    .lte('fecha', ultimoDia)

  if (data) {
    asistencias.value = data
    stats.value = {
      puntuales:  data.filter(a => a.estado === 'activo').length,
      retrasados: data.filter(a => a.estado === 'leve_retraso').length,
      ausentes:   data.filter(a => a.estado === 'falta').length,
    }
  }
}

async function cargarSolicitudes(id) {
  const { data } = await supabase
    .from('solicitudes_ausencia')
    .select(`
      id_ausencia, fecha_inicio, fecha_fin, aprobacion, fecha_solicitud,
      faltas ( faltas, goce_sueldo )
    `)
    .eq('id_empleado', id)
    .order('fecha_solicitud', { ascending: false })

  if (data) solicitudes.value = data
  loadingPermisos.value = false
}

async function actualizarPermiso(idAusencia, nuevoEstado) {
  const { error } = await supabase
    .from('solicitudes_ausencia')
    .update({ aprobacion: nuevoEstado })
    .eq('id_ausencia', idAusencia)

  if (!error) {
    const sol = solicitudes.value.find(s => s.id_ausencia === idAusencia)
    if (sol) sol.aprobacion = nuevoEstado
  }
}

async function darDeBaja() {
  const id = empleado.value.id_empleado
  const email = empleado.value.email

  try {
    const { error: e1 } = await supabase.from('asistencias').delete().eq('id_empleado', id)
    console.log('asistencias:', e1)

    const { error: e2 } = await supabase.from('solicitudes_ausencia').delete().eq('id_empleado', id)
    console.log('solicitudes_ausencia:', e2)

    const { error: e3 } = await supabase.from('horario').delete().eq('id_empleado', id)
    console.log('horario:', e3)

    const { error: e4 } = await supabase.from('saldo_vacaciones').delete().eq('id_empleado', id)
    console.log('saldo_vacaciones:', e4)

    const { error: e5 } = await supabase.from('cuenta').delete().eq('id_empleado', id)
    console.log('cuenta:', e5)

    const { error: e6 } = await supabase.from('empleado').delete().eq('id_empleado', id)
    console.log('empleado:', e6)

    if (!e6) {
      await supabase.functions.invoke('delete-user', { body: { email } })
      router.push('/directorio')
    }

  } catch (e) {
    console.log('Error general:', e)
  }
}

// Calendario
const calendarioDias = computed(() => {
  const hoy = new Date()
  const año = hoy.getFullYear()
  const mes = hoy.getMonth()
  const primerDia = new Date(año, mes, 1)
  const ultimoDia = new Date(año, mes + 1, 0)
  const dias = []

  // Filtramos solo las solicitudes que ya fueron aprobadas
  const permisosAprobados = solicitudes.value.filter(s => s.aprobacion === 'Aprobada')

  // Espacios vacíos al inicio (lunes = 0)
  let diaSemana = primerDia.getDay()
  diaSemana = diaSemana === 0 ? 6 : diaSemana - 1
  for (let i = 0; i < diaSemana; i++) {
    dias.push({ numero: '', clase: 'opacity-0', tooltip: '' })
  }

  for (let d = 1; d <= ultimoDia.getDate(); d++) {
    const fecha = new Date(año, mes, d)
    const fechaStr = fecha.toISOString().split('T')[0]
    const diaSem = fecha.getDay()
    const esFinDeSemana = diaSem === 0 || diaSem === 6
    const asistencia = asistencias.value.find(a => a.fecha === fechaStr)
    const esHoy = d === hoy.getDate()

    // Verificamos si la fecha actual está dentro de un rango de permiso aprobado
    const permisoActivo = permisosAprobados.find(p => {
      const fin = p.fecha_fin || p.fecha_inicio // Por si es permiso de un solo día
      return fechaStr >= p.fecha_inicio && fechaStr <= fin
    })

    let clase = ''
    let tooltip = ''

    // Jerarquía de visualización: 1. Permisos -> 2. Fin de semana -> 3. Asistencias -> 4. Sin registro
    if (permisoActivo) {
      const tipoPermiso = permisoActivo.faltas?.faltas?.toLowerCase() || ''
      
      if (tipoPermiso.includes('vacacion') || tipoPermiso.includes('vacaciones')) {
        clase = 'bg-blue-500 text-white border-blue-600'
        tooltip = 'Vacaciones'
      } else {
        clase = 'bg-purple-500 text-white border-purple-600'
        tooltip = `Falta Autorizada (${permisoActivo.faltas?.faltas || 'Permiso'})`
      }
    } else if (esFinDeSemana) {
      clase = 'bg-surface-container-high text-on-surface-variant'
      tooltip = 'Fin de semana'
    } else if (asistencia) {
      if (asistencia.estado === 'activo') {
        clase = 'bg-status-punctual text-on-primary'
        tooltip = 'Puntual'
      } else if (asistencia.estado === 'leve_retraso') {
        clase = 'bg-status-delay text-on-background'
        tooltip = 'Retraso'
      } else {
        clase = 'bg-status-absence text-on-primary'
        tooltip = 'Ausencia'
      }
    } else {
      clase = 'border border-outline-variant text-on-surface-variant'
      tooltip = 'Sin registro'
    }

    if (esHoy) clase += ' ring-2 ring-primary ring-offset-1 ring-offset-surface-bright'

    dias.push({ numero: d, clase, tooltip })
  }

  return dias
})

function formatFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha + 'T12:00:00').toLocaleDateString('es-MX', {
    year: 'numeric', month: 'long', day: 'numeric'
  })
}

function formatMes(fecha) {
  if (!fecha) return ''
  return new Date(fecha + 'T12:00:00').toLocaleDateString('es-MX', { month: 'short' }).toUpperCase()
}

function formatDia(fecha) {
  if (!fecha) return ''
  return new Date(fecha + 'T12:00:00').getDate()
}
</script>