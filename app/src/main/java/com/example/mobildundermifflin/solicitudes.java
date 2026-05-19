package com.example.mobildundermifflin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobildundermifflin.models.SolicitudAusencia;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.example.mobildundermifflin.utils.UIHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class solicitudes extends Fragment {

    private LinearLayout layoutHistorial;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnVacaciones = view.findViewById(R.id.btnVacaciones);
        Button btnPermiso    = view.findViewById(R.id.btnPermiso);
        layoutHistorial      = view.findViewById(R.id.layoutHistorial);

        // Cargar foto en toolbar
        ShapeableImageView ivToolbar = view.findViewById(R.id.ivProfileToolbar);
        if (ivToolbar != null) {
            UIHelper.cargarFotoToolbar(requireContext(), ivToolbar);
        }

        // Cargar Permiso por defecto
        cargarSubFragmento(new permiso());
        setTabActivo(btnPermiso, btnVacaciones);

        btnVacaciones.setOnClickListener(v -> {
            cargarSubFragmento(new vacaciones());
            setTabActivo(btnVacaciones, btnPermiso);
        });

        btnPermiso.setOnClickListener(v -> {
            cargarSubFragmento(new permiso());
            setTabActivo(btnPermiso, btnVacaciones);
        });

        cargarHistorialDesdeApi();
    }

    private void cargarSubFragmento(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.subFragmentContainer, fragment)
                .commit();
    }

    private void setTabActivo(Button activo, Button inactivo) {
        activo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#1A2E4A")));
        activo.setTextColor(Color.WHITE);

        inactivo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        inactivo.setTextColor(Color.parseColor("#857661"));
    }

    public void cargarHistorialDesdeApi() {
        if (!isAdded() || getContext() == null) return;
        
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());
        if (idEmpleado == -1) return;

        SupabaseClient.getApi()
                .getSolicitudesPorEmpleado("eq." + idEmpleado, "*", "fecha_solicitud.desc")
                .enqueue(new Callback<List<SolicitudAusencia>>() {
                    @Override
                    public void onResponse(Call<List<SolicitudAusencia>> call, Response<List<SolicitudAusencia>> response) {
                        if (isAdded() && response.isSuccessful() && response.body() != null) {
                            mostrarHistorial(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SolicitudAusencia>> call, Throwable t) {
                        Log.e("SOLICITUDES", "Error cargando historial: " + t.getMessage());
                    }
                });
    }

    private void mostrarHistorial(List<SolicitudAusencia> lista) {
        if (layoutHistorial == null) return;
        layoutHistorial.removeAllViews();

        if (lista.isEmpty()) {
            TextView tvVacio = new TextView(requireContext());
            tvVacio.setText("No hay solicitudes recientes");
            tvVacio.setPadding(20, 20, 20, 20);
            layoutHistorial.addView(tvVacio);
            return;
        }

        SimpleDateFormat inputFmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFmt = new SimpleDateFormat("dd MMM", new Locale("es", "MX"));

        for (SolicitudAusencia sol : lista) {
            View fila = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_historial, layoutHistorial, false);

            String fechaStr = sol.fechaInicio;
            try {
                Date date = inputFmt.parse(sol.fechaInicio);
                fechaStr = outputFmt.format(date);
            } catch (Exception ignored) {}

            ((TextView) fila.findViewById(R.id.tvTipoHistorial)).setText("Solicitud de Ausencia");
            ((TextView) fila.findViewById(R.id.tvFechaHistorial)).setText(fechaStr + " • " + (sol.aprobacion != null ? sol.aprobacion.toUpperCase() : "PENDIENTE"));

            TextView tvEstado = fila.findViewById(R.id.tvEstadoHistorial);
            String estado = (sol.aprobacion != null) ? sol.aprobacion.toLowerCase() : "pendiente";
            
            tvEstado.setText(estado.toUpperCase());
            
            int color;
            if (estado.contains("aprobado")) {
                color = Color.parseColor("#2E7D32"); // Verde
            } else if (estado.contains("rechazado")) {
                color = Color.parseColor("#B71C1C"); // Rojo
            } else {
                color = Color.parseColor("#FBC02D"); // Amarillo (Pendiente)
            }

            tvEstado.getBackground().setTint(color);
            if (estado.contains("pendiente")) {
                tvEstado.setTextColor(Color.BLACK);
            } else {
                tvEstado.setTextColor(Color.WHITE);
            }

            layoutHistorial.addView(fila);
        }
    }
}
