import { createRouter, createWebHistory } from 'vue-router'
import { supabase } from '../lib/supabase'

//importaciones de vistas
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Departamentos from '../views/Departamentos.vue'
import Directorio from '../views/Directorio.vue'
import DepartamentoDetalle from '../views/DepartamentoDetalle.vue'
import PerfilEmpleado from '../views/PerfilEmpleado.vue'
import NuevoEmpleado from '../views/NuevoEmpleado.vue'
import Expediente from '../views/Expediente.vue'
import Notificaciones from '../views/Notificaciones.vue'
import Scanner from '../views/Scanner.vue'

const routes = [
  { path: '/',                    component: Login },
  { path: '/dashboard',           component: Dashboard },
  { path: '/departamentos',       component: Departamentos },
  { path: '/directorio',          component: Directorio },
  { path: '/departamentos/:id',   component: DepartamentoDetalle },
  { path: '/empleado/:id',        component: PerfilEmpleado },
  { path: '/nuevo-empleado',      component: NuevoEmpleado },
  { path: '/expediente/:id',      component: Expediente },
  { path: '/notificaciones',      component: Notificaciones },
  { path: '/scanner',             component: Scanner },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Validación de seguridad y roles
router.beforeEach(async (to) => {
  const { data: { session } } = await supabase.auth.getSession()

  // 1. Si ya tiene sesión activa y trata de entrar al Login, enviarlo al Dashboard
  if (session && to.path === '/') {
    return '/dashboard'
  }

  // 2. Si NO hay sesión y la ruta no es pública (excluimos el '/' y el '/scanner')
  if (!session && to.path !== '/' && to.path !== '/scanner') {
    return '/'
  }

  // 3. Validación de Gerente 
  if (session && to.path !== '/' && to.path !== '/scanner') {
    const { data, error } = await supabase
      .from('empleado')
      .select('id_departamento')
      .eq('email', session.user.email)
      .limit(1)
      .single()

    // Si hubo un error o el empleado tiene departamento (no es gerente)
    if (error || (data && data.id_departamento !== null)) {
      await supabase.auth.signOut()
      return '/'
    }
  }
})

export default router