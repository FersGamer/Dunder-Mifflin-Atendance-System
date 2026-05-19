<template>
  <nav class="md:hidden fixed bottom-0 left-0 w-full flex justify-around items-center px-4 pb-4 pt-2 border-t border-outline-variant bg-surface shadow-[0_-2px_0_0_rgba(116,119,127,0.1)] z-50">
    <RouterLink v-for="item in navItems" :key="item.path" :to="item.path"
      class="flex flex-col items-center justify-center p-1 font-label-caps text-[10px] uppercase transition-all"
      :class="$route.path === item.path
        ? 'text-primary bg-secondary-container rounded-xl'
        : 'text-on-surface-variant hover:bg-surface-container-low'">
      <span class="material-symbols-outlined" :style="$route.path === item.path ? 'font-variation-settings: FILL 1' : ''">{{ item.icon }}</span>
      <span class="mt-1">{{ item.label }}</span>
    </RouterLink>
  </nav>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const navItems = [
  { path: '/dashboard',      icon: 'dashboard',       label: 'Home' },
  { path: '/departamentos',  icon: 'business_center', label: 'Depts' },
  { path: '/directorio',     icon: 'groups',          label: 'Staff' },
  { path: '/notificaciones', icon: 'notifications',   label: 'Alerts' },
]
</script>