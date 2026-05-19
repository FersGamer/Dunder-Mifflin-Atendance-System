package com.example.mobildundermifflin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mobildundermifflin.models.Vacaciones;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class permiso extends Fragment {

    private int saldoVacaciones = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permiso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etFecha = view.findViewById(R.id.etFechaPermiso);
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
                // Guardamos en formato ISO para Supabase
                etFecha.setTag(String.format("%d-%02d-%02d", y, m + 1, d));
                etFecha.setText(String.format("%02d/%02d/%d", d, m + 1, y));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            
            // Validación: No permitir fechas pasadas
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        // Tipos mapeados a los valores del enum tipo_falta en Supabase
        String[] tiposDisplay = { "Permiso Médico", "Permiso Personal", "Asunto Familiar", "Vacaciones", "Paternidad" };
        String[] tiposEnum    = { "Permiso Medico", "Permiso Personal", "Permiso Personal", "Vacaciones", "Paternidad" };

        AutoCompleteTextView acTipo = view.findViewById(R.id.acTipoPermiso);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, tiposDisplay);
        acTipo.setAdapter(adapter);

        view.findViewById(R.id.btnEnviarPermiso).setOnClickListener(v -> {
            String fechaISO     = etFecha.getTag() != null ? etFecha.getTag().toString() : "";
            String tipoDisplay  = acTipo.getText().toString();
            String just = ((TextInputEditText) view.findViewById(R.id.etJustificacion))
                    .getText().toString();

            if (fechaISO.isEmpty() || tipoDisplay.isEmpty() || just.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Encontrar el valor enum correspondiente
            String tipoEnum = "Permiso Personal";
            for (int i = 0; i < tiposDisplay.length; i++) {
                if (tiposDisplay[i].equals(tipoDisplay)) {
                    tipoEnum = tiposEnum[i];
                    break;
                }
            }

            // Validación: Si es vacación, verificar saldo
            if (tipoEnum.equals("Vacaciones") && saldoVacaciones <= 0) {
                Toast.makeText(requireContext(), "No tienes días de vacaciones disponibles", Toast.LENGTH_LONG).show();
                return;
            }

            // Nota: En permisos, fechaInicio y fechaFin son iguales (1 día máximo)
            enviarSolicitud(fechaISO, fechaISO, tipoEnum, just);
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
                            saldoVacaciones = v.diasOtorgados - v.diasConsumidos;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Vacaciones>> call, Throwable t) {
                        Log.e("PERMISO", "Error saldo: " + t.getMessage());
                    }
                });
    }

    private void enviarSolicitud(String fechaInicio, String fechaFin, String tipoFalta, String descripcion) {
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());

        Map<String, Object> bodyFalta = new HashMap<>();
        bodyFalta.put("faltas", tipoFalta);
        bodyFalta.put("goce_sueldo", true);

        SupabaseClient.getApi()
                .insertarFalta(bodyFalta)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Map<String, Object> bodySolicitud = new HashMap<>();
                        bodySolicitud.put("fecha_inicio", fechaInicio);
                        bodySolicitud.put("fecha_fin", fechaFin);
                        bodySolicitud.put("aprobacion", "Pendiente");
                        bodySolicitud.put("descripcion", descripcion);
                        bodySolicitud.put("fecha_solicitud", new java.text.SimpleDateFormat("yyyy-MM-dd")
                                .format(new java.util.Date()));
                        bodySolicitud.put("id_empleado", idEmpleado);

                        SupabaseClient.getApi()
                                .insertarSolicitud(bodySolicitud)
                                .enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> r) {
                                        if (isAdded()) {
                                            Toast.makeText(requireContext(), "✅ Solicitud enviada correctamente", Toast.LENGTH_SHORT).show();
                                            
                                            // Actualizar historial en el fragmento padre (solicitudes)
                                            Fragment parent = getParentFragment();
                                            if (parent instanceof solicitudes) {
                                                ((solicitudes) parent).cargarHistorialDesdeApi();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("PERMISO", "Error solicitud: " + t.getMessage());
                                    }
                                });
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("PERMISO", "Error falta: " + t.getMessage());
                    }
                });
    }
}
