import { defineStore } from 'pinia'
import { ref } from 'vue'
import { supabase } from '../lib/supabase'

export const useUsuarioStore = defineStore('usuario', () => {
  const nombre = ref('')
  const avatar = ref('')
  const cargado = ref(false)

  async function cargarUsuario() {
    // Si ya está cargado no vuelve a pedir
    if (cargado.value) return

    const { data: { session } } = await supabase.auth.getSession()
    if (!session) return

    const { data } = await supabase
      .from('empleado')
      .select('nombres, apellido_paterno, foto_url')
      .eq('email', session.user.email)
      .limit(1)

    const empleado = data?.[0]
    if (empleado) {
      nombre.value = `${empleado.nombres} ${empleado.apellido_paterno}`
      avatar.value = empleado.foto_url || ''
      cargado.value = true
    }
  }

  function limpiar() {
    nombre.value = ''
    avatar.value = ''
    cargado.value = false
  }

  return { nombre, avatar, cargado, cargarUsuario, limpiar }
})