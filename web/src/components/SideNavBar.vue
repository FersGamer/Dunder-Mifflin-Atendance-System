<template>
  <nav class="fixed left-0 top-0 h-full w-64 bg-[#05347E] text-white shadow-lg hidden md:flex flex-col py-unit z-40">
    
    <div class="px-gutter mb-6 pt-4 justify-center flex">
      <img src="../assets/dunder-mifflin-logo.png" 
         alt="Dunder Mifflin Logo" 
         class="h-18 w-auto object-contain" />
    </div>

    <div class="px-gutter mb-8 flex flex-col items-center gap-4 text-center border-b border-white/10 pb-6">
      <img :src="usuarioStore.avatar" alt="Avatar" class="w-24 h-24 rounded-full border-4 border-[#C8DEF5] shadow-lg"/>
      <div>
        <p class="font-headline-sm text-headline-sm text-white font-bold">{{ usuarioStore.nombre }}</p>
        <p class="font-body-md text-body-md text-[#C8DEF5]">Regional Manager</p>
      </div>
    </div>

    <ul class="flex-1 px-unit space-y-1">
      <li v-for="item in navItems" :key="item.path">
        <RouterLink :to="item.path"
          class="flex items-center gap-3 px-3 py-2 mx-2 rounded-md font-label-caps text-label-caps transition-all"
          :class="$route.path === item.path
            ? 'bg-white text-[#05347E] font-bold shadow' 
            : 'text-[#C8DEF5] hover:bg-white/10 hover:text-white'">
          <span class="material-symbols-outlined" :style="$route.path === item.path ? 'font-variation-settings: FILL 1' : ''">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </RouterLink>
      </li>
    </ul>

    <div class="mt-auto px-unit space-y-1 border-t border-white/20 pt-4 pb-4">
      <a class="flex items-center gap-3 px-3 py-2 mx-2 rounded text-[#C8DEF5] hover:bg-white/10 hover:text-white transition-all" href="#">
        <span class="material-symbols-outlined">settings</span>
        <span class="font-label-caps text-label-caps">Settings</span>
      </a>
      <button @click="handleLogout" class="flex items-center gap-3 px-3 py-2 mx-2 rounded text-[#C8DEF5] hover:bg-white/10 hover:text-white transition-all w-full text-left">
        <span class="material-symbols-outlined">logout</span>
        <span class="font-label-caps text-label-caps">Cerrar Sesión</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { onMounted } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { supabase } from '../lib/supabase'
import { useUsuarioStore } from '../stores/usuario'

const route = useRoute()
const router = useRouter()
const usuarioStore = useUsuarioStore()

const navItems = [
  { path: '/dashboard',      icon: 'dashboard',       label: 'Dashboard' },
  { path: '/departamentos',  icon: 'business_center', label: 'Departamentos' },
  { path: '/directorio',     icon: 'groups',          label: 'Empleados' },
  { path: '/notificaciones', icon: 'notifications',   label: 'Notificaciones' },
]

onMounted(async () => {
  await usuarioStore.cargarUsuario()
})

async function handleLogout() {
  usuarioStore.limpiar()
  await supabase.auth.signOut()
  router.push('/')
}
</script>