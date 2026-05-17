<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <header class="flex items-center gap-4 px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <button @click="router.back()" class="text-on-surface-variant hover:text-primary transition-colors">
          <span class="material-symbols-outlined">arrow_back</span>
        </button>
        <h2 class="font-headline-md text-headline-md text-primary">Perfil de Empleado</h2>
      </header>

      <div v-if="loading" class="text-center py-16 font-body-sm text-body-sm text-on-surface-variant">
        Cargando expediente...
      </div>

      <div v-else-if="empleado" class="p-8 max-w-4xl mx-auto">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">

          <!-- Foto y datos principales -->
          <div class="md:col-span-1">
            <div class="bg-surface border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] overflow-hidden">
              <div class="h-64 bg-surface-variant flex items-center justify-center overflow-hidden">
                <img v-if="empleado.foto_url" :src="empleado.foto_url" :alt="empleado.nombres"
                  class="w-full h-full object-cover"/>
                <span v-else class="material-symbols-outlined text-8xl text-outline">account_circle</span>
              </div>
              <div class="p-4 border-t border-outline-variant">
                <h3 class="font-headline-md text-headline-md text-primary">
                  {{ empleado.nombres }} {{ empleado.apellido_paterno }} {{ empleado.apellido_materno }}
                </h3>
                <p class="font-label-caps text-label-caps text-on-surface-variant mt-1">
                  {{ empleado.departamento?.nombre_departamento || 'Gerencia' }}
                </p>
              </div>
            </div>
          </div>

          <!-- Detalles -->
          <div class="md:col-span-2 space-y-6">

            <!-- Datos personales -->
            <div class="bg-surface border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] p-6 relative">
              <div class="absolute top-0 left-0 bg-surface-container-highest font-label-caps text-label-caps px-2 py-1 border-r border-b border-outline-variant rounded-br text-on-surface-variant">
                DATOS PERSONALES
              </div>
              <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <p class="font-label-caps text-label-caps text-on-surface-variant uppercase">Correo</p>
                  <p class="font-body-md text-body-md text-on-surface">{{ empleado.email }}</p>
                </div>
                <div>
                  <p class="font-label-caps text-label-caps text-on-surface-variant uppercase">Fecha de Contratación</p>
                  <p class="font-body-md text-body-md text-on-surface">{{ formatFecha(empleado.fecha_contratacion) }}</p>
                </div>
                <div>
                  <p class="font-label-caps text-label-caps text-on-surface-variant uppercase">Sueldo</p>
                  <p class="font-body-md text-body-md text-on-surface">${{ empleado.sueldo?.toLocaleString() }} MXN</p>
                </div>
                <div>
                  <p class="font-label-caps text-label-caps text-on-surface-variant uppercase">Departamento</p>
                  <p class="font-body-md text-body-md text-on-surface">{{ empleado.departamento?.nombre_departamento || 'Gerencia' }}</p>
                </div>
              </div>
            </div>

            <!-- Acciones -->
            <div class="flex gap-4">
              <RouterLink :to="`/expediente/${empleado.id_empleado}`"
                class="flex items-center gap-2 bg-primary text-on-primary px-4 py-2 rounded font-label-caps text-label-caps hover:bg-primary-container hover:text-on-primary-container transition-colors shadow-[2px_2px_0_0_#8C8C8C]">
                <span class="material-symbols-outlined text-base">folder_open</span>
                Ver Expediente
              </RouterLink>
              <button @click="confirmarEliminar"
                class="flex items-center gap-2 bg-error text-on-error px-4 py-2 rounded font-label-caps text-label-caps hover:opacity-90 transition-colors shadow-[2px_2px_0_0_#8C8C8C]">
                <span class="material-symbols-outlined text-base">person_remove</span>
                Dar de Baja
              </button>
            </div>

            <!-- Confirmación eliminar -->
            <div v-if="mostrarConfirmar" class="bg-error-container border border-error p-4 rounded">
              <p class="font-body-md text-body-md text-on-error-container mb-4">
                ¿Confirmas dar de baja a {{ empleado.nombres }}? Esta acción no se puede deshacer.
              </p>
              <div class="flex gap-3">
                <button @click="eliminarEmpleado"
                  class="bg-error text-on-error px-4 py-2 rounded font-label-caps text-label-caps">
                  Confirmar Baja
                </button>
                <button @click="mostrarConfirmar = false"
                  class="border border-outline px-4 py-2 rounded font-label-caps text-label-caps text-on-surface-variant">
                  Cancelar
                </button>
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const empleado = ref(null)
const mostrarConfirmar = ref(false)

onMounted(async () => {
  const { data } = await supabase
    .from('empleado')
    .select(`
      id_empleado, nombres, apellido_paterno, apellido_materno,
      email, fecha_contratacion, sueldo, foto_url,
      departamento ( nombre_departamento )
    `)
    .eq('id_empleado', route.params.id)
    .single()

  if (data) empleado.value = data
  loading.value = false
})

function formatFecha(fecha) {
  if (!fecha) return '—'
  return new Date(fecha).toLocaleDateString('es-MX', {
    year: 'numeric', month: 'long', day: 'numeric'
  })
}

function confirmarEliminar() {
  mostrarConfirmar.value = true
}

async function eliminarEmpleado() {
  await supabase.from('cuenta').delete().eq('id_empleado', empleado.value.id_empleado)
  await supabase.from('empleado').delete().eq('id_empleado', empleado.value.id_empleado)
  router.push('/directorio')
}
</script>