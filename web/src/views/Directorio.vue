<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 bg-background pb-24 md:pb-0">

      <header class="flex justify-between items-center px-8 h-16 bg-surface border-b border-outline-variant sticky top-0 z-30">
        <div>
          <h2 class="font-headline-md text-headline-md text-primary">Directorio de Personal</h2>
          <p class="font-body-sm text-body-sm text-on-surface-variant">Dunder Mifflin Paper Co. — Scranton, PA</p>
        </div>
        <RouterLink to="/nuevo-empleado"
          class="flex items-center gap-2 bg-primary text-on-primary px-4 py-2 rounded font-label-caps text-label-caps hover:bg-primary-container hover:text-on-primary-container transition-colors shadow-[2px_2px_0_0_#8C8C8C]">
          <span class="material-symbols-outlined text-base">person_add</span>
          Nuevo Empleado
        </RouterLink>
      </header>

      <div class="p-8">
        <!-- Buscador -->
        <div class="relative mb-6">
          <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">search</span>
          <input v-model="busqueda" type="text" placeholder="Buscar empleado..."
            class="w-full pl-10 pr-4 py-3 bg-surface border border-outline-variant focus:border-primary focus:ring-0 font-body-md text-body-md text-on-surface placeholder-outline-variant transition-colors rounded"/>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="text-center py-16 font-body-sm text-body-sm text-on-surface-variant">
          Cargando directorio...
        </div>

        <!-- Grid de empleados -->
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div v-for="emp in empleadosFiltrados" :key="emp.id_empleado"
            class="bg-surface border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] overflow-hidden hover:shadow-[3px_3px_0_0_#8C8C8C] transition-all cursor-pointer"
            @click="router.push(`/empleado/${emp.id_empleado}`)">

            <!-- Foto -->
            <div class="h-48 bg-surface-variant overflow-hidden flex items-center justify-center">
              <img v-if="emp.foto_url" :src="emp.foto_url" :alt="emp.nombres"
                class="w-full h-full object-cover grayscale hover:grayscale-0 transition-all"/>
              <span v-else class="material-symbols-outlined text-6xl text-outline">account_circle</span>
            </div>

            <!-- Info -->
            <div class="p-4 border-t border-outline-variant">
              <div class="flex justify-between items-start mb-1">
                <h3 class="font-label-caps text-label-caps text-primary">
                  {{ emp.nombres }} {{ emp.apellido_paterno }}
                </h3>
              </div>
              <p class="font-body-sm text-body-sm text-on-surface-variant mb-3">
                {{ emp.departamento?.nombre_departamento || 'Gerencia' }}
              </p>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-[14px] text-outline">mail</span>
                <span class="font-memo-mono text-memo-mono text-on-surface-variant text-xs truncate">{{ emp.email }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Sin resultados -->
        <div v-if="!loading && empleadosFiltrados.length === 0" class="text-center py-16">
          <span class="material-symbols-outlined text-6xl text-outline">search_off</span>
          <p class="font-body-md text-body-md text-on-surface-variant mt-4">No se encontraron empleados.</p>
          <p class="font-memo-mono text-memo-mono text-on-surface-variant text-sm mt-1">
            HR-Notice: Quizás Creed los archivó en el lugar equivocado.
          </p>
        </div>
      </div>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { supabase } from '../lib/supabase'
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'

const router = useRouter()
const loading = ref(true)
const empleados = ref([])
const busqueda = ref('')

const empleadosFiltrados = computed(() => {
  if (!busqueda.value) return empleados.value
  const q = busqueda.value.toLowerCase()
  return empleados.value.filter(e =>
    `${e.nombres} ${e.apellido_paterno} ${e.email}`.toLowerCase().includes(q)
  )
})

onMounted(async () => {
  const { data } = await supabase
    .from('empleado')
    .select(`
      id_empleado, nombres, apellido_paterno, email, foto_url,
      departamento ( nombre_departamento )
    `)
    .order('apellido_paterno')

  if (data) empleados.value = data
  loading.value = false
})
</script>