package com.example.mobildundermifflin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navInicio, navQr, navAsistencia, navSolicitudes;
    private ImageView ivInicio, ivQr, ivAsistencia, ivSolicitudes;
    private TextView tvInicio, tvQr, tvAsistencia, tvSolicitudes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        navInicio = findViewById(R.id.nav_inicio);
        navQr = findViewById(R.id.nav_qr);
        navAsistencia = findViewById(R.id.nav_asistencia);
        navSolicitudes = findViewById(R.id.nav_solicitudes);

        ivInicio = findViewById(R.id.iv_inicio);
        ivQr = findViewById(R.id.iv_qr);
        ivAsistencia = findViewById(R.id.iv_asistencia);
        ivSolicitudes = findViewById(R.id.iv_solicitudes);

        tvInicio = findViewById(R.id.tv_inicio);
        tvQr = findViewById(R.id.tv_qr);
        tvAsistencia = findViewById(R.id.tv_asistencia);
        tvSolicitudes = findViewById(R.id.tv_solicitudes);

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
            loadFragment(new solicitudes());
            updateNavUI(R.id.nav_solicitudes);
        });

        // Estado inicial
        if (savedInstanceState == null) {
            loadFragment(new inicio());
            updateNavUI(R.id.nav_inicio);
        }
    }

    private void updateNavUI(int selectedId) {
        // Resetear todos a estado inactivo
        resetItem(navInicio, ivInicio, tvInicio);
        resetItem(navQr, ivQr, tvQr);
        resetItem(navAsistencia, ivAsistencia, tvAsistencia);
        resetItem(navSolicitudes, ivSolicitudes, tvSolicitudes);

        // Aplicar estado activo al seleccionado
        if (selectedId == R.id.nav_inicio) {
            setActiveItem(navInicio, ivInicio, tvInicio);
        } else if (selectedId == R.id.nav_qr) {
            setActiveItem(navQr, ivQr, tvQr);
        } else if (selectedId == R.id.nav_asistencia) {
            setActiveItem(navAsistencia, ivAsistencia, tvAsistencia);
        } else if (selectedId == R.id.nav_solicitudes) {
            setActiveItem(navSolicitudes, ivSolicitudes, tvSolicitudes);
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
}