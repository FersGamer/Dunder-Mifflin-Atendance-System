package com.example.mobildundermifflin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mobildundermifflin.models.Empleado;
import com.example.mobildundermifflin.models.Asistencia;
import com.example.mobildundermifflin.models.SolicitudAusencia;
import com.example.mobildundermifflin.models.Vacaciones;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.example.mobildundermifflin.utils.UIHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class inicio extends Fragment {

    private ShapeableImageView ivFotoPerfil, ivProfileToolbar;
    private TextView tvNombreUsuario, tvDepartamento, tvTurno;
    private TextView tvAsistencia, tvFaltas, tvTiempo, tvDiasRestantes;
    private ImageButton btnNotificaciones;

    public inicio() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Le pedimos al MainActivity que evalúe y pinte NUESTRA campana
        if (getActivity() instanceof MainActivity && btnNotificaciones != null) {
            ((MainActivity) getActivity()).verificarNotificacionesGlobal(btnNotificaciones);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && getActivity() instanceof MainActivity && btnNotificaciones != null) {
            ((MainActivity) getActivity()).verificarNotificacionesGlobal(btnNotificaciones);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar vistas locales
        ivFotoPerfil     = view.findViewById(R.id.ivFotoPerfil);
        tvNombreUsuario  = view.findViewById(R.id.tvNombreUsuario);
        tvDepartamento   = view.findViewById(R.id.tvDepartamento);
        tvTurno          = view.findViewById(R.id.tvTurno);
        tvAsistencia     = view.findViewById(R.id.tvAsiatencia);
        tvFaltas         = view.findViewById(R.id.tvFaltas);
        tvTiempo         = view.findViewById(R.id.tvTiempo);
        tvDiasRestantes  = view.findViewById(R.id.tvDiasRestantes);
        btnNotificaciones = view.findViewById(R.id.btnNotificaciones);

        // 2. CORRECCIÓN: Buscar ivProfileToolbar dentro de la vista del fragmento actual (view)
        ivProfileToolbar = view.findViewById(R.id.ivProfileToolbar);

        if (ivProfileToolbar != null) {
            UIHelper.cargarFotoToolbar(requireContext(), ivProfileToolbar);
        }

        // 3. Configurar campana de notificaciones
        if (btnNotificaciones != null) {
            btnNotificaciones.setOnClickListener(v -> {
                // Apagamos el color visualmente al instante
                btnNotificaciones.clearColorFilter();

                if (getActivity() instanceof MainActivity) {
                    MainActivity main = (MainActivity) getActivity();

                    // 1. Le decimos al Main que actualice la base de datos
                    main.marcarNotificacionesComoVistasGlobal();

                    // 2. Le decimos al Main que nos cambie de pantalla
                    main.irASolicitudes();
                }
            });
        }

        // 4. Cargar el resto de los datos
        cargarDatosEmpleado();
    }


    private void cargarDatosEmpleado() {
        String nombreGuardado = SessionManager.getNombres(requireContext());
        String fotoGuardada   = SessionManager.getFotoUrl(requireContext());

        // 1. Mostrar lo local PRIMERO para que la pantalla cargue rápido
        if (!nombreGuardado.isEmpty()) {
            mostrarDatosLocales();
            // Nota: asumo que tus métodos de asistencias y vacaciones no tienen
            // bloqueos similares y siempre van a la BD.
            cargarAsistencias(SessionManager.getIdEmpleado(requireContext()));
            cargarVacaciones(SessionManager.getIdEmpleado(requireContext()));
        }

        // 2. SIN "return;". Ahora SIEMPRE vamos a Supabase a revisar si hay algo nuevo.
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());

        SupabaseClient.getApi()
                .getEmpleadoCompleto("eq." + idEmpleado,
                        "id_empleado,nombres,apellido_paterno,foto_url,departamento(nombre_departamento),horario(hora_entrada,hora_salida)")
                .enqueue(new Callback<List<Empleado>>() {
                    @Override
                    public void onResponse(Call<List<Empleado>> call, Response<List<Empleado>> response) {
                        if (!isAdded() || getActivity() == null) return;

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Empleado emp = response.body().get(0);

                            String depto = (emp.departamento != null) ? emp.departamento.nombreDepartamento : "Sin departamento";
                            String turno = (emp.horario != null && !emp.horario.isEmpty()) ? emp.horario.get(0).horaEntrada + " - " + emp.horario.get(0).horaSalida : "Sin horario";
                            String nuevaFoto = emp.fotoUrl != null ? emp.fotoUrl : "";

                            // Verificamos si la foto que trajo la BD es diferente a la que teníamos guardada
                            if (!nuevaFoto.equals(fotoGuardada)) {
                                Log.d("INICIO", "¡Nueva foto detectada! Actualizando UI...");

                                // Guardamos la nueva en SessionManager
                                SessionManager.guardarDatosEmpleado(requireContext(),
                                        emp.nombres, emp.apellidoPaterno, nuevaFoto, depto, turno);

                                // Actualizamos la pantalla con la foto nueva
                                actualizarUI(emp.nombres, emp.apellidoPaterno, nuevaFoto, depto, turno);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Empleado>> call, Throwable t) {
                        Log.e("INICIO", "Error: " + t.getMessage());
                    }
                });
    }

    private void mostrarDatosLocales() {
        if (!isAdded()) return;
        String nombres    = SessionManager.getNombres(requireContext());
        String apellido   = SessionManager.getApellidoPaterno(requireContext());
        String fotoUrl    = SessionManager.getFotoUrl(requireContext());
        String depto      = SessionManager.getDepartamento(requireContext());
        String turno      = SessionManager.getTurno(requireContext());
        actualizarUI(nombres, apellido, fotoUrl, depto, turno);
    }

    private void actualizarUI(String nombres, String apellido, String fotoUrl, String depto, String turno) {
        if (!isAdded() || getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (!isAdded()) return;

            tvNombreUsuario.setText(nombres + " " + apellido);
            tvDepartamento.setText(depto);
            tvTurno.setText("Turno: " + turno);

            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(fotoUrl)
                        .placeholder(R.drawable.ejemplo)
                        .circleCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                        .into(ivFotoPerfil);

                if (ivProfileToolbar != null) {
                    Glide.with(requireContext())
                            .load(fotoUrl)
                            .placeholder(R.drawable.ejemplo)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                            .into(ivProfileToolbar);
                }
            }
        });
    }

    private void cargarAsistencias(int idEmpleado) {
        SupabaseClient.getApi()
                .getAsistenciasPorEmpleado("eq." + idEmpleado, "id_asistencias,estado,fecha")
                .enqueue(new Callback<List<Asistencia>>() {
                    @Override
                    public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {
                        if (!isAdded() || getActivity() == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            List<Asistencia> asistencias = response.body();
                            int totalAsistencias = (int) asistencias.stream()
                                    .filter(a -> "activo".equals(a.estado)).count();
                            int totalFaltas = (int) asistencias.stream()
                                    .filter(a -> "falta".equals(a.estado)).count();
                            int totalRetardos = (int) asistencias.stream()
                                    .filter(a -> "leve_retraso".equals(a.estado)).count();

                            getActivity().runOnUiThread(() -> {
                                if (!isAdded()) return;
                                tvAsistencia.setText(String.valueOf(totalAsistencias));
                                tvFaltas.setText(String.valueOf(totalFaltas));
                                tvTiempo.setText(String.valueOf(totalRetardos));
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                        Log.e("INICIO", "Error asistencias: " + t.getMessage());
                    }
                });
    }

    private void cargarVacaciones(int idEmpleado) {
        SupabaseClient.getApi()
                .getVacacionesPorEmpleado("eq." + idEmpleado, "dias_otorgados,dias_consumidos")
                .enqueue(new Callback<List<Vacaciones>>() {
                    @Override
                    public void onResponse(Call<List<Vacaciones>> call, Response<List<Vacaciones>> response) {
                        if (!isAdded() || getActivity() == null) return;
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Vacaciones vac = response.body().get(0);
                            int diasRestantes = vac.diasOtorgados - vac.diasConsumidos;
                            getActivity().runOnUiThread(() -> {
                                if (!isAdded()) return;
                                tvDiasRestantes.setText(String.valueOf(diasRestantes));
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Vacaciones>> call, Throwable t) {
                        Log.e("INICIO", "Error vacaciones: " + t.getMessage());
                    }
                });
    }
}
