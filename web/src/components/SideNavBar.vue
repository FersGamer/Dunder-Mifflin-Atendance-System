<template>
  <nav class="fixed left-0 top-0 h-full w-64 border-r border-outline-variant bg-secondary-fixed dark:bg-tertiary-container shadow-[2px_0_0_0_rgba(116,119,127,0.1)] hidden md:flex flex-col py-unit z-40">
    <div class="px-gutter mb-6 pt-4">
      <h1 class="font-headline-md text-headline-md text-primary">Dunder Mifflin</h1>
    </div>
    <div class="px-gutter mb-6 flex items-center gap-3">
      <img :src="usuarioStore.avatar" alt="Avatar" class="w-12 h-12 rounded-full border border-outline"/>
      <div>
        <p class="font-label-caps text-label-caps text-primary">{{ usuarioStore.nombre }}</p>
        <p class="font-body-sm text-body-sm text-on-surface-variant">Regional Manager</p>
      </div>
    </div>
    <div class="px-gutter mb-8">
      <button class="w-full bg-primary text-on-primary border border-outline-variant py-2 rounded font-label-caps text-label-caps hover:bg-primary-container transition-colors">
        New Filing
      </button>
    </div>
    <ul class="flex-1 px-unit space-y-1">
      <li v-for="item in navItems" :key="item.path">
        <RouterLink :to="item.path"
          class="flex items-center gap-3 px-3 py-2 rounded font-label-caps text-label-caps transition-all"
          :class="$route.path === item.path
            ? 'bg-surface text-primary border-y border-l border-outline-variant font-bold'
            : 'text-on-secondary-fixed-variant hover:bg-secondary-container'">
          <span class="material-symbols-outlined" :style="$route.path === item.path ? 'font-variation-settings: FILL 1' : ''">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </RouterLink>
      </li>
    </ul>
    <div class="mt-auto px-unit space-y-1 border-t border-outline-variant pt-4">
      <a class="flex items-center gap-3 px-3 py-2 rounded text-on-secondary-fixed-variant hover:bg-secondary-container transition-all" href="#">
        <span class="material-symbols-outlined">settings</span>
        <span class="font-label-caps text-label-caps">Settings</span>
      </a>
      <button @click="handleLogout" class="flex items-center gap-3 px-3 py-2 rounded text-on-secondary-fixed-variant hover:bg-secondary-container transition-all w-full">
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
  { path: '/dashboard',      icon: 'dashboard',       label: 'Dashboard' },
  { path: '/departamentos',  icon: 'business_center', label: 'Departamentos' },
  { path: '/directorio',     icon: 'groups',          label: 'Empleados' },
  { path: '/notificaciones', icon: 'notifications',   label: 'Notificaciones' },
  { path: '/nuevo-empleado', icon: 'person_add',      label: 'Nuevo Empleado' },
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