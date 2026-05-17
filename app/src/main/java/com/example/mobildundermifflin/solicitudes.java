package com.example.mobildundermifflin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

public class solicitudes extends Fragment {

    // ✅ Historial simulado — agrega filas aquí fácilmente
    private final List<ItemHistorial> historial = Arrays.asList(
            new ItemHistorial("Permiso médico",    "12 de Octubre • 1 día",   "APROBADO",  "#2E7D32"),
            new ItemHistorial("Trámite personal",  "05 de Octubre • 0.5 días","PENDIENTE", "#E65100"),
            new ItemHistorial("Asunto familiar",   "28 de Septiembre • 1 día","RECHAZADO", "#B71C1C"),
            new ItemHistorial("Vacaciones",        "15 de Agosto • 5 días",   "APROBADO",  "#2E7D32")
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudes,
                container, false);

        Button btnVacaciones = view.findViewById(R.id.btnVacaciones);
        Button btnPermiso    = view.findViewById(R.id.btnPermiso);

        // Cargar Permiso por defecto (tab activo)
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

        // Historial
        LinearLayout layoutHistorial = view.findViewById(R.id.layoutHistorial);
        cargarHistorial(layoutHistorial);

        return view;
    }

    private void cargarSubFragmento(Fragment fragment) {
        getChildFragmentManager()          // ← childFragmentManager para sub-fragmentos
                .beginTransaction()
                .replace(R.id.subFragmentContainer, fragment)
                .commit();
    }

    private void setTabActivo(Button activo, Button inactivo) {
        activo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#1A2E4A")));
        activo.setTextColor(Color.WHITE);

        inactivo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#FFFFFF")));
        inactivo.setTextColor(Color.parseColor("#857661"));
    }

    private void cargarHistorial(LinearLayout contenedor) {
        for (ItemHistorial item : historial) {
            View fila = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_historial, contenedor, false);

            ((TextView) fila.findViewById(R.id.tvTipoHistorial)).setText(item.tipo);
            ((TextView) fila.findViewById(R.id.tvFechaHistorial)).setText(item.fecha);

            TextView tvEstado = fila.findViewById(R.id.tvEstadoHistorial);
            tvEstado.setText(item.estado);
            tvEstado.getBackground().setTint(Color.parseColor(item.color));

            contenedor.addView(fila);
        }
    }

    // ✅ Modelo del historial
    static class ItemHistorial {
        String tipo, fecha, estado, color;
        ItemHistorial(String tipo, String fecha, String estado, String color) {
            this.tipo   = tipo;
            this.fecha  = fecha;
            this.estado = estado;
            this.color  = color;
        }
    }
}