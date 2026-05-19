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

// Función blindada para obtener fecha y hora exacta de México sin importar el dispositivo
function obtenerFechaHoraLocal() {
  const ahora = new Date();
  const mxTime = new Date(ahora.toLocaleString("en-US", { timeZone: "America/Mexico_City" }));
  
  const year = mxTime.getFullYear();
  const month = String(mxTime.getMonth() + 1).padStart(2, "0");
  const day = String(mxTime.getDate()).padStart(2, "0");
  
  const hours = String(mxTime.getHours()).padStart(2, "0");
  const minutes = String(mxTime.getMinutes()).padStart(2, "0");
  const seconds = String(mxTime.getSeconds()).padStart(2, "0");

  return {
    fechaStr: `${year}-${month}-${day}`,
    horaStr: `${hours}:${minutes}:${seconds}`,
    fechaObj: mxTime
  };
}

let diaActualVisual = obtenerFechaHoraLocal().fechaStr;

onMounted(async () => {
  actualizarHora();
  relojInterval = setInterval(actualizarHora, 1000);

  await cargarRegistrosHoy();

  // Sincronización en tiempo real
  supabase
    .channel("asistencias-scanner")
    .on("postgres_changes", { event: "INSERT", schema: "public", table: "asistencias" }, async (payload) => {
      if (procesando.value || !payload.new.hora_entrada) return;
      const { data: emp } = await supabase.from("empleado").select("nombres, apellido_paterno, foto_url").eq("id_empleado", payload.new.id_empleado).single();
      if (emp) agregarLog(payload.new.id_empleado, `${emp.nombres} ${emp.apellido_paterno}`, emp.foto_url, "Entrada", payload.new.estado === "activo" ? "exito" : "retraso");
    })
    .on("postgres_changes", { event: "UPDATE", schema: "public", table: "asistencias" }, async (payload) => {
      if (procesando.value) return;
      if (payload.new.hora_salida && payload.new.hora_salida !== payload.old.hora_salida) {
        const { data: emp } = await supabase.from("empleado").select("nombres, apellido_paterno, foto_url").eq("id_empleado", payload.new.id_empleado).single();
        if (emp) agregarLog(payload.new.id_empleado, `${emp.nombres} ${emp.apellido_paterno}`, emp.foto_url, "Salida", payload.new.estatus?.includes("Anticipada") ? "retraso" : "exito");
      }
    })
    .subscribe();

  // Iniciar cámara frontal
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: "user" } });
    videoEl.value.srcObject = stream;
    videoEl.value.play();
    videoEl.value.addEventListener("playing", () => escanear());
  } catch (e) {
    mostrarFeedback("error", "Sin cámara", "Verifica los permisos.");
  }
});

async function cargarRegistrosHoy() {
  const { fechaStr } = obtenerFechaHoraLocal();
  const { data } = await supabase
    .from("asistencias")
    .select("id_asistencias, estado, hora_entrada, hora_salida, id_empleado, empleado(nombres, apellido_paterno, foto_url)")
    .eq("fecha", fechaStr)
    .not("hora_entrada", "is", null)
    .order("hora_entrada", { ascending: false })
    .limit(10);

  if (data) {
    logs.value = data.map((a) => {
      const hEntrada = a.hora_entrada ? a.hora_entrada.slice(0, 5) : "";
      const hSalida = a.hora_salida ? a.hora_salida.slice(0, 5) : "";
      return {
        id: a.id_asistencias,
        id_empleado: a.id_empleado,
        nombre: `${a.empleado.nombres} ${a.empleado.apellido_paterno}`,
        foto: a.empleado.foto_url,
        accion: a.hora_salida ? "Turno Completado" : "En Turno",
        hora: a.hora_salida ? `${hEntrada} — Salida: ${hSalida}` : `Entrada: ${hEntrada}`,
        tipo: a.estado === "activo" ? "exito" : a.estado === "leve_retraso" ? "retraso" : "error",
      };
    });
  }
}

onUnmounted(() => {
  if (stream) stream.getTracks().forEach((t) => t.stop());
  if (animFrame) clearTimeout(animFrame);
  if (relojInterval) clearInterval(relojInterval);
  if (feedbackTimer) clearTimeout(feedbackTimer);
});

