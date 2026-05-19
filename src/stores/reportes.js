// stores/reportesStore.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { supabase } from '@/lib/supabase'

export const useReportes= defineStore('reportes', () => {
  // ── State ────────────────────────────────────────────────────────────────
  const generando = ref(false)
  const error = ref(null)
  const ultimoTotal = ref(null)

  // ── Actions ──────────────────────────────────────────────────────────────
  async function exportarCSV({ departamento, fechaInicio, fechaFin, tipo }) {
    generando.value = true
    error.value = null
    ultimoTotal.value = null

    try {
      let filas, nombre

      if (tipo === 'solicitudes') {
        const rows = await _fetchSolicitudes({ departamento, fechaInicio, fechaFin })
        filas = _serializarSolicitudes(rows)
        nombre = `solicitudes_${fechaInicio ?? 'todo'}_${fechaFin ?? ''}.csv`
      } else {
        const rows = await _fetchAsistencias({ departamento, fechaInicio, fechaFin, tipo })
        filas = _serializarAsistencias(rows)
        nombre = `reporte_${tipo === 'excepciones' ? 'excepciones' : 'asistencia'}_${fechaInicio ?? 'todo'}.csv`
      }

      _descargar(_toCSV(filas), nombre)
      ultimoTotal.value = filas.length - 1 // sin contar header
      return true
    } catch (err) {
      error.value = err.message ?? 'Error al generar reporte'
      return false
    } finally {
      generando.value = false
    }
  }

  // ── Private fetches ──────────────────────────────────────────────────────
  async function _fetchAsistencias({ departamento, fechaInicio, fechaFin, tipo }) {
    let q = supabase
      .from('asistencias')
      .select(`
        id_asistencias, fecha, hora_entrada, hora_salida, estado, estatus,
        empleado (
          id_empleado, nombres, apellido_paterno, apellido_materno, email,
          departamento ( id_departamento, nombre_departamento )
        )
      `)
      .order('fecha')
      .order('hora_entrada')

    if (fechaInicio) q = q.gte('fecha', fechaInicio)
    if (fechaFin)    q = q.lte('fecha', fechaFin)

    const { data, error: err } = await q
    if (err) throw err

    let rows = data ?? []

    if (departamento && departamento !== 'todos') {
      rows = rows.filter(r =>
        r.empleado?.departamento?.nombre_departamento?.toLowerCase() === departamento.toLowerCase()
      )
    }
    if (tipo === 'excepciones') {
      rows = rows.filter(r =>
        ['tardanza', 'salida_temprana'].includes(r.estado) ||
        ['incompleto', 'tardanza'].includes(r.estatus)
      )
    }
    return rows
  }

  async function _fetchSolicitudes({ departamento, fechaInicio, fechaFin }) {
    let q = supabase
      .from('solicitudes_ausencia')
      .select(`
        id_ausencia, fecha_inicio, fecha_fin, aprobacion, fecha_solicitud,
        empleado (
          nombres, apellido_paterno, apellido_materno, email,
          departamento ( nombre_departamento )
        ),
        faltas ( faltas, goce_sueldo )
      `)
      .order('fecha_solicitud', { ascending: false })

    if (fechaInicio) q = q.gte('fecha_solicitud', fechaInicio)
    if (fechaFin)    q = q.lte('fecha_solicitud', fechaFin)

    const { data, error: err } = await q
    if (err) throw err

    let rows = data ?? []
    if (departamento && departamento !== 'todos') {
      rows = rows.filter(r =>
        r.empleado?.departamento?.nombre_departamento?.toLowerCase() === departamento.toLowerCase()
      )
    }
    return rows
  }

  // ── Serializers ──────────────────────────────────────────────────────────
  function _serializarAsistencias(rows) {
    const headers = ['ID', 'Fecha', 'Nombre', 'Departamento', 'Email', 'Hora Entrada', 'Hora Salida', 'Estado', 'Estatus']
    return [
      headers,
      ...rows.map(r => {
        const e = r.empleado ?? {}
        return [
          r.id_asistencias,
          r.fecha,
          `${e.nombres ?? ''} ${e.apellido_paterno ?? ''} ${e.apellido_materno ?? ''}`.trim(),
          e.departamento?.nombre_departamento ?? '',
          e.email ?? '',
          r.hora_entrada ?? '',
          r.hora_salida ?? '',
          r.estado ?? '',
          r.estatus ?? '',
        ]
      })
    ]
  }

  function _serializarSolicitudes(rows) {
    const headers = ['ID', 'Empleado', 'Departamento', 'Email', 'Fecha Solicitud', 'Fecha Inicio', 'Fecha Fin', 'Tipo Falta', 'Goce de Sueldo', 'Aprobación']
    return [
      headers,
      ...rows.map(r => {
        const e = r.empleado ?? {}
        return [
          r.id_ausencia,
          `${e.nombres ?? ''} ${e.apellido_paterno ?? ''}`.trim(),
          e.departamento?.nombre_departamento ?? '',
          e.email ?? '',
          r.fecha_solicitud ?? '',
          r.fecha_inicio ?? '',
          r.fecha_fin ?? '',
          r.faltas?.faltas ?? '',
          r.faltas?.goce_sueldo ? 'Sí' : 'No',
          r.aprobacion ?? '',
        ]
      })
    ]
  }

  // ── CSV utils ────────────────────────────────────────────────────────────
  function _toCSV(data) {
    return data
      .map(row =>
        row.map(cell => {
          const s = String(cell ?? '').replace(/"/g, '""')
          return s.includes(',') || s.includes('"') || s.includes('\n') ? `"${s}"` : s
        }).join(',')
      )
      .join('\r\n')
  }

  function _descargar(csv, nombre) {
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = nombre
    a.click()
    URL.revokeObjectURL(url)
  }

  return {
    generando, error, ultimoTotal,
    exportarCSV,
  }
})