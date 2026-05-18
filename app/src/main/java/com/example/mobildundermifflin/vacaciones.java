package com.example.mobildundermifflin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobildundermifflin.models.Vacaciones;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class vacaciones extends Fragment {

    private String fechaInicioISO = "";
    private String fechaFinISO = "";
    private TextView tvSaldoDias;
    private TextInputEditText etInicio, etFin, etNotas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vacaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSaldoDias = view.findViewById(R.id.tvSaldoDias);
        etInicio    = view.findViewById(R.id.etFechaInicio);
        etFin       = view.findViewById(R.id.etFechaFin);
        etNotas     = view.findViewById(R.id.etNotas);

        etInicio.setOnClickListener(v -> mostrarDatePicker(etInicio, true));
        etFin.setOnClickListener(v -> mostrarDatePicker(etFin, false));

        view.findViewById(R.id.btnEnviarVacaciones).setOnClickListener(v -> {
            validarYEnviar();
        });

        cargarSaldoVacaciones();
    }

    private void cargarSaldoVacaciones() {
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());
        if (idEmpleado == -1) return;

        SupabaseClient.getApi()
                .getVacacionesPorEmpleado("eq." + idEmpleado, "dias_otorgados,dias_consumidos")
                .enqueue(new Callback<List<Vacaciones>>() {
                    @Override
                    public void onResponse(Call<List<Vacaciones>> call, Response<List<Vacaciones>> response) {
                        if (isAdded() && response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Vacaciones v = response.body().get(0);
                            int saldo = v.diasOtorgados - v.diasConsumidos;
                            tvSaldoDias.setText(String.valueOf(saldo));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Vacaciones>> call, Throwable t) {
                        Log.e("VACACIONES", "Error saldo: " + t.getMessage());
                    }
                });
    }

    private void mostrarDatePicker(TextInputEditText campo, boolean esInicio) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
            String iso = String.format(Locale.US, "%d-%02d-%02d", y, m + 1, d);
            String display = String.format(Locale.US, "%02d/%02d/%d", d, m + 1, y);
            campo.setText(display);
            if (esInicio) fechaInicioISO = iso;
            else fechaFinISO = iso;
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validarYEnviar() {
        if (fechaInicioISO.isEmpty() || fechaFinISO.isEmpty()) {
            Toast.makeText(requireContext(), "Selecciona el rango de fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaInicioISO.compareTo(fechaFinISO) > 0) {
            Toast.makeText(requireContext(), "La fecha de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        enviarSolicitudVacaciones(fechaInicioISO, fechaFinISO);
    }

    private void enviarSolicitudVacaciones(String inicio, String fin) {
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());

        Map<String, Object> bodyFalta = new HashMap<>();
        bodyFalta.put("faltas", "Vacaciones");
        bodyFalta.put("goce_sueldo", true);

        SupabaseClient.getApi()
                .insertarFalta(bodyFalta)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Map<String, Object> bodySolicitud = new HashMap<>();
                        bodySolicitud.put("fecha_inicio", inicio);
                        bodySolicitud.put("fecha_fin", fin);
                        bodySolicitud.put("aprobacion", "Pendiente");
                        bodySolicitud.put("fecha_solicitud", new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()));
                        bodySolicitud.put("id_empleado", idEmpleado);

                        SupabaseClient.getApi()
                                .insertarSolicitud(bodySolicitud)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> r) {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "✅ Solicitud enviada correctamente", Toast.LENGTH_LONG).show();
                                            limpiarCampos();
                                            
                                            // Actualizar historial en el fragmento padre (solicitudes)
                                            Fragment parent = getParentFragment();
                                            if (parent instanceof solicitudes) {
                                                ((solicitudes) parent).cargarHistorialDesdeApi();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("VACACIONES", "Error solicitud: " + t.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("VACACIONES", "Error falta: " + t.getMessage());
                    }
                });
    }

    private void limpiarCampos() {
        etInicio.setText("");
        etFin.setText("");
        etNotas.setText("");
        fechaInicioISO = "";
        fechaFinISO = "";
    }
}
