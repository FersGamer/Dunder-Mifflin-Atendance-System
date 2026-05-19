<template>
  <div class="bg-surface text-on-surface h-screen flex flex-col md:flex-row overflow-hidden font-body-md">
    <header
      class="flex justify-between items-center w-full px-8 h-16 bg-surface border-b border-outline-variant md:hidden">
      <span class="font-headline-md text-headline-md text-primary uppercase tracking-tighter">Dunder Mifflin Paper Co.</span>
    </header>

    <main class="flex-1 flex flex-col md:flex-row w-full overflow-hidden">
      <section
        class="flex-1 bg-surface-dim relative flex flex-col border-b md:border-b-0 md:border-r border-outline-variant p-6">
        <div class="flex justify-between items-center mb-4">
          <div class="flex justify-center -mb-2.5">
            <img src="../assets/dunder-mifflin-logo.png" alt="Dunder Mifflin Logo" class="h-12 w-auto object-contain" />
          </div>
          <h1 class="font-headline-lg text-headline-lg text-primary">
            Estación de Escaneo
          </h1>
          <div class="font-memo-mono text-memo-mono text-on-surface-variant flex items-center gap-2">
            <span class="material-symbols-outlined">schedule</span>
            <span>{{ horaActual }}</span>
          </div>
        </div>

        <div
          class="flex-1 relative bg-ink-black rounded-lg overflow-hidden border border-outline shadow-[2px_2px_0_0_#8C8C8C]">
          <video ref="videoEl" class="absolute inset-0 w-full h-full object-cover opacity-80 grayscale" autoplay muted
            playsinline></video>
          <canvas ref="canvasEl" class="hidden"></canvas>

          <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
            <div
              class="w-64 h-64 border-2 border-primary-fixed border-dashed flex flex-col items-center justify-center relative">
              <div class="absolute top-0 left-0 w-8 h-8 border-t-4 border-l-4 border-status-punctual"></div>
              <div class="absolute top-0 right-0 w-8 h-8 border-t-4 border-r-4 border-status-punctual"></div>
              <div class="absolute bottom-0 left-0 w-8 h-8 border-b-4 border-l-4 border-status-punctual"></div>
              <div class="absolute bottom-0 right-0 w-8 h-8 border-b-4 border-r-4 border-status-punctual"></div>
              <span class="material-symbols-outlined text-primary-fixed text-4xl mb-2 opacity-70">qr_code_scanner</span>
              <p class="font-label-caps text-label-caps text-primary-fixed bg-surface/80 px-2 py-1 rounded">
                Alinear Gafete
              </p>
            </div>
          </div>

          <transition name="fade">
            <div v-if="feedback"
              class="absolute bottom-4 left-4 right-4 p-4 rounded border flex items-start gap-3 shadow-[2px_2px_0_0_#8C8C8C]"
              :class="feedback.tipo === 'exito'
                ? 'bg-status-punctual/10 border-status-punctual text-status-punctual'
                : feedback.tipo === 'retraso'
                  ? 'bg-status-delay/10 border-status-delay text-status-delay'
                  : 'bg-error-container border-error text-on-error-container'
                ">
              <span class="material-symbols-outlined mt-0.5">
                {{
                  feedback.tipo === "exito"
                    ? "check_circle"
                    : feedback.tipo === "retraso"
                      ? "schedule"
                      : "error"
                }}
              </span>
              <div>
                <p class="font-label-caps text-label-caps mb-1">
                  {{ feedback.titulo }}
                </p>
                <p class="font-body-sm text-body-sm">{{ feedback.mensaje }}</p>
              </div>
            </div>
          </transition>
        </div>

        <div class="mt-4 bg-surface p-3 border border-outline-variant text-center">
          <p class="font-memo-mono text-memo-mono text-on-surface-variant">
            Notificación de RRHH: Falsificar escaneos resultará en acción
            disciplinaria y confiscación de sus donas.
          </p>
        </div>
      </section>

      <section class="w-full md:w-1/3 min-w-[320px] bg-secondary-container p-6 flex flex-col gap-6">
        <div
          class="flex-1 flex flex-col bg-surface border border-outline-variant shadow-[2px_2px_0_0_#8C8C8C] rounded overflow-hidden">
          <div class="bg-surface-container-high border-b border-outline-variant p-3 flex justify-between items-center">
            <h2 class="font-label-caps text-label-caps text-on-surface">
              Registro Reciente
            </h2>
            <span class="material-symbols-outlined text-on-surface-variant">list_alt</span>
          </div>

          <div class="flex-1 overflow-y-auto p-3 flex flex-col gap-3">
            <div v-if="logs.length === 0" class="text-center py-8 font-body-sm text-body-sm text-on-surface-variant">
              Sin registros aún...
            </div>

            <div v-for="log in logs" :key="log.id"
              class="bg-surface border border-outline-variant p-3 flex items-center justify-between relative overflow-hidden">
              <div class="absolute left-0 top-0 bottom-0 w-1" :class="log.tipo === 'exito'
                ? 'bg-status-punctual'
                : log.tipo === 'retraso'
                  ? 'bg-status-delay'
                  : 'bg-status-absence'
                "></div>

              <div class="flex items-center gap-4 pl-2">
                <div
                  class="w-14 h-14 rounded-full bg-surface-container flex items-center justify-center overflow-hidden border border-outline shrink-0">
                  <img v-if="log.foto" :src="log.foto" :alt="log.nombre" class="w-full h-full object-cover" />
                  <span v-else class="material-symbols-outlined text-on-surface-variant text-3xl">person</span>
                </div>

                <div>
                  <p class="font-label-caps text-label-caps text-on-surface font-bold">
                    {{ log.nombre }}
                  </p>
                  <p class="font-body-sm text-body-sm text-on-surface-variant mt-0.5">
                    {{ log.accion }} — {{ log.hora }}
                  </p>
                </div>
              </div>

              <span class="material-symbols-outlined" :class="log.tipo === 'exito'
                ? 'text-status-punctual'
                : log.tipo === 'retraso'
                  ? 'text-status-delay'
                  : 'text-status-absence'
                ">
                {{
                  log.tipo === "exito"
                    ? "check_circle"
                    : log.tipo === "retraso"
                      ? "warning"
                      : "cancel"
                }}
              </span>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import jsQR from "jsqr";
