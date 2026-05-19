package com.example.mobildundermifflin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class changePassw extends Fragment {

    private TextInputEditText etPasswordNueva, etPasswordConfirmar;

    public changePassw() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_passw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPasswordNueva     = view.findViewById(R.id.etPasswordNueva);
        etPasswordConfirmar = view.findViewById(R.id.etPasswordConfirmar);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        btnSiguiente.setOnClickListener(v -> cambiarPassword());
    }

    private void cambiarPassword() {
        String nueva     = etPasswordNueva.getText().toString().trim();
        String confirmar = etPasswordConfirmar.getText().toString().trim();

        // Validaciones básicas
        if (nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nueva.equals(confirmar)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación de complejidad
        if (!validarPasswordSegura(nueva)) {
            return; // El mensaje de error lo da el método validarPasswordSegura
        }

        // Obtener token guardado en sesión
        String token = SessionManager.getToken(getContext());
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llamar a Supabase Auth para cambiar contraseña en hilo separado
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("password", nueva);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://gwbppbdkedatauevokbp.supabase.co/auth/v1/user")
                        .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .put(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                        .build();

                Response response = client.newCall(request).execute();
                Log.d("CHANGE_PASS", "Código: " + response.code());

                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "¡Contraseña actualizada!", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof PrimerInicio) {
                            ((PrimerInicio) getActivity()).loadFragment(new subirFoto());
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error al cambiar contraseña", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("CHANGE_PASS", "Error: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error de red: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private boolean validarPasswordSegura(String password) {
        if (password.length() < 8) {
            Toast.makeText(getContext(), "Debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getContext(), "Debe contener al menos una mayúscula", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            Toast.makeText(getContext(), "Debe contener al menos una minúscula", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            Toast.makeText(getContext(), "Debe contener al menos un número", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            Toast.makeText(getContext(), "Debe contener al menos un carácter especial", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tieneSecuencias(password)) {
            Toast.makeText(getContext(), "No se permiten secuencias consecutivas (ej. 123 o abc)", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean tieneSecuencias(String password) {
        String p = password.toLowerCase();
        for (int i = 0; i < p.length() - 2; i++) {
            char c1 = p.charAt(i);
            char c2 = p.charAt(i + 1);
            char c3 = p.charAt(i + 2);

            // Secuencia ascendente (abc, 123)
            if (c2 == c1 + 1 && c3 == c2 + 1) return true;
            // Secuencia descendente (cba, 321)
            if (c2 == c1 - 1 && c3 == c2 - 1) return true;
        }
        return false;
    }
}
