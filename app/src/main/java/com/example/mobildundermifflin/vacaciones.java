package com.example.mobildundermifflin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class vacaciones extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vacaciones,
                container, false);

        TextInputEditText etInicio = view.findViewById(R.id.etFechaInicio);
        TextInputEditText etFin    = view.findViewById(R.id.etFechaFin);

        // Date picker al tocar los campos
        etInicio.setOnClickListener(v -> mostrarDatePicker(etInicio));
        etFin.setOnClickListener(v -> mostrarDatePicker(etFin));

        view.findViewById(R.id.btnEnviarVacaciones).setOnClickListener(v -> {
            String inicio = etInicio.getText().toString();
            String fin    = etFin.getText().toString();
            String notas  = ((TextInputEditText) view.findViewById(R.id.etNotas))
                    .getText().toString();

            if (inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Selecciona las fechas", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(),
                    "✅ Solicitud enviada", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void mostrarDatePicker(TextInputEditText campo) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
            campo.setText(String.format("%02d/%02d/%d", m + 1, d, y));
        }, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }
}