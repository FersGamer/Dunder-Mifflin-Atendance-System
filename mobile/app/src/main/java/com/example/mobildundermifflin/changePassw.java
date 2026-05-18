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

import com.example.mobildundermifflin.models.AuthRequest;
import com.example.mobildundermifflin.models.AuthResponse;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class changePassw extends Fragment {

    private TextInputEditText etPasswordActual, etPasswordNueva, etPasswordConfirmar;

    public changePassw() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_passw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPasswordActual    = view.findViewById(R.id.etPasswordActual);
        etPasswordNueva     = view.findViewById(R.id.etPasswordNueva);
        etPasswordConfirmar = view.findViewById(R.id.etPasswordConfirmar);
        Button btnSiguiente = view.findViewById(R.id.btnSiguiente);

        btnSiguiente.setOnClickListener(v -> cambiarPassword());
    }

    private void cambiarPassword() {
        String actual    = etPasswordActual.getText().toString().trim();
        String nueva     = etPasswordNueva.getText().toString().trim();
        String confirmar = etPasswordConfirmar.getText().toString().trim();

        // Validaciones
        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nueva.equals(confirmar)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nueva.length() < 8) {
            Toast.makeText(getContext(), "Mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
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
                    // Navegar al siguiente fragmento en el hilo principal
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
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}