function actualizarHora() {
  horaActual.value = new Date().toLocaleTimeString("es-MX", { hour: "2-digit", minute: "2-digit" });
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

  if (code && !procesando.value) procesarQR(code.data);

  animFrame = setTimeout(() => requestAnimationFrame(escanear), 250);
}

// LÓGICA DE NEGOCIO CORREGIDA
async function procesarQR(contenido) {
  const partes = contenido.split("|");
  if (partes.length !== 2) {
    mostrarFeedback("error", "QR Inválido", "Formato incorrecto.");
    return;
  }

  const idEmpleado = parseInt(partes[0]);
  const tipoQR = partes[1];

  if (isNaN(idEmpleado) || !["entrada", "salida"].includes(tipoQR)) {
    mostrarFeedback("error", "QR Inválido", "Datos corruptos.");
    return;
  }

  procesando.value = true;

  // 1. Obtener datos del empleado y su horario
  const { data: emp, error: empError } = await supabase
    .from("empleado")
    .select("id_empleado, nombres, apellido_paterno, foto_url, horario(hora_entrada, hora_salida, minutos_tolerancia)")
    .eq("id_empleado", idEmpleado)
    .single();

  if (empError || !emp) {
    mostrarFeedback("error", "Empleado no encontrado", "ID no registrado en el sistema.");
    procesando.value = false;
    return;
  }

  const nombre = `${emp.nombres} ${emp.apellido_paterno}`;
  const { fechaStr, horaStr, fechaObj } = obtenerFechaHoraLocal();
  const horario = emp.horario?.[0]; // Puede ser undefined si no tiene turno

  // ==============================
  // FLUJO DE ENTRADA
  // ==============================
  if (tipoQR === "entrada") {
    
    // Verificar si ya registró algo hoy
    const { data: yaRegistro } = await supabase
      .from("asistencias")
      .select("id_asistencias, estado, hora_entrada")
      .eq("id_empleado", idEmpleado)
      .eq("fecha", fechaStr)
      .maybeSingle();

    if (yaRegistro && yaRegistro.hora_entrada && yaRegistro.estado !== 'falta') {
      mostrarFeedback("error", "Duplicado", `${nombre} ya registró entrada hoy.`);
      procesando.value = false;
      return;
    }

    // EVALUACIÓN ESTRICTA DEL TURNO
    let estado = "inusual"; // Por defecto es inusual hasta demostrar lo contrario
    let estatus = "Sin Turno Asignado";
    let tipoUi = "retraso"; // Color amarillo de advertencia
    
    if (horario && horario.hora_entrada) {
      const [hE, mE] = horario.hora_entrada.split(":").map(Number);
      const tolerancia = horario.minutos_tolerancia || 15;
      
      const inicioTurno = new Date(fechaObj);
      inicioTurno.setHours(hE, mE, 0);
      
      const limitePuntual = new Date(inicioTurno.getTime() + (tolerancia * 60000));
      const muyTemprano = new Date(inicioTurno.getTime() - (120 * 60000)); // 2 horas antes
      const finDeTurno = new Date(fechaObj);
      
      if (horario.hora_salida) {
        const [hS, mS] = horario.hora_salida.split(":").map(Number);
        finDeTurno.setHours(hS, mS, 0);
      } else {
        finDeTurno.setHours(23, 59, 59); // Fallback si no hay hora de salida
      }

      // Lógica de validación
      if (fechaObj < muyTemprano) {
        estado = "inusual";
        estatus = "Turno Equivocado"; // Ej. Es del turno vespertino y llegó en la mañana
        tipoUi = "retraso";
      } else if (fechaObj > finDeTurno) {
        estado = "inusual";
        estatus = "Fuera de Turno"; // Llegó cuando su turno ya acabó
        tipoUi = "error";
      } else if (fechaObj > limitePuntual) {
        estado = "leve_retraso";
        estatus = "Retraso";
        tipoUi = "retraso";
      } else {
        estado = "activo";
        estatus = "Puntual";
        tipoUi = "exito";
      }
    }

    // EJECUCIÓN EN BASE DE DATOS
    let dbError = null;
    
    if (yaRegistro?.estado === 'falta') {
      // Actualizamos la falta generada por el sistema
      const res = await supabase.from('asistencias').update({ 
        hora_entrada: horaStr, estado, estatus 
      }).eq('id_asistencias', yaRegistro.id_asistencias);
      dbError = res.error;
    } else {
      // Insertamos registro nuevo
      const res = await supabase.from("asistencias").insert({
        id_empleado: idEmpleado, fecha: fechaStr, hora_entrada: horaStr, estado, estatus
      });
      dbError = res.error;
    }

    // SI FALLA LA BASE DE DATOS, AVISAR AL USUARIO Y DETENER TODO
    if (dbError) {
      mostrarFeedback("error", "Error Base de Datos", dbError.message);
      procesando.value = false;
      return;
    }

    // Si tuvo éxito, mostramos en pantalla
    const titulo = estado === "activo" ? "Entrada" : `Entrada: ${estatus}`;
    mostrarFeedback(tipoUi, titulo, `${nombre} — ${horaStr}`);
    agregarLog(idEmpleado, nombre, emp.foto_url, "Entrada", tipoUi);

  } 
  // ==============================
  // FLUJO DE SALIDA
  // ==============================
  else {
    const { data: asistencia } = await supabase
      .from("asistencias")
      .select("id_asistencias, hora_salida")
      .eq("id_empleado", idEmpleado)
      .eq("fecha", fechaStr)
      .maybeSingle();

    if (!asistencia) {
      mostrarFeedback("error", "Falta Entrada", `${nombre} no tiene entrada registrada hoy.`);
      procesando.value = false;
      return;
    }

    if (asistencia.hora_salida) {
      mostrarFeedback("error", "Duplicado", `${nombre} ya registró su salida.`);
      procesando.value = false;
      return;
    }

    let estatusSalida = "Salida (Sin Turno)";
    let tipoUi = "exito";

    if (horario && horario.hora_salida) {
      const [hS, mS] = horario.hora_salida.split(":").map(Number);
      const salidaEsperada = new Date(fechaObj);
      salidaEsperada.setHours(hS, mS, 0);
      
      if (fechaObj < salidaEsperada) {
        estatusSalida = "Salida Anticipada";
        tipoUi = "retraso"; // Se fue antes de tiempo
      } else {
        estatusSalida = "Salida a Tiempo";
      }
    }

    // EJECUCIÓN EN BASE DE DATOS
    const { error: updateError } = await supabase
      .from("asistencias")
      .update({ hora_salida: horaStr, estatus: estatusSalida })
      .eq("id_asistencias", asistencia.id_asistencias);

    // SI FALLA LA BASE DE DATOS, AVISAR AL USUARIO
    if (updateError) {
      mostrarFeedback("error", "Error Base de Datos", updateError.message);
      procesando.value = false;
      return;
    }

    mostrarFeedback(tipoUi, "Turno Finalizado", `${nombre} — ${estatusSalida}`);
    agregarLog(idEmpleado, nombre, emp.foto_url, "Salida", tipoUi);
  }

  // Desbloqueo del escáner
  setTimeout(() => { procesando.value = false; }, 3000);
}

