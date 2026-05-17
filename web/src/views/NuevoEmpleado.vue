<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <div class="p-8 max-w-6xl mx-auto">

        <!-- Stepper -->
        <div class="mb-8 bg-surface border border-outline-variant p-4 pb-12 rounded shadow-[2px_2px_0_0_#8C8C8C] relative">
          <!-- Etiqueta superior -->
          <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed rounded-br z-10">
            FORMULARIO RH-07
          </div>

          <!-- Contenedor Flex -->
          <div class="flex items-center w-full mt-10 px-4 md:px-8">
            <template v-for="(paso, i) in pasos" :key="i">
              
              <!-- Círculo y Etiqueta del paso -->
              <div class="relative flex flex-col items-center">
                <div class="w-8 h-8 rounded-full flex items-center justify-center font-label-caps text-label-caps shadow-[2px_2px_0_0_#8C8C8C] z-10 transition-colors duration-300"
                  :class="pasoActual > i + 1 ? 'bg-status-punctual text-on-primary' : pasoActual === i + 1 ? 'bg-primary text-on-primary' : 'bg-surface border border-outline-variant text-on-surface-variant'">
                  <span v-if="pasoActual > i + 1" class="material-symbols-outlined text-sm">check</span>
                  <span v-else>{{ i + 1 }}</span>
                </div>
                
                <!-- El texto tiene posición absoluta para no empujar la línea hacia abajo -->
                <div class="absolute top-10 w-28 text-center font-label-caps text-label-caps leading-tight"
                  :class="pasoActual === i + 1 ? 'text-primary' : 'text-on-surface-variant'">
                  {{ paso }}
                </div>
              </div>

              <!-- Línea conectora (se omite después del último paso) -->
              <div v-if="i < pasos.length - 1" class="flex-auto h-0.5 mx-2 transition-colors duration-300"
                :class="pasoActual > i + 1 ? 'bg-status-punctual' : 'bg-outline-variant'">
              </div>
              
            </template>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

          <!-- Formulario -->
          <div class="lg:col-span-2 bg-surface border border-outline-variant p-8 shadow-[2px_2px_0_0_#8C8C8C] relative">
            <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed">
              SECCIÓN {{ pasoActual }}
            </div>

            <!-- PASO 1: Datos Personales -->
            <div v-if="pasoActual === 1" class="mt-6 space-y-6">
              <h3 class="font-headline-md text-headline-md text-primary">Datos Personales</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Nombre(s) *</label>
                  <input v-model="form.nombres" type="text" placeholder="Ej. Dwight Kurt"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Paterno *</label>
                  <input v-model="form.apellido_paterno" type="text" placeholder="Ej. Schrute"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Materno</label>
                  <input v-model="form.apellido_materno" type="text" placeholder="Ej. K."
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <!-- Email generado automáticamente -->
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Correo Corporativo</label>
                  <div class="flex items-center border-b-2 border-outline-variant py-2">
                    <span class="font-body-md text-body-md text-on-surface-variant">{{ emailGenerado || 'nombre.apellido@dundermifflin.com' }}</span>
                    <span v-if="emailGenerado" class="material-symbols-outlined text-status-punctual text-sm ml-2">check_circle</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- PASO 2: Datos Laborales -->
            <div v-if="pasoActual === 2" class="mt-6 space-y-6">
              <h3 class="font-headline-md text-headline-md text-primary">Datos Laborales</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Fecha de Contratación</label>
                  <div class="border-b-2 border-outline-variant py-2 font-body-md text-body-md text-on-surface-variant">
                    {{ fechaHoy }}
                  </div>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Departamento *</label>
                  <select v-model="form.id_departamento" @change="asignarSueldo"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface">
                    <option value="">Seleccionar...</option>
                    <option v-for="dept in departamentos" :key="dept.id_departamento" :value="dept.id_departamento">
                      {{ dept.nombre_departamento }}
                    </option>
                  </select>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Sueldo Asignado</label>
                  <div class="border-b-2 border-outline-variant py-2 font-body-md text-body-md"
                    :class="form.sueldo ? 'text-status-punctual' : 'text-on-surface-variant'">
                    {{ form.sueldo ? `$${form.sueldo.toLocaleString()} MXN` : 'Se asigna al seleccionar departamento' }}
                  </div>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Días de Vacaciones</label>
                  <div class="border-b-2 border-outline-variant py-2 font-body-md text-body-md text-status-punctual">
                    12 días (1er año — LFT)
                  </div>
                </div>
              </div>

              <!-- Horario -->
              <div class="border border-outline-variant rounded p-4 relative">
                <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
                  HORARIO LABORAL
                </div>
                <div class="mt-4">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-3 uppercase block">Turno *</label>
                  <div class="grid grid-cols-3 gap-3">
                    <button v-for="turno in turnos" :key="turno.nombre" @click="seleccionarTurno(turno)"
                      class="p-3 border rounded text-center transition-all"
                      :class="form.turno === turno.nombre
                        ? 'border-primary bg-primary text-on-primary shadow-[2px_2px_0_0_#8C8C8C]'
                        : 'border-outline-variant hover:bg-surface-container text-on-surface'">
                      <p class="font-label-caps text-label-caps">{{ turno.nombre }}</p>
                      <p class="font-memo-mono text-memo-mono mt-1"
                        :class="form.turno === turno.nombre ? 'text-on-primary' : 'text-on-surface-variant'">
                        {{ turno.entrada }} — {{ turno.salida }}
                      </p>
                    </button>
                  </div>
                  <p v-if="form.turno" class="font-body-sm text-body-sm text-on-surface-variant mt-3">
                    <span class="material-symbols-outlined text-sm align-middle">schedule</span>
                    Tolerancia: 15 minutos
                  </p>
                </div>
              </div>
            </div>

            <!-- PASO 3: Vista previa y confirmación -->
            <div v-if="pasoActual === 3" class="mt-6 space-y-6">
              <h3 class="font-headline-md text-headline-md text-primary">Confirmar Registro</h3>

              <div class="space-y-4">
                <div class="bg-surface-container-low border border-outline-variant p-4 rounded">
                  <p class="font-label-caps text-label-caps text-on-surface-variant mb-3 uppercase">Datos Personales</p>
                  <div class="grid grid-cols-2 gap-2 font-body-sm text-body-sm">
                    <span class="text-on-surface-variant">Nombre completo</span>
                    <span class="text-on-surface font-bold">{{ form.nombres }} {{ form.apellido_paterno }} {{ form.apellido_materno }}</span>
                    <span class="text-on-surface-variant">Correo</span>
                    <span class="text-on-surface">{{ emailGenerado }}</span>
                  </div>
                </div>
                <div class="bg-surface-container-low border border-outline-variant p-4 rounded">
                  <p class="font-label-caps text-label-caps text-on-surface-variant mb-3 uppercase">Datos Laborales</p>
                  <div class="grid grid-cols-2 gap-2 font-body-sm text-body-sm">
                    <span class="text-on-surface-variant">Departamento</span>
                    <span class="text-on-surface font-bold">{{ departamentoSeleccionado }}</span>
                    <span class="text-on-surface-variant">Sueldo</span>
                    <span class="text-on-surface">${{ form.sueldo?.toLocaleString() }} MXN</span>
                    <span class="text-on-surface-variant">Turno</span>
                    <span class="text-on-surface">{{ form.turno }}</span>
                    <span class="text-on-surface-variant">Horario</span>
                    <span class="text-on-surface">{{ form.hora_entrada }} — {{ form.hora_salida }}</span>
                    <span class="text-on-surface-variant">Vacaciones</span>
                    <span class="text-on-surface">12 días</span>
                  </div>
                </div>
                <div class="bg-surface-container-low border border-outline-variant p-4 rounded">
                  <p class="font-label-caps text-label-caps text-on-surface-variant mb-3 uppercase">Acceso al Sistema</p>
                  <div class="grid grid-cols-2 gap-2 font-body-sm text-body-sm">
                    <span class="text-on-surface-variant">Usuario</span>
                    <span class="text-on-surface font-bold">{{ usuarioGenerado }}</span>
                    <span class="text-on-surface-variant">Contraseña temporal</span>
                    <span class="text-on-surface font-memo-mono">DunderMifflin2026!</span>
                  </div>
                </div>
              </div>

              <div v-if="errorMsg" class="bg-error-container text-on-error-container p-3 rounded flex items-start gap-2 border border-error">
                <span class="material-symbols-outlined">error</span>
                <p class="font-body-sm text-body-sm">{{ errorMsg }}</p>
              </div>
              <div v-if="exitoMsg" class="bg-status-punctual/10 text-status-punctual p-3 rounded flex items-start gap-2 border border-status-punctual">
                <span class="material-symbols-outlined">check_circle</span>
                <p class="font-body-sm text-body-sm">{{ exitoMsg }}</p>
              </div>
            </div>

            <!-- Navegación -->
            <div class="mt-8 flex justify-between items-center border-t border-outline-variant pt-6">
              <button @click="pasoAnterior" :disabled="pasoActual === 1 || empleadoRegistrado"
                class="font-label-caps text-label-caps px-6 py-2 border border-outline-variant text-on-surface-variant uppercase transition-colors"
                :class="(pasoActual === 1 || empleadoRegistrado) ? 'opacity-50 cursor-not-allowed' : 'hover:bg-surface-container'">
                Anterior
              </button>
              
              <button v-if="pasoActual < 3" @click="pasoSiguiente"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary hover:bg-primary-container hover:text-on-primary-container transition-colors uppercase flex items-center gap-2 shadow-[2px_2px_0_0_#8C8C8C]">
                Siguiente
                <span class="material-symbols-outlined text-sm">arrow_forward</span>
              </button>

              <button v-if="pasoActual === 3 && !empleadoRegistrado" @click="registrarEmpleado" :disabled="loading"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary hover:bg-primary-container hover:text-on-primary-container transition-colors uppercase flex items-center gap-2 shadow-[2px_2px_0_0_#8C8C8C]">
                {{ loading ? 'Registrando BD...' : 'Crear Accesos y Esperar Foto' }}
                <span class="material-symbols-outlined text-sm">how_to_reg</span>
              </button>

              <div v-if="pasoActual === 3 && empleadoRegistrado && esperandoFoto" class="flex items-center gap-4">
                <span class="font-label-caps text-label-caps text-on-surface-variant flex items-center gap-2">
                  <span class="material-symbols-outlined animate-spin text-primary">sync</span>
                  ESPERANDO FOTO DESDE APP...
                </span>
                <button @click="finalizar" 
                  class="font-label-caps text-label-caps px-4 py-2 border border-outline-variant text-on-surface-variant hover:bg-surface-container transition-colors uppercase text-xs">
                  Omitir por ahora
                </button>
              </div>

              <button v-if="pasoActual === 3 && empleadoRegistrado && !esperandoFoto" @click="finalizar" 
                class="font-label-caps text-label-caps px-6 py-2 bg-status-punctual text-on-primary hover:bg-status-punctual/90 transition-colors uppercase flex items-center gap-2 shadow-[2px_2px_0_0_#8C8C8C]">
                Finalizar Proceso
                <span class="material-symbols-outlined text-sm">check_circle</span>
              </button>
            </div>
          </div>

          <!-- Gafete preview -->
          <div class="lg:col-span-1">
            <div class="bg-surface-container-low border border-outline-variant p-6 shadow-[2px_2px_0_0_#8C8C8C] sticky top-24">
              <div class="flex items-center gap-2 mb-4 pb-3 border-b border-outline-variant">
                <span class="material-symbols-outlined text-on-surface-variant">badge</span>
                <h3 class="font-label-caps text-label-caps text-on-surface-variant uppercase">Vista Previa Gafete</h3>
              </div>
              <div class="bg-surface border border-outline-variant p-6 flex flex-col items-center text-center">
                <div class="w-24 h-24 bg-surface-variant rounded-full border-2 border-outline-variant mb-4 flex items-center justify-center overflow-hidden relative">
                  <img v-if="fotoUrl" :src="fotoUrl" alt="Foto Gafete" class="w-full h-full object-cover" />
                  
                  <div v-else-if="esperandoFoto" class="absolute inset-0 bg-surface-variant/80 flex flex-col items-center justify-center">
                    <span class="material-symbols-outlined animate-spin text-primary mb-1">sync</span>
                  </div>

                  <span v-else class="material-symbols-outlined text-5xl text-outline">account_circle</span>
                </div>
                <p class="font-label-caps text-label-caps text-on-surface-variant mb-1">DUNDER MIFFLIN PAPER CO.</p>
                <div class="font-headline-md text-headline-md text-primary w-full border-b border-outline-variant pb-2 mb-2 min-h-[32px]">
                  {{ form.nombres || 'Nombre' }} {{ form.apellido_paterno || 'Apellido' }}
                </div>
                <div class="font-label-caps text-label-caps text-on-surface-variant mb-4 uppercase min-h-4">
                  {{ departamentoSeleccionado || 'DEPARTAMENTO' }}
                </div>
                <div class="w-full text-left space-y-2 border-t border-outline-variant pt-3">
                  <div class="flex items-center gap-2">
                    <span class="material-symbols-outlined text-[14px] text-outline">mail</span>
                    <span class="font-memo-mono text-on-surface-variant text-xs">{{ emailGenerado || '---' }}</span>
                  </div>
                  <div class="flex items-center gap-2">
                    <span class="material-symbols-outlined text-[14px] text-outline">schedule</span>
                    <span class="font-memo-mono  text-on-surface-variant text-xs">{{ form.turno || 'Sin turno asignado' }}</span>
                  </div>
                </div>
              </div>
              <p class="font-memo-mono text-memo-mono text-on-surface-variant text-xs text-center italic mt-4">
                HR-Notice: La impresión requiere aprobación de Michael Scott.
              </p>
            </div>
          </div>

        </div>
      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const pasoActual = ref(1)
const pasos = ['Datos Personales', 'Datos Laborales', 'Confirmación']
const loading = ref(false)
const errorMsg = ref('')
const exitoMsg = ref('')
const departamentos = ref([])
const empleadoRegistrado = ref(false)
const esperandoFoto = ref(false)
const fotoUrl = ref(null)
let realtimeChannel = null

const form = ref({
  nombres: '', apellido_paterno: '', apellido_materno: '',
  id_departamento: '', sueldo: null,
  turno: '', hora_entrada: '', hora_salida: ''
})

// Sueldos por departamento
const sueldosPorDepartamento = {
  'Ventas': 18500,
  'Contabilidad': 20000,
  'Recursos Humanos': 17000,
  'Recepción': 14000,
  'Almacén': 13500,
}

// Turnos
const turnos = [
  { nombre: 'Matutino',  entrada: '08:00', salida: '16:00' },
  { nombre: 'Vespertino', entrada: '14:00', salida: '22:00' },
  { nombre: 'Mixto',     entrada: '10:00', salida: '19:00' },
]

// Fecha de hoy formateada
const fechaHoy = new Date().toLocaleDateString('es-MX', {
  year: 'numeric', month: 'long', day: 'numeric'
})
const fechaISO = new Date().toISOString().split('T')[0]

// Computadas
const emailGenerado = computed(() => {
  if (!form.value.nombres || !form.value.apellido_paterno) return ''
  const nombre = form.value.nombres.split(' ')[0].toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
  const apellido = form.value.apellido_paterno.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
  return `${nombre}.${apellido}@dundermifflin.com`
})

const usuarioGenerado = computed(() => {
  if (!form.value.nombres || !form.value.apellido_paterno) return ''
  const nombre = form.value.nombres.split(' ')[0].toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
  const apellido = form.value.apellido_paterno.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
  return `${nombre}.${apellido}`
})

const departamentoSeleccionado = computed(() => {
  const dept = departamentos.value.find(d => d.id_departamento === form.value.id_departamento)
  return dept?.nombre_departamento || ''
})

onUnmounted(() => {
  if (realtimeChannel) supabase.removeChannel(realtimeChannel)
})

onMounted(async () => {
  const { data } = await supabase.from('departamento').select('id_departamento, nombre_departamento')
  if (data) departamentos.value = data
})

function asignarSueldo() {
  const nombre = departamentoSeleccionado.value
  form.value.sueldo = sueldosPorDepartamento[nombre] || null
}

function seleccionarTurno(turno) {
  form.value.turno = turno.nombre
  form.value.hora_entrada = turno.entrada
  form.value.hora_salida = turno.salida
}

function validarPaso() {
  if (pasoActual.value === 1) {
    if (!form.value.nombres || !form.value.apellido_paterno) {
      errorMsg.value = 'El nombre y apellido paterno son obligatorios.'
      return false
    }
  }
  if (pasoActual.value === 2) {
    if (!form.value.id_departamento) {
      errorMsg.value = 'Debes seleccionar un departamento.'
      return false
    }
    if (!form.value.turno) {
      errorMsg.value = 'Debes seleccionar un turno.'
      return false
    }
  }
  errorMsg.value = ''
  return true
}

function pasoSiguiente() {
  if (validarPaso()) pasoActual.value++
}

function pasoAnterior() {
  if (pasoActual.value > 1) pasoActual.value--
  errorMsg.value = ''
}

async function registrarEmpleado() {
  loading.value = true
  errorMsg.value = ''
  exitoMsg.value = ''

  try {
    // 1. Crear usuario en Supabase Auth
    const { data: authData, error: authError } = await supabase.functions.invoke('create-user', {
      body: {
        email: emailGenerado.value,
        password: 'DunderMifflin2026!',
      }
    })

    if (authError) throw new Error('Error al crear acceso: ' + authError.message)

    // 2. Insertar horario
    const { data: horarioData, error: horarioError } = await supabase
      .from('horario')
      .insert({
        turno: form.value.turno,
        hora_entrada: form.value.hora_entrada,
        hora_salida: form.value.hora_salida,
        minutos_tolerancia: 15
      })
      .select()
      .single()

    if (horarioError) throw new Error('Error al crear horario: ' + horarioError.message)

    // 3. Insertar saldo vacaciones
    const { data: vacData, error: vacError } = await supabase
      .from('saldo_vacaciones')
      .insert({
        ano_periodo: new Date().getFullYear(),
        dias_otorgados: 12,
        dias_consumidos: 0
      })
      .select()
      .single()

    if (vacError) throw new Error('Error al crear vacaciones: ' + vacError.message)

    // 4. Insertar empleado
    const { data: empData, error: empError } = await supabase
      .from('empleado')
      .insert({
        nombres: form.value.nombres,
        apellido_paterno: form.value.apellido_paterno,
        apellido_materno: form.value.apellido_materno,
        email: emailGenerado.value,
        fecha_contratacion: fechaISO,
        sueldo: form.value.sueldo,
        id_departamento: form.value.id_departamento
      })
      .select()
      .single()

    if (empError) throw new Error('Error al guardar empleado: ' + empError.message)

    // 5. Insertar cuenta
    const { error: cuentaError } = await supabase
      .from('cuenta')
      .insert({
        id_cuenta: empData.id_empleado,
        usuario_cuenta: usuarioGenerado.value,
        huella: false,
        primer_inicio: true,
        id_empleado: empData.id_empleado
      })

    if (cuentaError) throw new Error('Error al crear cuenta: ' + cuentaError.message)

    empleadoRegistrado.value = true
    esperandoFoto.value = true
    exitoMsg.value = `Cuenta creada. Pide a ${form.value.nombres} que inicie sesión en la app, actualice su contraseña y tome su foto.`

    // Iniciar la búsqueda de la foto en la BD
    iniciarEscuchaFoto(empData.id_empleado)

  } catch (e) {
    errorMsg.value = e.message
  }

  loading.value = false
}

function iniciarEscuchaFoto(idEmpleado) {
  // Nos suscribimos SOLO a los cambios (UPDATE) de este empleado en particular
  realtimeChannel = supabase
    .channel('esperando-foto-gafete')
    .on(
      'postgres_changes',
      {
        event: 'UPDATE',
        schema: 'public',
        table: 'empleado', // Asegúrate de que sea tu tabla correcta
        filter: `id_empleado=eq.${idEmpleado}` // Escucha solo a este empleado
      },
      (payload) => {
        // Esta función se dispara SOLA en cuanto hay un cambio en la base de datos
        const nuevaFoto = payload.new.foto_url 
        
        if (nuevaFoto) {
          fotoUrl.value = nuevaFoto
          esperandoFoto.value = false
          exitoMsg.value = '¡Foto recibida con éxito! El gafete está listo.'
          
          // Como ya tenemos la foto, cerramos la conexión en tiempo real
          supabase.removeChannel(realtimeChannel)
        }
      }
    )
    .subscribe()
}

function finalizar() {
  if (realtimeChannel) supabase.removeChannel(realtimeChannel)
  router.push('/directorio')
}
</script>