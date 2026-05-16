<template>
  <div class="bg-background text-on-background min-h-screen flex items-center justify-center p-4">
    <main class="w-full max-w-md bg-surface border border-outline-variant rounded shadow-[2px_2px_0_0_#8C8C8C] p-8 relative">
      <div class="absolute -top-6 left-0 bg-secondary text-on-secondary px-3 py-1 font-label-caps text-label-caps rounded-t border border-b-0 border-outline-variant shadow-[2px_0_0_0_#8C8C8C]">
        FORMULARIO 01-A
      </div>
      <div class="text-center mb-8">
        <h1 class="font-headline-lg text-headline-lg text-primary uppercase tracking-tighter mb-2">Dunder Mifflin</h1>
        <p class="font-body-md text-body-md text-on-surface-variant">Sistema de Control de Asistencia</p>
      </div>
      <div class="space-y-6">
        <div>
          <label class="block font-label-caps text-label-caps text-on-surface mb-2 uppercase" for="email">Correo Electrónico Corporativo</label>
          <div class="relative">
            <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">mail</span>
            <input v-model="email" class="w-full pl-10 pr-4 py-3 bg-surface-bright border-b-2 border-outline focus:border-primary focus:ring-0 font-body-md text-body-md text-on-surface placeholder-outline-variant transition-colors" id="email" placeholder="empleado@dundermifflin.com" type="email"/>
          </div>
        </div>
        <div>
          <label class="block font-label-caps text-label-caps text-on-surface mb-2 uppercase" for="password">Contraseña</label>
          <div class="relative">
            <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline">lock</span>
            <input v-model="password" class="w-full pl-10 pr-4 py-3 bg-surface-bright border-b-2 border-outline focus:border-primary focus:ring-0 font-body-md text-body-md text-on-surface placeholder-outline-variant transition-colors" id="password" placeholder="••••••••" type="password"/>
          </div>
          <div class="mt-2 flex justify-end">
            <a class="font-body-sm text-body-sm text-primary hover:underline" href="#">¿Olvidó su contraseña?</a>
          </div>
        </div>
        <div v-if="errorMsg" class="bg-error-container text-on-error-container p-3 rounded flex items-start gap-2 border border-error">
          <span class="material-symbols-outlined">error</span>
          <p class="font-body-sm text-body-sm">{{ errorMsg }}</p>
        </div>
        <button @click="handleLogin" class="w-full bg-primary text-on-primary font-label-caps text-label-caps uppercase py-4 rounded hover:bg-primary-container hover:text-on-primary-container transition-colors shadow-[2px_2px_0_0_#8C8C8C] active:translate-x-px active:translate-y-px active:shadow-[1px_1px_0_0_#8C8C8C]">
          {{ loading ? 'Verificando...' : 'Iniciar sesión' }}
        </button>
      </div>
      <div class="mt-8 text-center border-t border-outline-variant pt-6">
        <p class="font-memo-mono text-memo-mono text-on-surface-variant text-xs">
          Uso exclusivo para personal autorizado de Dunder Mifflin Paper Co.<br/>
          Cualquier acceso no autorizado será reportado a Recursos Humanos.
        </p>
      </div>
      <div class="absolute bottom-4 right-4 opacity-20 hover:opacity-100 transition-opacity" title="World's Best Boss">
        <img alt="Coffee mug" class="w-12 h-12 object-cover rounded-full border border-outline grayscale" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBOpAAVnnm-zF6Y9wJMsUTtmdoTSkrwo1krEfRFMYoXp4R9okFGtuxWYfP90irFkbty2VneG3FV2QslExaicZ4Ueslw1wuLbOAn652nbarUGk-GG4no0RMc95f_ZMZJH2SRjTfGO1uMpHcEFIzjHBMbTyNQpc8YDEjWC8gVKxd03vCpqryQSWB5OT5O24FVSM54eqtkGC9vL3FyHfpmmzcsuY3y07Tjz0b2VGrfYcHoChp0L1JG3gSVc7b9dNnxdKnSrCqLQ86gRdUE"/>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { supabase } from '../lib/supabase'

const router = useRouter()
const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  loading.value = true
  errorMsg.value = ''
  const { error } = await supabase.auth.signInWithPassword({
    email: email.value,
    password: password.value
  })
  if (error) {
    errorMsg.value = 'Credenciales incorrectas. Intento registrado.'
  } else {
    router.push('/dashboard')
  }
  loading.value = false
}
</script>