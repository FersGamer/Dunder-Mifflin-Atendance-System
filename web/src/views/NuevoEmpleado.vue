<template>
  <div class="bg-background text-on-background min-h-screen flex flex-col font-body-md text-body-md selection:bg-primary-fixed selection:text-on-primary-fixed">
    <!-- TopAppBar -->
    <header class="bg-surface border-b border-outline-variant flex justify-between items-center w-full px-margin-edge h-16 sticky top-0 z-50">
      <div class="font-headline-md text-headline-md text-primary uppercase tracking-tighter">Dunder Mifflin Paper Co.</div>
      <div class="flex items-center gap-4">
        <span class="font-label-caps text-label-caps text-on-surface-variant">Clock Out</span>
        <span class="material-symbols-outlined text-on-surface-variant cursor-pointer hover:bg-surface-container-high transition-colors p-2 rounded-full">account_circle</span>
      </div>
    </header>

    <div class="flex flex-1">
      <SideNavBar />
      <main class="flex-1 md:ml-64 p-margin-edge pb-24 md:pb-margin-edge">
        <!-- Stepper -->
        <div class="bg-surface border border-outline-variant p-6 mb-8 shadow-[2px_2px_0px_0px_#8C8C8C] relative">
          <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed">FORMULARIO RH-07</div>
          <div class="flex justify-between items-center z-10 relative mt-2">
            <div v-for="(paso, i) in pasos" :key="i" class="flex flex-col items-center">
              <div class="w-8 h-8 rounded-full flex items-center justify-center font-label-caps text-label-caps mb-2 shadow-[2px_2px_0px_0px_#8C8C8C]"
                :class="pasoActual === i + 1 ? 'bg-primary text-on-primary' : 'bg-surface text-on-surface-variant border border-outline-variant'">
                {{ i + 1 }}
              </div>
              <span class="font-label-caps text-label-caps" :class="pasoActual === i + 1 ? 'text-primary' : 'text-on-surface-variant'">{{ paso }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Formulario -->
          <div class="lg:col-span-2 bg-surface border border-outline-variant p-8 shadow-[2px_2px_0px_0px_#8C8C8C] relative">
            <div class="absolute top-0 left-0 bg-secondary-fixed px-3 py-1 border-r border-b border-outline-variant font-label-caps text-label-caps text-on-secondary-fixed">SECCIÓN {{ pasoActual }}</div>

            <!-- Paso 1: Datos Personales -->
            <div v-if="pasoActual === 1">
              <h2 class="font-headline-lg text-headline-lg text-primary mt-4 mb-6">Datos Personales</h2>
              <div class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Nombre(s)</label>
                    <input v-model="form.nombres" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="Ej. Dwight Kurt" type="text"/>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Paterno</label>
                    <input v-model="form.apellido_paterno" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="Ej. Schrute" type="text"/>
                  </div>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Apellido Materno</label>
                    <input v-model="form.apellido_materno" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="Ej. K." type="text"/>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Correo Electrónico</label>
                    <input v-model="form.email" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="ejemplo@dundermifflin.com" type="email"/>
                  </div>
                </div>
              </div>
            </div>

            <!-- Paso 2: Datos Laborales -->
            <div v-if="pasoActual === 2">
              <h2 class="font-headline-lg text-headline-lg text-primary mt-4 mb-6">Datos Laborales</h2>
              <div class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Fecha de Contratación</label>
                    <input v-model="form.fecha_contratacion" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" type="date"/>
                  </div>
                  <div class="flex flex-col">
                    <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Sueldo</label>
                    <input v-model="form.sueldo" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="0.00" type="number"/>
                  </div>
                </div>
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Departamento</label>
                  <select v-model="form.id_departamento" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface">
                    <option value="">Seleccionar departamento...</option>
                    <option v-for="dept in departamentos" :key="dept.id_departamento" :value="dept.id_departamento">
                      {{ dept.nombre_departamento }}
                    </option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Paso 3: Acceso -->
            <div v-if="pasoActual === 3">
              <h2 class="font-headline-lg text-headline-lg text-primary mt-4 mb-6">Acceso al Sistema</h2>
              <div class="space-y-6">
                <div class="flex flex-col">
                  <label class="font-label-caps text-label-caps text-on-surface-variant mb-2 uppercase">Contraseña Temporal</label>
                  <input v-model="form.password" class="bg-surface border-0 border-b-2 border-outline-variant focus:ring-0 focus:border-primary px-0 py-2 font-body-md text-body-md text-on-surface" placeholder="Mínimo 8 caracteres" type="password"/>
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
            </div>

            <!-- Botones navegación -->
            <div class="mt-10 flex justify-between items-center border-t border-outline-variant/50 pt-6">
              <button @click="pasoAnterior" :disabled="pasoActual === 1"
                class="font-label-caps text-label-caps px-6 py-2 border border-outline-variant text-on-surface-variant uppercase transition-colors"
                :class="pasoActual === 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-surface-container'">
                Anterior
              </button>
              <button v-if="pasoActual < 3" @click="pasoSiguiente"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary border border-primary hover:bg-primary/90 transition-colors uppercase flex items-center gap-2">
                Siguiente Paso
                <span class="material-symbols-outlined text-body-md">arrow_forward</span>
              </button>
              <button v-if="pasoActual === 3" @click="registrarEmpleado" :disabled="loading"
                class="font-label-caps text-label-caps px-6 py-2 bg-primary text-on-primary border border-primary hover:bg-primary/90 transition-colors uppercase flex items-center gap-2">
                {{ loading ? 'Registrando...' : 'Registrar Empleado' }}
                <span class="material-symbols-outlined text-body-md">check</span>
              </button>
            </div>
          </div>

          <!-- Vista previa gafete -->
          <div class="lg:col-span-1">
            <div class="bg-surface-container-low border border-outline-variant p-6 shadow-[2px_2px_0px_0px_#8C8C8C] sticky top-24">
              <div class="flex items-center gap-2 mb-6 pb-4 border-b border-outline-variant">
                <span class="material-symbols-outlined text-on-surface-variant">badge</span>
                <h3 class="font-label-caps text-label-caps text-on-surface-variant uppercase tracking-widest">Vista Previa Gafete</h3>
              </div>
              <div class="bg-surface border border-outline-variant p-6 flex flex-col items-center text-center">
                <div class="w-24 h-24 bg-surface-variant rounded border border-outline-variant mb-4 flex items-center justify-center overflow-hidden">
                  <img alt="Placeholder Gafete" class="w-full h-full object-cover grayscale opacity-50" src="https://lh3.googleusercontent.com/aida-public/AB6AXuD5ShG3HeGB22V11V6qlK5tFnm1LeNETNCQRz3V5eEwVBBwaBCB1KvxeObpo7bq3AijTJPxaUjlWXA4t_KhuWovcoLfhC0GP1H3rJXHt3Uw2Ce-3dt-9eVk4DfMWmdbfgAOtyZkN7Z-s2eHeBw4ncFVCeP2LefjHYgMEy3qMQziujKVQsTX5Zec6hypRSoCWnbH0veXwKmYe0WOPS0g4poJ44Kb6GmWb-RdfwwWF3rYgB3dYrJzLGSCmD6AGqFKdTrQyWniSI3ZCsVr"/>
                </div>
                <div class="font-headline-md text-headline-md text-primary w-full border-b border-outline-variant pb-2 mb-2 min-h-[32px] truncate">
                  {{ form.nombres || 'Nombre' }} {{ form.apellido_paterno || 'Apellidos' }}
                </div>
                <div class="font-label-caps text-label-caps text-on-surface-variant mb-6 uppercase min-h-4">
                  {{ departamentoSeleccionado || 'PUESTO PENDIENTE' }}
                </div>
                <div class="w-full space-y-2 mt-4 text-left">
                  <div class="flex items-center gap-2">
                    <span class="material-symbols-outlined text-body-md text-outline">mail</span>
                    <span class="font-memo-mono text-memo-mono text-on-surface-variant text-sm min-h-5">{{ form.email || '---' }}</span>
                  </div>
                </div>
              </div>
              <div class="mt-6 font-memo-mono text-memo-mono text-on-surface-variant text-xs text-center italic">
                HR-Notice: La impresión final requerirá aprobación de la gerencia regional.
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const pasoActual = ref(1)
const pasos = ['Datos Personales', 'Datos Laborales', 'Acceso']
const loading = ref(false)
const errorMsg = ref('')
const exitoMsg = ref('')
const departamentos = ref([])

const form = ref({
  nombres: '', apellido_paterno: '', apellido_materno: '',
  email: '', fecha_contratacion: '', sueldo: '',
  id_departamento: '', password: ''
})

const departamentoSeleccionado = computed(() => {
  const dept = departamentos.value.find(d => d.id_departamento === form.value.id_departamento)
  return dept?.nombre_departamento || ''
})

onMounted(async () => {
  const { data } = await supabase.from('departamento').select('id_departamento, nombre_departamento')
  if (data) departamentos.value = data
})

function pasoSiguiente() { if (pasoActual.value < 3) pasoActual.value++ }
function pasoAnterior() { if (pasoActual.value > 1) pasoActual.value-- }

async function registrarEmpleado() {
  loading.value = true
  errorMsg.value = ''
  exitoMsg.value = ''

  // 1. Crear usuario en Supabase Auth
  const { data: authData, error: authError } = await supabase.auth.admin.createUser({
    email: form.value.email,
    password: form.value.password,
    email_confirm: true
  })

  if (authError) {
    errorMsg.value = 'Error al crear acceso: ' + authError.message
    loading.value = false
    return
  }

  // 2. Insertar en tabla empleado
  const { error: empError } = await supabase.from('empleado').insert({
    nombres: form.value.nombres,
    apellido_paterno: form.value.apellido_paterno,
    apellido_materno: form.value.apellido_materno,
    email: form.value.email,
    fecha_contratacion: form.value.fecha_contratacion,
    sueldo: parseFloat(form.value.sueldo),
    id_departamento: form.value.id_departamento || null
  })

  if (empError) {
    errorMsg.value = 'Error al guardar empleado: ' + empError.message
  } else {
    exitoMsg.value = `Empleado ${form.value.nombres} registrado correctamente.`
    form.value = { nombres: '', apellido_paterno: '', apellido_materno: '', email: '', fecha_contratacion: '', sueldo: '', id_departamento: '', password: '' }
    pasoActual.value = 1
  }
  loading.value = false
}
</script>