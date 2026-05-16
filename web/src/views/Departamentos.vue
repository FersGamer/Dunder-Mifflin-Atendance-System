<template>
  <div class="flex min-h-screen">
    <SideNavBar />
    <main class="flex-1 md:ml-64 p-gutter">
      <h1 class="font-headline-lg text-headline-lg text-primary">Panel de Departamentos</h1>
    </main>
    <BottomNavBar />
  </div>
</template>

<script setup>
import SideNavBar from '../components/SideNavBar.vue'
import BottomNavBar from '../components/BottomNavBar.vue'
</script>