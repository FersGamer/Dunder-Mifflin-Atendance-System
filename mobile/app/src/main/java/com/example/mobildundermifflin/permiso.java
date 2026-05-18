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

import androidx.fragment.app.Fragment;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class permiso extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permiso, container, false);

        TextInputEditText etFecha = view.findViewById(R.id.etFechaPermiso);
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
                // Guardamos en formato ISO para Supabase
                etFecha.setTag(String.format("%d-%02d-%02d", y, m + 1, d));
                etFecha.setText(String.format("%02d/%02d/%d", d, m + 1, y));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
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

            enviarSolicitud(fechaISO, fechaISO, tipoEnum);
        });

        return view;
    }

    private void enviarSolicitud(String fechaInicio, String fechaFin, String tipoFalta) {
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