function mostrarFeedback(tipo, titulo, mensaje) {
  feedback.value = { tipo, titulo, mensaje };
  if (feedbackTimer) clearTimeout(feedbackTimer);
  feedbackTimer = setTimeout(() => { feedback.value = null; }, 4000);
}

function agregarLog(idEmpleado, nombre, foto, accion, tipo) {
  const { fechaStr, horaStr } = obtenerFechaHoraLocal();
  const horaCorta = horaStr.slice(0, 5); // Quitar segundos para la UI

  if (fechaStr !== diaActualVisual) {
    logs.value = [];
    diaActualVisual = fechaStr;
  }

  const index = logs.value.findIndex(log => log.id_empleado === idEmpleado);

  if (index !== -1) {
    const logExistente = logs.value[index];
    if (accion === "Salida") {
      logExistente.accion = "Turno Completado";
      const horaEntradaLimpia = logExistente.hora.replace("Entrada: ", "");
      logExistente.hora = `${horaEntradaLimpia} — Salida: ${horaCorta}`;
      logExistente.tipo = tipo; 
    }
    logs.value.splice(index, 1);
    logs.value.unshift(logExistente);
  } else {
    logs.value.unshift({
      id: Date.now(),
      id_empleado: idEmpleado,
      nombre,
      foto,
      accion: accion === "Entrada" ? "En Turno" : "Turno Completado",
      hora: accion === "Entrada" ? `Entrada: ${horaCorta}` : `Salida: ${horaCorta}`,
      tipo,
    });
  }
  
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