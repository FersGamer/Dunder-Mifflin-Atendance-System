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
  if (!session && to.path !== '/') return '/'
})

export default router