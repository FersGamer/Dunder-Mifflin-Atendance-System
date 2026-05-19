package com.example.mobildundermifflin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobildundermifflin.models.SolicitudAusencia;
import com.example.mobildundermifflin.network.SupabaseClient;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navInicio, navQr, navAsistencia, navSolicitudes, nav_cierreSesion;
    private ImageView ivInicio, ivQr, ivAsistencia, ivSolicitudes, iv_cierreSesion;
    private TextView tvInicio, tvQr, tvAsistencia, tvSolicitudes, tv_cierreSesion;
    private boolean hayNotificacionesNuevas = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        navInicio = findViewById(R.id.nav_inicio);
        navQr = findViewById(R.id.nav_qr);
        navAsistencia = findViewById(R.id.nav_asistencia);
        navSolicitudes = findViewById(R.id.nav_solicitudes);
        nav_cierreSesion= findViewById(R.id.nav_cierreSesion);

        ivInicio = findViewById(R.id.iv_inicio);
        ivQr = findViewById(R.id.iv_qr);
        ivAsistencia = findViewById(R.id.iv_asistencia);
        ivSolicitudes = findViewById(R.id.iv_solicitudes);
        iv_cierreSesion = findViewById(R.id.iv_cierreSesion);

        tvInicio = findViewById(R.id.tv_inicio);
        tvQr = findViewById(R.id.tv_qr);
        tvAsistencia = findViewById(R.id.tv_asistencia);
        tvSolicitudes = findViewById(R.id.tv_solicitudes);
        tv_cierreSesion = findViewById(R.id.tv_cierreSesion);

        // Listeners
        navInicio.setOnClickListener(v -> {
            loadFragment(new inicio());
            updateNavUI(R.id.nav_inicio);
        });

        navQr.setOnClickListener(v -> {
            loadFragment(new qrLector());
            updateNavUI(R.id.nav_qr);
        });

        navAsistencia.setOnClickListener(v -> {
            loadFragment(new asistencia());
            updateNavUI(R.id.nav_asistencia);
        });

        navSolicitudes.setOnClickListener(v -> {
            irASolicitudes();
        });

        nav_cierreSesion.setOnClickListener(v -> {
            // Cerrar sesión y regresar al login
            SessionManager.cerrarSesion(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Estado inicial
        if (savedInstanceState == null) {
            loadFragment(new inicio());
            updateNavUI(R.id.nav_inicio);
        }
    }

    public void irASolicitudes() {
        loadFragment(new solicitudes());
        updateNavUI(R.id.nav_solicitudes);
    }

    private void updateNavUI(int selectedId) {
        // Resetear todos a estado inactivo
        resetItem(navInicio, ivInicio, tvInicio);
        resetItem(navQr, ivQr, tvQr);
        resetItem(navAsistencia, ivAsistencia, tvAsistencia);
        resetItem(navSolicitudes, ivSolicitudes, tvSolicitudes);
        resetItem(nav_cierreSesion, iv_cierreSesion, tv_cierreSesion);

        // Aplicar estado activo al seleccionado
        if (selectedId == R.id.nav_inicio) {
            setActiveItem(navInicio, ivInicio, tvInicio);
        } else if (selectedId == R.id.nav_qr) {
            setActiveItem(navQr, ivQr, tvQr);
        } else if (selectedId == R.id.nav_asistencia) {
            setActiveItem(navAsistencia, ivAsistencia, tvAsistencia);
        } else if (selectedId == R.id.nav_solicitudes) {
            setActiveItem(navSolicitudes, ivSolicitudes, tvSolicitudes);
        } else if (selectedId == R.id.nav_cierreSesion) {
            setActiveItem(nav_cierreSesion, iv_cierreSesion, tv_cierreSesion);
        }
    }

    private void resetItem(LinearLayout layout, ImageView icon, TextView text) {
        layout.setBackground(null);
        icon.setColorFilter(Color.parseColor("#757575"));
        text.setTextColor(Color.parseColor("#757575"));
        text.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void setActiveItem(LinearLayout layout, ImageView icon, TextView text) {
        layout.setBackgroundResource(R.drawable.bg_nav_highlight);
        icon.setColorFilter(Color.WHITE);
        text.setTextColor(Color.WHITE);
        text.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    public void verificarNotificacionesGlobal(ImageButton btnCampana) {
        if (btnCampana == null) return;

        // ¡TRUCO DE UI! Pintamos al instante con lo que "recordamos" de la última vez,
        // así eliminamos el retraso visual al cambiar de pestaña.
        if (hayNotificacionesNuevas) {
            btnCampana.setColorFilter(Color.parseColor("#FF9800"), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            btnCampana.clearColorFilter();
        }

        int idEmpleado = SessionManager.getIdEmpleado(this);
        if (idEmpleado == -1) return;

        // Ahora sí, vamos a internet a confirmar si hay algo nuevo
        SupabaseClient.getApi()
                .getSolicitudesPorEmpleado("eq." + idEmpleado, "aprobacion,visto", "fecha_solicitud.desc")
                .enqueue(new Callback<List<SolicitudAusencia>>() {
                    @Override
                    public void onResponse(Call<List<SolicitudAusencia>> call, Response<List<SolicitudAusencia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean estadoActual = false;

                            for (SolicitudAusencia sol : response.body()) {
                                String estado = sol.aprobacion != null ? sol.aprobacion.trim() : "";
                                if (!estado.equalsIgnoreCase("Pendiente") && !sol.visto) {
                                    estadoActual = true;
                                    break;
                                }
                            }

                            // Si el estado en la BD es distinto al que recordábamos, actualizamos la memoria y la UI
                            if (estadoActual != hayNotificacionesNuevas) {
                                hayNotificacionesNuevas = estadoActual;
                                final boolean pintarNaranja = hayNotificacionesNuevas;

                                runOnUiThread(() -> {
                                    if (pintarNaranja) {
                                        btnCampana.setColorFilter(Color.parseColor("#FF9800"), android.graphics.PorterDuff.Mode.SRC_IN);
                                    } else {
                                        btnCampana.clearColorFilter();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SolicitudAusencia>> call, Throwable t) {
                        Log.e("MAIN", "Error verificando notificaciones: " + t.getMessage());
                    }
                });
    }

    public void marcarNotificacionesComoVistasGlobal() {
        hayNotificacionesNuevas = false;
        int idEmpleado = SessionManager.getIdEmpleado(this);
        if (idEmpleado == -1) return;

        Map<String, Object> campos = new java.util.HashMap<>();
        campos.put("visto", true);

        SupabaseClient.getApi()
                .marcarSolicitudesComoVistas("eq." + idEmpleado, "is.false", campos)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("MAIN", "Notificaciones marcadas como vistas exitosamente.");
                        } else {
                            Log.e("MAIN", "Error al actualizar 'visto': " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("MAIN", "Fallo de red al marcar como vistas: " + t.getMessage());
                    }
                });
    }
}
