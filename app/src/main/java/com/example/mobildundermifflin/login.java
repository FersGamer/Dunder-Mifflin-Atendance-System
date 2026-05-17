package com.example.mobildundermifflin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobildundermifflin.models.AuthRequest;
import com.example.mobildundermifflin.models.AuthResponse;
import com.example.mobildundermifflin.models.Cuenta;
import com.example.mobildundermifflin.models.Empleado;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {

    private TextInputEditText etUsuario, etPassword;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario  = findViewById(R.id.etTelefono);
        etPassword = findViewById(R.id.etPassword);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        android.util.Log.d("LOGIN", "Buscando usuario: " + usuario);

        SupabaseClient.getApi()
                .getCuentaPorUsuario("eq." + usuario, "id_cuenta,usuario_cuenta,primer_inicio,id_empleado")
                .enqueue(new Callback<List<Cuenta>>() {
                    @Override
                    public void onResponse(Call<List<Cuenta>> call, Response<List<Cuenta>> response) {
                        android.util.Log.d("LOGIN", "Respuesta código: " + response.code());
                        android.util.Log.d("LOGIN", "Body: " + response.body());

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Cuenta cuenta = response.body().get(0);
                            android.util.Log.d("LOGIN", "Cuenta encontrada: " + cuenta.usuarioCuenta);
                            verificarPassword(cuenta, password);
                        } else {
                            mostrarError("Usuario no encontrado");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Cuenta>> call, Throwable t) {
                        android.util.Log.e("LOGIN", "Error: " + t.getMessage());
                        mostrarError("Error de conexión: " + t.getMessage());
                    }
                });
    }

    private void verificarPassword(Cuenta cuenta, String password) {
        SupabaseClient.getAuthApi()
                .signIn(new AuthRequest(
                        cuenta.usuarioCuenta + "@dundermifflin.com",
                        password
                ))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().accessToken;

                            // Buscar datos del empleado
                            SupabaseClient.getApi()
                                    .getEmpleadoPorId("eq." + cuenta.idEmpleado, "id_empleado,nombres,apellido_paterno")
                                    .enqueue(new Callback<List<Empleado>>() {
                                        @Override
                                        public void onResponse(Call<List<Empleado>> call, Response<List<Empleado>> r) {
                                            String nombres = "";
                                            String apellido = "";
                                            if (r.body() != null) {
                                                android.util.Log.d("LOGIN_DEBUG", "Tamaño de la lista: " + r.body().size());
                                            } else {
                                                android.util.Log.e("LOGIN_DEBUG", "Error de servidor: " + r.errorBody());
                                            }
                                            if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                                                nombres = r.body().get(0).nombres;
                                                apellido = r.body().get(0).apellidoPaterno;
                                            }
                                            android.util.Log.d("LOGIN", "Nombres: " + nombres);
                                            android.util.Log.d("LOGIN", "Apellido: " + apellido);
                                            SessionManager.guardar(login.this, token, cuenta, nombres, apellido);

                                            if (cuenta.primerInicio) {
                                                startActivity(new Intent(login.this, PrimerInicio.class));
                                            } else {
                                                startActivity(new Intent(login.this, MainActivity.class));
                                            }
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<List<Empleado>> call, Throwable t) {
                                            SessionManager.guardar(login.this, token, cuenta, "", "");
                                            startActivity(new Intent(login.this, MainActivity.class));
                                            finish();
                                        }
                                    });
                        } else {
                            mostrarError("Contraseña incorrecta");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        mostrarError("Error: " + t.getMessage());
                    }
                });
    }

    private void mostrarError(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(login.this, msg, Toast.LENGTH_SHORT).show();
            btnIngresar.setEnabled(true);
            btnIngresar.setText("Iniciar sesión");
        });
    }
}