import { supabase } from "../lib/supabase";

const videoEl = ref(null);
const canvasEl = ref(null);
const feedback = ref(null);
const logs = ref([]);
const horaActual = ref("");
const procesando = ref(false);

let stream = null;
let animFrame = null;
let feedbackTimer = null;
let relojInterval = null;

// Variable global para rastrear en qué día estamos "visualmente" y evitar el Efecto Medianoche
let diaActualVisual = new Date().toLocaleDateString('en-CA');

onMounted(async () => {
  actualizarHora();
  relojInterval = setInterval(actualizarHora, 1000);

  // Cargar registros del día al iniciar
  await cargarRegistrosHoy();

  // Realtime unificado para nuevos registros (Entradas) y actualizaciones (Salidas)
  supabase
    .channel("asistencias-scanner")
    .on(
      "postgres_changes",
      {
        event: "INSERT",
        schema: "public",
        table: "asistencias",
      },
      async (payload) => {
        // Si esta misma pantalla acaba de procesar el QR, ignoramos el realtime para no duplicar el log
        if (procesando.value) return;

        const { data: emp } = await supabase
          .from("empleado")
          .select("nombres, apellido_paterno, foto_url")
          .eq("id_empleado", payload.new.id_empleado)
          .single();

        if (emp) {
          const tipo = payload.new.estado === "activo" ? "exito" : "retraso";
          agregarLog(
            `${emp.nombres} ${emp.apellido_paterno}`,
            emp.foto_url,
            "Entrada",
            tipo
          );
        }
      }
    )
    .on(
      "postgres_changes",
      {
        event: "UPDATE",
        schema: "public",
        table: "asistencias",
      },
      async (payload) => {
        // Detectar si fue una actualización de salida y si no fue generada por esta misma pantalla
        if (payload.new.hora_salida && !procesando.value) {
          const { data: emp } = await supabase
            .from("empleado")
            .select("nombres, apellido_paterno, foto_url")
            .eq("id_empleado", payload.new.id_empleado)
            .single();

          if (emp) {
            const tipo = payload.new.estatus.includes("Anticipada") ? "retraso" : "exito";
            agregarLog(
              `${emp.nombres} ${emp.apellido_paterno}`,
              emp.foto_url,
              "Salida",
              tipo
            );
          }
        }
      }
    )
    .subscribe();

  // Configuración de la Cámara
  try {
    stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: "user" },
    });
    videoEl.value.srcObject = stream;
    videoEl.value.play();
    videoEl.value.addEventListener("playing", () => escanear());
  } catch (e) {
    mostrarFeedback(
      "error",
      "Sin acceso a cámara",
      "Verifica los permisos del navegador."
    );
  }
});

