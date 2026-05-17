import { createRouter, createWebHistory } from 'vue-router'
import { supabase } from '../lib/supabase'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Departamentos from '../views/Departamentos.vue'
import Directorio from '../views/Directorio.vue'
import DepartamentoDetalle from '../views/DepartamentoDetalle.vue'
import PerfilEmpleado from '../views/PerfilEmpleado.vue'
import NuevoEmpleado from '../views/NuevoEmpleado.vue'
import Expediente from '../views/Expediente.vue'
import Notificaciones from '../views/Notificaciones.vue'

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
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const { data: { session } } = await supabase.auth.getSession()

  // Si no hay sesión y no es la página de login, redirige al login
  if (!session && to.path !== '/') return '/'

  // Si hay sesión, verificar que sea gerente
  if (session && to.path !== '/') {
    const { data } = await supabase
      .from('empleado')
      .select('id_departamento')
      .eq('email', session.user.email)
      .limit(1)

    const empleado = data?.[0]

    // Si tiene departamento asignado, NO es gerente → redirige al login
    if (empleado && empleado.id_departamento !== null) {
      await supabase.auth.signOut()
      return '/'
    }
  }
})

export default router