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
  // Solo se ejecuta si tiene sesión y está navegando por rutas administrativas protegidas
  if (session && to.path !== '/' && to.path !== '/scanner') {
    const { data, error } = await supabase
      .from('empleado')
      .select('id_departamento')
      .eq('email', session.user.email)
      .limit(1)
      .single() // Agregamos .single() para que devuelva el objeto directo, no un arreglo

    // Si hubo un error en la red o el empleado tiene departamento (no es gerente)
    if (error || (data && data.id_departamento !== null)) {
      await supabase.auth.signOut()
      // Opcional: Podrías alertar por qué lo sacaste antes de redirigir
      return '/'
    }
  }
})