async function cargarRegistrosHoy() {
  const hoy = new Date().toLocaleDateString('en-CA');

  const { data } = await supabase
    .from("asistencias")
    .select(
      "id_asistencias, estado, hora_entrada, hora_salida, id_empleado, empleado(nombres, apellido_paterno, foto_url)"
    )
    .eq("fecha", hoy)
    .order("id_asistencias", { ascending: false })
    .limit(10);

  if (data) {
    logs.value = data.map((a) => ({
      id: a.id_asistencias,
      nombre: `${a.empleado.nombres} ${a.empleado.apellido_paterno}`,
      foto: a.empleado.foto_url,
      accion: a.hora_salida ? "Salida" : "Entrada",
      hora: a.hora_entrada || a.hora_salida || "—",
      tipo:
        a.estado === "activo"
          ? "exito"
          : a.estado === "leve_retraso"
            ? "retraso"
            : "error",
    }));
  }
}

onUnmounted(() => {
  if (stream) stream.getTracks().forEach((t) => t.stop());
  if (animFrame) clearTimeout(animFrame); // Usamos clearTimeout porque cambiamos a setTimeout
  if (relojInterval) clearInterval(relojInterval);
  if (feedbackTimer) clearTimeout(feedbackTimer);
});

function actualizarHora() {
  horaActual.value = new Date().toLocaleTimeString("es-MX", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

function escanear() {
  const video = videoEl.value;
  const canvas = canvasEl.value;
  if (!video || !canvas) return;

  const ctx = canvas.getContext("2d");
  canvas.width = video.videoWidth;
  canvas.height = video.videoHeight;
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

  const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
  const code = jsQR(imageData.data, imageData.width, imageData.height);

  if (code && !procesando.value) {
    procesarQR(code.data);
  }

  // Optimización de rendimiento: Escanear 4 veces por segundo en lugar de 60
  animFrame = setTimeout(() => requestAnimationFrame(escanear), 250);
}

async function procesarQR(contenido) {
  const partes = contenido.split('|')
  if (partes.length !== 2) {
    mostrarFeedback('error', 'QR Inválido', 'Este código no pertenece al sistema Dunder Mifflin.')
    return
  }

  const idEmpleado = parseInt(partes[0])
  const tipoQR = partes[1]

  if (isNaN(idEmpleado) || !['entrada', 'salida'].includes(tipoQR)) {
    mostrarFeedback('error', 'QR Inválido', 'No se pudo identificar al empleado.')
    return
  }

  procesando.value = true

  const { data: emp } = await supabase
    .from('empleado')
    .select('id_empleado, nombres, apellido_paterno, foto_url, horario(hora_entrada, hora_salida, minutos_tolerancia)')
    .eq('id_empleado', idEmpleado)
    .single()

  if (!emp) {
    mostrarFeedback('error', 'Empleado no encontrado', `ID ${idEmpleado} no existe.`)
    procesando.value = false
    return
  }

  const nombre = `${emp.nombres} ${emp.apellido_paterno}`
  const ahora = new Date()
  const horaStr = ahora.toTimeString().split(' ')[0]
  const fechaStr = ahora.toLocaleDateString('en-CA')
  const horario = emp.horario?.[0]

  if (tipoQR === 'entrada') {
    const { data: yaRegistro } = await supabase
      .from('asistencias')
      .select('id_asistencias, estado')
      .eq('id_empleado', idEmpleado)
      .eq('fecha', fechaStr)
      .maybeSingle()

    if (yaRegistro && yaRegistro.estado !== 'falta') {
      mostrarFeedback('error', 'Ya registrado', `${nombre} ya registró entrada hoy.`)
      agregarLog(nombre, emp.foto_url, 'Entrada', 'error')
      procesando.value = false
      return
    }

    // Si ya tiene falta automática, actualizarla con la entrada real
    if (yaRegistro?.estado === 'falta') {
      const { error } = await supabase
        .from('asistencias')
        .update({
          hora_entrada: horaStr,
          estado: 'leve_retraso',
          estatus: 'Retraso Mayor'
        })
        .eq('id_asistencias', yaRegistro.id_asistencias)

      mostrarFeedback('retraso', '⚠ Falta actualizada', `${nombre} llegó tarde pero se actualizó el registro.`)
      agregarLog(nombre, emp.foto_url, 'Entrada', 'retraso')
      setTimeout(() => { procesando.value = false }, 3000)
      return
    }

    let estado = 'activo'
    let estatus = 'Puntual'
    let tipo = 'exito'
    let titulo = 'Entrada Registrada'
    let mensaje = `${nombre} — ${horaStr}`

    if (horario) {
      const [hE, mE] = horario.hora_entrada.split(':').map(Number)
      const tolerancia = horario.minutos_tolerancia || 15

      // Tiempo de entrada esperado
      const entradaEsperada = new Date(ahora)
      entradaEsperada.setHours(hE, mE, 0, 0)

      // Límites
      const unaHoraAntes = new Date(entradaEsperada.getTime() - 60 * 60 * 1000)
      const limiteRetraso = new Date(entradaEsperada.getTime() + tolerancia * 60 * 1000)
      const unaHoraDespues = new Date(entradaEsperada.getTime() + 60 * 60 * 1000)

      if (ahora < unaHoraAntes) {
        estado = 'activo'
        estatus = 'Puntual'
        tipo = 'retraso'
        titulo = '⚠ Registro Inusual'
        mensaje = `${nombre} registró entrada muy temprano (${horaStr}). Se guardó el registro.`
      } else if (ahora > unaHoraDespues) {
        estado = 'leve_retraso'
        estatus = 'Retraso Mayor'
        tipo = 'retraso'
        titulo = '⚠ Entrada muy tarde'
        mensaje = `${nombre} registró entrada inusualmente tarde (${horaStr}).`
      } else if (ahora > limiteRetraso) {
        estado = 'leve_retraso'
        estatus = 'Retraso'
        tipo = 'retraso'
        titulo = '⚠ Entrada con Retraso'
        mensaje = `${nombre} llegó tarde. Hora: ${horaStr}`
      }
    }

    const { error: insertError } = await supabase
      .from('asistencias')
      .insert({
        id_empleado: idEmpleado,
        fecha: fechaStr,
        hora_entrada: horaStr,
        estado,
        estatus
      })

    mostrarFeedback(tipo, titulo, mensaje)
    agregarLog(nombre, emp.foto_url, 'Entrada', tipo)

  } else {
    // SALIDA
    const { data: asistencia } = await supabase
      .from('asistencias')
      .select('id_asistencias, hora_salida')
      .eq('id_empleado', idEmpleado)
      .eq('fecha', fechaStr)
      .maybeSingle()

    if (!asistencia) {
      mostrarFeedback('error', 'Sin entrada registrada', `${nombre} no tiene entrada hoy.`)
      procesando.value = false
      return
    }

    if (asistencia.hora_salida) {
      mostrarFeedback('error', 'Ya registró salida', `${nombre} ya registró salida hoy.`)
      procesando.value = false
      return
    }

    let estatusSalida = 'Salida a Tiempo'
    let tipo = 'exito'
    let titulo = 'Salida Registrada'
    let mensaje = `${nombre} — ${horaStr}`

    if (horario) {
      const [hS, mS] = horario.hora_salida.split(':').map(Number)

      const salidaEsperada = new Date(ahora)
      salidaEsperada.setHours(hS, mS, 0, 0)

      const unaHoraAntes = new Date(salidaEsperada.getTime() - 60 * 60 * 1000)
      const unaHoraDespues = new Date(salidaEsperada.getTime() + 60 * 60 * 1000)

      if (ahora < unaHoraAntes) {
        estatusSalida = 'Salida Anticipada'
        tipo = 'retraso'
        titulo = '⚠ Salida muy temprana'
        mensaje = `${nombre} salió inusualmente temprano (${horaStr}).`
      } else if (ahora < salidaEsperada) {
        estatusSalida = 'Salida Anticipada'
        tipo = 'retraso'
        titulo = '⚠ Salida Anticipada'
        mensaje = `${nombre} salió antes del horario (${horaStr}).`
      } else if (ahora > unaHoraDespues) {
        estatusSalida = 'Salida a Tiempo'
        tipo = 'retraso'
        titulo = '⚠ Salida muy tardía'
        mensaje = `${nombre} registró salida inusualmente tarde (${horaStr}).`
      }
    }

    const { error: updateError } = await supabase
      .from('asistencias')
      .update({ hora_salida: horaStr, estatus: estatusSalida })
      .eq('id_asistencias', asistencia.id_asistencias)

    mostrarFeedback(tipo, titulo, mensaje)
    agregarLog(nombre, emp.foto_url, 'Salida', tipo)
  }

  setTimeout(() => { procesando.value = false }, 3000)
}

function mostrarFeedback(tipo, titulo, mensaje) {
  feedback.value = { tipo, titulo, mensaje };
  if (feedbackTimer) clearTimeout(feedbackTimer);
  feedbackTimer = setTimeout(() => {
    feedback.value = null;
  }, 4000);
}

function agregarLog(nombre, foto, accion, tipo) {
  const hoyReal = new Date().toLocaleDateString('en-CA');

  // Lógica para curar el "Efecto Medianoche"
  if (hoyReal !== diaActualVisual) {
    logs.value = [];
    diaActualVisual = hoyReal;
  }

  logs.value.unshift({
    id: Date.now(),
    nombre,
    foto,
    accion,
    hora: new Date().toLocaleTimeString("es-MX", {
      hour: "2-digit",
      minute: "2-digit",
    }),
    tipo,
  });
  
  // Máximo 10 en el log
  if (logs.value.length > 10) logs.value.pop();
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>