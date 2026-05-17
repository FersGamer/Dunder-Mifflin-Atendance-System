package com.example.mobildundermifflin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class permiso extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permiso,
                container, false);

        // Date picker
        TextInputEditText etFecha = view.findViewById(R.id.etFechaPermiso);
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
                etFecha.setText(String.format("%02d/%02d/%d", m+1, d, y));
            }, c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // ✅ Opciones del dropdown — agrega las que necesites
        String[] tipos = {
                "Permiso médico",
                "Trámite personal",
                "Asunto familiar",
                "Capacitación",
                "Otro"
        };
        AutoCompleteTextView acTipo = view.findViewById(R.id.acTipoPermiso);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                tipos);
        acTipo.setAdapter(adapter);

        view.findViewById(R.id.btnEnviarPermiso).setOnClickListener(v -> {
            String fecha = etFecha.getText().toString();
            String tipo  = acTipo.getText().toString();
            String just  = ((TextInputEditText) view.findViewById(R.id.etJustificacion))
                    .getText().toString();

            if (fecha.isEmpty() || tipo.isEmpty() || just.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(),
                    "✅ Solicitud enviada", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}