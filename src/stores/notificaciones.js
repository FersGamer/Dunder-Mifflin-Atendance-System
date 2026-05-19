import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { supabase } from "@/lib/supabase";

export const useNotificaciones = defineStore("notificaciones", () => {
  // ── State ────────────────────────────────────────────────────────────────
  const notificaciones = ref([]);
  const loading = ref(false);
  const error = ref(null);
  let suscripciones = [];

  // ── Getters ──────────────────────────────────────────────────────────────
  const pendientesCount = computed(
    () =>
      notificaciones.value.filter(
        (n) => n.tipo === "solicitud" && n.aprobacion === "Pendiente",
      ).length,
  );

  const porFiltro = computed(() => (filtro) => {
    if (filtro === "entradas")
      return notificaciones.value.filter((n) => n.tipo === "entrada");
    if (filtro === "salidas")
      return notificaciones.value.filter((n) => n.tipo === "salida");
    if (filtro === "solicitudes")
      return notificaciones.value.filter((n) => n.tipo === "solicitud");
    return notificaciones.value;
  });

  // ── Actions ──────────────────────────────────────────────────────────────
  async function inicializar() {
    loading.value = true;
    error.value = null;
    await Promise.all([_cargarAsistencias(), _cargarSolicitudes()]);
    _suscribirRealtime();
    loading.value = false;
  }

  function limpiar() {
    suscripciones.forEach((s) => supabase.removeChannel(s));
    suscripciones = [];
    notificaciones.value = [];
  }

  async function responderSolicitud(notif, decision) {
    const { error: err } = await supabase
      .from("solicitudes_ausencia")
      .update({ aprobacion: decision })
      .eq("id_ausencia", notif._idAusencia)

    if (err) {
      error.value = err.message
      return false
    }

    // Si se aprueba, actualizar días consumidos en saldo_vacaciones
    if (decision === "Aprobada") {
      const idEmpleado = notif.raw?.empleado?.id_empleado
      const tipoFalta = notif.raw?.faltas?.faltas || ""

      // Validación insensible a mayúsculas para "vacaciones"
      const esVacaciones = tipoFalta.toLowerCase().includes("vacacion")

      if (esVacaciones && idEmpleado) {
        // Usamos la nueva función utilitaria de días hábiles
        const diasSolicitados = _calcDiasHabiles(notif.raw?.fecha_inicio, notif.raw?.fecha_fin)

        const { data: saldo } = await supabase
          .from("saldo_vacaciones")
          .select("id_vacaciones, dias_consumidos")
          .eq("id_empleado", idEmpleado)
          .single()

        if (saldo) {
          await supabase
            .from("saldo_vacaciones")
            .update({ dias_consumidos: (saldo.dias_consumidos || 0) + diasSolicitados })
            .eq("id_vacaciones", saldo.id_vacaciones)
        }
      }
    }

    // Actualizar el estado directamente en la lista local del store
    const idx = notificaciones.value.findIndex((n) => n.id === notif.id)
    if (idx !== -1) {
      notificaciones.value[idx] = {
        ...notificaciones.value[idx],
        aprobacion: decision,
      }
    }
    return true
  }

  // ── Private ──────────────────────────────────────────────────────────────
  async function _cargarAsistencias() {
    const ayer = new Date(Date.now() - 86400000).toISOString().split("T")[0];
    const { data, error: err } = await supabase
      .from("asistencias")
      .select(
        `
        id_asistencias, fecha, hora_entrada, hora_salida, estado, estatus,
        empleado ( id_empleado, nombres, apellido_paterno, id_departamento,
          departamento ( nombre_departamento ) )
      `,
      )
      .gte("fecha", ayer)
      .order("fecha", { ascending: false })
      .order("hora_entrada", { ascending: false })
      .limit(30);

    if (err) {
      error.value = err.message;
      return;
    }
    const ids = new Set(notificaciones.value.map((n) => n._sourceId));
    (data ?? []).forEach((a) => {
      const n = _asistenciaANotif(a);
      if (!ids.has(n._sourceId)) notificaciones.value.push(n);
    });
  }

  async function _cargarSolicitudes() {
    const { data, error: err } = await supabase
      .from("solicitudes_ausencia")
      .select(
        `
        id_ausencia, fecha_inicio, fecha_fin, aprobacion, fecha_solicitud, descripcion,
        faltas ( faltas, goce_sueldo ),
        empleado ( id_empleado, nombres, apellido_paterno, id_departamento,
          departamento ( nombre_departamento ) )
      `,
      )
      .eq("aprobacion", "Pendiente")
      .order("fecha_solicitud", { ascending: false });

    if (err) {
      error.value = err.message;
      return;
    }
    const ids = new Set(notificaciones.value.map((n) => n._sourceId));
    (data ?? []).forEach((s) => {
      const n = _solicitudANotif(s);
      if (!ids.has(n._sourceId)) notificaciones.value.push(n);
    });
  }

  function _suscribirRealtime() {
    // Unificamos las suscripciones en un canal global para evitar fugas de memoria
    const canalGlobal = supabase
      .channel("notificaciones-realtime-all")
      .on(
        "postgres_changes",
        { event: "INSERT", schema: "public", table: "asistencias" },
        async ({ new: row }) => {
          const { data } = await supabase
            .from("asistencias")
            .select(
              `id_asistencias, fecha, hora_entrada, hora_salida, estado, estatus,
              empleado ( id_empleado, nombres, apellido_paterno, id_departamento,
                departamento ( nombre_departamento ) )`,
            )
            .eq("id_asistencias", row.id_asistencias)
            .single();
          if (data) notificaciones.value.unshift(_asistenciaANotif(data));
        },
      )
      .on(
        "postgres_changes",
        { event: "UPDATE", schema: "public", table: "solicitudes_ausencia" },
        ({ new: row }) => {
          const sourceId = `solic_${row.id_ausencia}`
          const idx = notificaciones.value.findIndex(n => n._sourceId === sourceId)
          if (idx !== -1) {
            notificaciones.value[idx] = {
              ...notificaciones.value[idx],
              aprobacion: row.aprobacion
            }
          }
        }
      )
      .on(
        "postgres_changes",
        { event: "INSERT", schema: "public", table: "solicitudes_ausencia" },
        async ({ new: row }) => {
          const { data } = await supabase
            .from("solicitudes_ausencia")
            .select(`
              id_ausencia, fecha_inicio, fecha_fin, aprobacion, fecha_solicitud, descripcion,
              faltas ( faltas, goce_sueldo ),
              empleado ( id_empleado, nombres, apellido_paterno, id_departamento,
                departamento ( nombre_departamento ) )
            `)
            .eq("id_ausencia", row.id_ausencia)
            .single()
          if (data) notificaciones.value.unshift(_solicitudANotif(data))
        },
      )
      .subscribe() // Encadenado de manera segura al final

    suscripciones = [canalGlobal];
  }

  // ── Mappers ──────────────────────────────────────────────────────────────
  function _nombreEmpleado(emp) {
    if (!emp) return "Empleado";
    return `${emp.nombres?.charAt(0) ?? ""}. ${emp.apellido_paterno ?? ""}`;
  }

  function _asistenciaANotif(a) {
    const emp = a.empleado;
    const dept = emp?.departamento?.nombre_departamento ?? "Sin departamento";
    const esEntrada = !!a.hora_entrada && !a.hora_salida;
    const esTemprano =
      a.estado === "salida_temprana" || a.estatus === "incompleto";

    return {
      id: `notif_${Date.now()}_${Math.random().toString(36).slice(2)}`,
      _sourceId: `asist_${a.id_asistencias}`,
      tipo: esEntrada ? "entrada" : "salida",
      icono: esEntrada ? "login" : "logout",
      color: esEntrada ? "punctual" : esTemprano ? "absence" : "punctual",
      etiqueta: esEntrada
        ? "Registro de Entrada"
        : esTemprano
          ? "Registro de Salida (Temprano)"
          : "Registro de Salida",
      titulo: `${_nombreEmpleado(emp)} - ${dept}`,
      descripcion: esEntrada
        ? "Registro de entrada completado exitosamente. Cumplimiento de horario verificado."
        : esTemprano
          ? "Salida registrada antes del horario establecido. Generar reporte de incidencia para RH."
          : "Salida registrada correctamente.",
      horaTexto: _formatHora(esEntrada ? a.hora_entrada : a.hora_salida),
      raw: a,
    };
  }

  // Se agregaron los campos faltantes a la composición de la descripción
  function _solicitudANotif(s) {
    const emp = s.empleado;
    const dept = emp?.departamento?.nombre_departamento ?? "Sin departamento";
    const dias = _calcDiasHabiles(s.fecha_inicio, s.fecha_fin);

    return {
      id: `notif_${Date.now()}_${Math.random().toString(36).slice(2)}`,
      _sourceId: `solic_${s.id_ausencia}`,
      _idAusencia: s.id_ausencia,
      tipo: "solicitud",
      icono: "event_note",
      color: "delay",
      etiqueta: "Solicitud PTO",
      titulo: `${_nombreEmpleado(emp)}: Permiso de Ausencia`,
      descripcion: s.descripcion
        ? `${s.descripcion} — ${dias} día${dias !== 1 ? 's' : ''}. Fechas: ${_formatFecha(s.fecha_inicio)} – ${_formatFecha(s.fecha_fin)}.`
        : `Solicita ${dias} día${dias !== 1 ? 's' : ''}. Fechas: ${_formatFecha(s.fecha_inicio)} – ${_formatFecha(s.fecha_fin)}.`,
      horaTexto: _formatRelativa(s.fecha_solicitud),
      aprobacion: s.aprobacion ?? "Pendiente",
      raw: s,
    };
  }

  // ── Utils ────────────────────────────────────────────────────────────────
  function _formatHora(t) {
    if (!t) return "";
    const [h, m] = t.split(":");
    const hr = parseInt(h);
    return `${hr > 12 ? hr - 12 : hr}:${m} ${hr >= 12 ? "PM" : "AM"}`;
  }
  function _formatFecha(d) {
    if (!d) return "";
    const [, m, day] = d.split("-");
    return `${parseInt(day)}/${parseInt(m)}`;
  }
  function _formatRelativa(d) {
    if (!d) return "";
    const diff = Math.floor((Date.now() - new Date(d)) / 86400000);
    if (diff === 0) return "Hoy";
    if (diff === 1) return "Ayer";
    if (diff < 7) return `Hace ${diff} días`;
    return new Date(d).toLocaleDateString("es-MX", {
      day: "numeric",
      month: "short",
    });
  }

  // NUEVA LÓGICA DE NEGOCIO: Cuenta únicamente días laborales (Lunes a Viernes)
  function _calcDiasHabiles(a, b) {
    if (!a || !b) return 1;
    const inicio = new Date(a + "T00:00:00");
    const fin = new Date(b + "T00:00:00");
    
    let diasHabiles = 0;
    let current = new Date(inicio);

    while (current <= fin) {
      const dayOfWeek = current.getDay();
      // 0 = Domingo, 6 = Sábado
      if (dayOfWeek !== 0 && dayOfWeek !== 6) {
        diasHabiles++;
      }
      current.setDate(current.getDate() + 1);
    }
    return Math.max(1, diasHabiles);
  }

  return {
    notificaciones,
    loading,
    error,
    pendientesCount,
    porFiltro,
    inicializar,
    limpiar,
    responderSolicitud,
  };
});