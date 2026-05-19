<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <header class="flex items-center justify-between px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div class="flex items-center gap-4">
          <RouterLink :to="`/empleado/${route.params.id}`" class="text-on-surface-variant hover:text-primary transition-colors">
            <span class="material-symbols-outlined">arrow_back</span>
          </RouterLink>
          <div>
            <p class="font-memo-mono text-memo-mono text-on-surface-variant">Actualizando Registro HR-{{ route.params.id }}</p>
            <h2 class="font-headline-md text-headline-md text-primary">Expediente del Empleado: Edición</h2>
          </div>
        </div>
        <RouterLink :to="`/empleado/${route.params.id}`"
          class="flex items-center gap-2 px-4 py-2 border border-outline text-on-surface-variant font-label-caps text-label-caps hover:bg-surface-container transition-colors rounded">
          <span class="material-symbols-outlined text-sm">close</span>
          Cancelar
        </RouterLink>
      </header>

      <div v-if="loading" class="text-center py-24 font-body-sm text-body-sm text-on-surface-variant">
        Cargando expediente...
      </div>

      <div v-else class="p-8 max-w-5xl mx-auto">

        <div class="mb-8 bg-surface border border-outline-variant p-4 pb-12 rounded shadow-[2px_2px_0_0_#8C8C8C] relative">
          <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed rounded-br z-10">
            FORMULARIO RH-07
          </div>

          <div class="flex items-center w-full mt-10 px-4 md:px-8">
            <template v-for="(paso, i) in pasos" :key="i">
              <div class="relative flex flex-col items-center">
                <div class="w-8 h-8 rounded-full flex items-center justify-center font-label-caps text-label-caps shadow-[2px_2px_0_0_#8C8C8C] z-10 transition-colors duration-300"
                  :class="pasoActual > i + 1 ? 'bg-status-punctual text-on-primary' : pasoActual === i + 1 ? 'bg-primary text-on-primary' : 'bg-surface border border-outline-variant text-on-surface-variant'">
                  <span v-if="pasoActual > i + 1" class="material-symbols-outlined text-sm">check</span>
                  <span v-else>{{ i + 1 }}</span>
                </div>
                <div class="absolute top-10 w-28 text-center font-label-caps text-label-caps leading-tight"
                  :class="pasoActual === i + 1 ? 'text-primary' : 'text-on-surface-variant'">
                  {{ paso }}
                </div>
              </div>
              <div v-if="i < pasos.length - 1" class="flex-auto h-0.5 mx-2 transition-colors duration-300"
                :class="pasoActual > i + 1 ? 'bg-status-punctual' : 'bg-outline-variant'">
              </div>
            </template>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

          <div class="lg:col-span-2 bg-surface border border-outline-variant p-8 shadow-[2px_2px_0_0_#8C8C8C] relative">
            <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed">
              SECCIÓN {{ pasoActual }}
            </div>

            <div v-if="pasoActual === 1" class="mt-6 space-y-6">
              <div class="flex justify-between items-center">
                <h3 class="font-headline-lg text-headline-lg text-primary">Datos Personales</h3>
                <span class="bg-status-punctual/10 text-status-punctual text-[10px] font-bold px-2 py-0.5 border border-status-punctual/20 rounded">
                  ESTADO: ACTIVO
                </span>
              </div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Nombre(s)</label>
                  <input v-model="form.nombres" @input="validarLetras('nombres')" type="text"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Paterno</label>
                  <input v-model="form.apellido_paterno" @input="validarLetras('apellido_paterno')" type="text"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Materno</label>
                  <input v-model="form.apellido_materno" @input="validarLetras('apellido_materno')" type="text"
                    class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Correo Corporativo</label>
                  <input v-model="form.email" type="email" disabled
                    class="bg-surface-container border-0 border-b-2 border-outline-variant px-0 py-2 font-memo-mono text-memo-mono text-on-surface-variant cursor-not-allowed"/>
                </div>
              </div>
            </div>

            <div v-if="pasoActual === 2" class="mt-6 space-y-8">
              <div>
                <h3 class="font-headline-lg text-headline-lg text-primary mb-4">Datos Laborales</h3>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Departamento</label>
                    <select v-model="form.id_departamento"
                      class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface">
                      <option v-for="dept in departamentos" :key="dept.id_departamento" :value="dept.id_departamento">
                        {{ dept.nombre_departamento }}
                      </option>
                    </select>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Sueldo (MXN)</label>
                    <input v-model="form.sueldo" type="number"
                      class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Fecha de Contratación</label>
                    <input v-model="form.fecha_contratacion" type="date"
                      class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                  </div>
                </div>
              </div>

              <div class="border border-outline-variant rounded p-4 relative">
                <div class="absolute top-0 left-0 bg-blue-100 font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-blue-800">
                  SALDO DE VACACIONES
                </div>
                <div class="mt-6 grid grid-cols-2 gap-6">
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2">DÍAS OTORGADOS</label>
                    <input v-model="form.dias_otorgados" type="number" min="0"
                      class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2">DÍAS CONSUMIDOS</label>
                    <input v-model="form.dias_consumidos" type="number" min="0"
                      class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface"/>
                  </div>
                </div>
              </div>

              <div class="border border-outline-variant rounded p-4 relative">
                <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
                  HORARIO LABORAL
                </div>
                <div class="mt-4">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-3 uppercase block">Turno</label>
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
                </div>
              </div>

              <div class="border border-outline-variant rounded p-4 relative bg-error/5">
                <div class="absolute top-0 left-0 bg-error text-on-error font-label-caps text-label-caps px-2 py-1 border-r border-b border-error rounded-br">
                  CONTROL DE SEGURIDAD
                </div>
                <div class="mt-6 flex items-center justify-between">
                  <div>
                    <p class="font-label-caps text-label-caps text-on-surface">Forzar Reinicio de Contraseña</p>
                    <p class="text-label-caps text-on-surface-variant mt-1">Si se activa, el empleado será obligado a crear una nueva clave en su próximo inicio de sesión.</p>
                  </div>
                  
                  <button @click="form.reiniciar_password = !form.reiniciar_password"
                    class="w-12 h-6 rounded-full relative transition-colors duration-300 focus:outline-none shadow-inner"
                    :class="form.reiniciar_password ? 'bg-error' : 'bg-outline-variant'">
                    <span class="absolute top-1 left-1 bg-white w-4 h-4 rounded-full transition-transform duration-300 shadow"
                      :class="form.reiniciar_password ? 'translate-x-6' : 'translate-x-0'"></span>
                  </button>
                </div>
              </div>

            </div>

            <div v-if="pasoActual === 3" class="mt-6 space-y-4">
              <h3 class="font-headline-lg text-headline-lg text-primary">Confirmar Cambios</h3>
              
              <div class="bg-surface-container-low border border-outline-variant p-4 rounded space-y-2">
                <p class="font-label-caps text-label-caps text-on-surface-variant uppercase mb-3">Datos Personales</p>
                <div class="grid grid-cols-2 gap-2 font-body-sm text-body-sm">
                  <span class="text-on-surface-variant">Nombre</span>
                  <span class="font-bold">{{ form.nombres }} {{ form.apellido_paterno }} {{ form.apellido_materno }}</span>
                  <span class="text-on-surface-variant">Correo</span>
                  <span class="font-memo-mono">{{ form.email }}</span>
                </div>
              </div>

              <div class="bg-surface-container-low border border-outline-variant p-4 rounded space-y-2">
                <p class="font-label-caps text-label-caps text-on-surface-variant uppercase mb-3">Datos Laborales y Accesos</p>
                <div class="grid grid-cols-2 gap-2 font-body-sm text-body-sm">
                  <span class="text-on-surface-variant">Departamento</span>
                  <span class="font-bold">{{ departamentoSeleccionado }}</span>
                  <span class="text-on-surface-variant">Sueldo</span>
                  <span>${{ Number(form.sueldo).toLocaleString() }} MXN</span>
                  <span class="text-on-surface-variant">Saldo Vacaciones</span>
                  <span>{{ form.dias_otorgados }} otorgados / {{ form.dias_consumidos }} consumidos</span>
                  <span class="text-on-surface-variant">Turno</span>
                  <span>{{ form.turno }}</span>
                  <span class="text-error font-bold" v-if="form.reiniciar_password">Seguridad</span>
                  <span class="text-error font-bold" v-if="form.reiniciar_password">Reinicio de contraseña activado</span>
                </div>
              </div>

              <div v-if="exitoMsg" class="bg-status-punctual/10 text-status-punctual p-3 rounded flex items-center gap-2 border border-status-punctual/30">
                <span class="material-symbols-outlined">check_circle</span>
                <p class="font-body-sm text-body-sm">{{ exitoMsg }}</p>
              </div>
              <div v-if="errorMsg" class="bg-error-container text-on-error-container p-3 rounded flex items-center gap-2 border border-error">
                <span class="material-symbols-outlined">error</span>
                <p class="font-body-sm text-body-sm">{{ errorMsg }}</p>
              </div>
            </div>

            <div class="mt-8 flex justify-between items-center border-t border-outline-variant pt-6">
              <div class="flex gap-3">
                <button @click="pasoAnterior" :disabled="pasoActual === 1"
                  class="font-label-caps text-label-caps px-6 py-2 border border-outline-variant text-on-surface-variant uppercase transition-colors"
                  :class="pasoActual === 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-surface-container'">
                  Anterior
                </button>
                <button @click="descartarCambios"
                  class="font-label-caps text-label-caps px-6 py-2 border border-error text-error hover:bg-error/5 transition-colors uppercase">
                  Descartar
                </button>
              </div>
              <button v-if="pasoActual < 3" @click="pasoSiguiente"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary hover:bg-primary-container hover:text-on-primary-container transition-colors uppercase flex items-center gap-2 shadow-[2px_2px_0_0_#8C8C8C]">
                Siguiente
                <span class="material-symbols-outlined text-sm">arrow_forward</span>
              </button>
              <button v-if="pasoActual === 3" @click="guardarCambios" :disabled="guardando"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary hover:bg-primary-container hover:text-on-primary-container transition-colors uppercase flex items-center gap-2 shadow-[2px_2px_0_0_#8C8C8C]">
                {{ guardando ? 'Guardando...' : 'Guardar Cambios' }}
                <span class="material-symbols-outlined text-sm">save</span>
              </button>
            </div>
          </div>

          <div class="lg:col-span-1">
            <div class="bg-surface-container-low border border-outline-variant p-6 shadow-[2px_2px_0_0_#8C8C8C] sticky top-24">
              <div class="flex items-center gap-2 mb-4 pb-3 border-b border-outline-variant">
                <span class="material-symbols-outlined text-on-surface-variant">badge</span>
                <h3 class="font-label-caps text-label-caps text-on-surface-variant uppercase">Vista Previa Gafete</h3>
              </div>
              <div class="bg-surface border border-outline-variant p-6 flex flex-col items-center text-center">
                <div class="w-24 h-24 bg-surface-variant rounded border border-outline-variant mb-4 overflow-hidden flex items-center justify-center">
                  <img v-if="empleado?.foto_url" :src="empleado.foto_url" :alt="form.nombres"
                    class="w-full h-full object-cover grayscale"/>
                  <span v-else class="material-symbols-outlined text-5xl text-outline">account_circle</span>
                </div>
                <p class="font-label-caps text-label-caps text-on-surface-variant mb-1">DUNDER MIFFLIN PAPER CO.</p>
                <div class="font-headline-md text-headline-md text-primary w-full border-b border-outline-variant pb-2 mb-2">
                  {{ form.nombres || 'Nombre' }} {{ form.apellido_paterno || 'Apellido' }}
                </div>
                <div class="font-label-caps text-label-caps text-on-surface-variant mb-4 uppercase">
                  {{ departamentoSeleccionado || 'DEPARTAMENTO' }}
                </div>
                <div class="w-full text-left space-y-2 border-t border-outline-variant pt-3">
                  <div class="flex items-center gap-2">
                    <span class="material-symbols-outlined text-[14px] text-outline">mail</span>
                    <span class="font-memo-mono text-on-surface-variant text-xs truncate">{{ form.email }}</span>
                  </div>
                  <div v-if="form.turno" class="flex items-center gap-2">
                    <span class="material-symbols-outlined text-[14px] text-outline">schedule</span>
                    <span class="font-memo-mono text-on-surface-variant text-xs">{{ form.turno }}</span>
                  </div>
                </div>
              </div>
              <p class="font-memo-mono text-memo-mono text-on-surface-variant text-xs text-center italic mt-4">
                HR-Notice: La edición será registrada en la bitácora de auditoría.
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const guardando = ref(false)
const pasoActual = ref(1)
const pasos = ['Datos Personales', 'Datos Laborales', 'Confirmar']
const exitoMsg = ref('')
const errorMsg = ref('')
const departamentos = ref([])
const empleado = ref(null)
const idHorario = ref(null)
const idVacaciones = ref(null) // Nuevo ref para guardar el ID de la tabla saldo_vacaciones

const form = ref({
  nombres: '', apellido_paterno: '', apellido_materno: '',
  email: '', id_departamento: null, sueldo: 0,
  fecha_contratacion: '', turno: '', hora_entrada: '', hora_salida: '',
  // Nuevos campos
  dias_otorgados: 0, dias_consumidos: 0,
  reiniciar_password: false
})

const turnos = [
  { nombre: 'Matutino',  entrada: '08:00', salida: '16:00' },
  { nombre: 'Vespertino', entrada: '14:00', salida: '22:00' },
  { nombre: 'Mixto',     entrada: '10:00', salida: '19:00' },
]

const departamentoSeleccionado = computed(() => {
  const dept = departamentos.value.find(d => d.id_departamento === form.value.id_departamento)
  return dept?.nombre_departamento || ''
})

onMounted(async () => {
  const id = route.params.id

  // Se agregó la petición de saldo_vacaciones al Promise.all
  const [{ data: emp }, { data: depts }, { data: saldo }] = await Promise.all([
    supabase.from('empleado').select(`
      id_empleado, nombres, apellido_paterno, apellido_materno,
      email, fecha_contratacion, sueldo, foto_url, id_departamento,
      horario ( id_horario, turno, hora_entrada, hora_salida )
    `).eq('id_empleado', id).single(),
    supabase.from('departamento').select('id_departamento, nombre_departamento'),
    supabase.from('saldo_vacaciones').select('*').eq('id_empleado', id).single()
  ])

  if (emp) {
    empleado.value = emp
    const hor = emp.horario?.[0]
    idHorario.value = hor?.id_horario || null
    
    // Asignar los datos del saldo si existen
    if (saldo) {
      idVacaciones.value = saldo.id_vacaciones
    }

    form.value = {
      nombres: emp.nombres,
      apellido_paterno: emp.apellido_paterno,
      apellido_materno: emp.apellido_materno || '',
      email: emp.email,
      id_departamento: emp.id_departamento,
      sueldo: emp.sueldo,
      fecha_contratacion: emp.fecha_contratacion,
      turno: hor?.turno || '',
      hora_entrada: hor?.hora_entrada || '',
      hora_salida: hor?.hora_salida || '',
      dias_otorgados: saldo?.dias_otorgados || 0,
      dias_consumidos: saldo?.dias_consumidos || 0,
      reiniciar_password: false // Valor por defecto
    }
  }
  if (depts) departamentos.value = depts
  loading.value = false
})

// NUEVA FUNCIÓN: Validador de texto (Filtra números y caracteres no alfabéticos en tiempo real)
function validarLetras(campo) {
  // Regex: Permite letras de la A-Z, mayúsculas, minúsculas, espacios y caracteres acentuados (incluyendo ñ y diéresis)
  const regexInvalido = /[^a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]/g;
  form.value[campo] = form.value[campo].replace(regexInvalido, '');
}

function seleccionarTurno(turno) {
  form.value.turno = turno.nombre
  form.value.hora_entrada = turno.entrada
  form.value.hora_salida = turno.salida
}

function pasoSiguiente() { pasoActual.value++ }
function pasoAnterior() { if (pasoActual.value > 1) pasoActual.value-- }
function descartarCambios() { router.push(`/empleado/${route.params.id}`) }

async function guardarCambios() {
  guardando.value = true
  errorMsg.value = ''
  exitoMsg.value = ''

  try {
    // 1. Actualizar empleado
    const { error: empError } = await supabase
      .from('empleado')
      .update({
        nombres: form.value.nombres,
        apellido_paterno: form.value.apellido_paterno,
        apellido_materno: form.value.apellido_materno,
        id_departamento: form.value.id_departamento,
        sueldo: parseFloat(form.value.sueldo),
        fecha_contratacion: form.value.fecha_contratacion,
      })
      .eq('id_empleado', route.params.id)

    if (empError) throw new Error('Error al actualizar empleado: ' + empError.message)

    // 2. Actualizar horario
    if (idHorario.value) {
      await supabase.from('horario').update({
        turno: form.value.turno,
        hora_entrada: form.value.hora_entrada,
        hora_salida: form.value.hora_salida,
      }).eq('id_horario', idHorario.value)
    }

    // 3. NUEVO: Actualizar saldo de vacaciones
    if (idVacaciones.value) {
      await supabase.from('saldo_vacaciones').update({
        dias_otorgados: parseInt(form.value.dias_otorgados),
        dias_consumidos: parseInt(form.value.dias_consumidos)
      }).eq('id_vacaciones', idVacaciones.value)
    }

    // 4. NUEVO: Actualizar seguridad (cuenta)
    // Asumo que tu tabla de acceso se llama "cuenta" y el campo "primer_inicio_sesion"
    if (form.value.reiniciar_password) {
      const { error: errorCuenta } = await supabase.from('cuenta').update({
        primer_inicio: true
      }).eq('id_empleado', route.params.id)
      
      if (errorCuenta) throw new Error('Error al forzar reinicio de clave: ' + errorCuenta.message)
    }

    exitoMsg.value = 'Expediente actualizado correctamente.'
    setTimeout(() => router.push(`/empleado/${route.params.id}`), 1500)

  } catch (e) {
    errorMsg.value = e.message
  }

  guardando.value = false
}
</script>