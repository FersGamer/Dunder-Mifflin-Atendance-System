package com.example.mobildundermifflin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;
import java.util.List;

public class qrLector extends Fragment {

    private CountDownTimer timer;
    private TextView tvContador;
    private ImageView ivQr;
    private ProgressBar progressContador;
    private MaterialButton btnEntrada, btnSalida;
    private int segundos = 60;

    public qrLector() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_lector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias a las vistas
        tvContador = view.findViewById(R.id.tvContador);
        ivQr = view.findViewById(R.id.ivQrCode);
        progressContador = view.findViewById(R.id.progressContador);
        btnEntrada = view.findViewById(R.id.btnEntrada);
        btnSalida = view.findViewById(R.id.btnSalida);
        TextView tvNombreQr = view.findViewById(R.id.tvNombreQr);
        LinearLayout layoutRegistros = view.findViewById(R.id.layoutRegistros);

        // Datos de sesión
        SharedPreferences sesion = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String nombre   = sesion.getString("nombre", "Usuario");
        String apellido = sesion.getString("apellido", "");
        String id       = sesion.getString("id", "001");

        if (tvNombreQr != null) {
            tvNombreQr.setText(nombre + " " + apellido);
        }

        // Generar QR inicial
        generarQr(id);

        // Iniciar contador
        iniciarContador();

        // Lógica de los botones Entrada/Salida
        btnEntrada.setOnClickListener(v -> setButtonActive(btnEntrada, btnSalida));
        btnSalida.setOnClickListener(v -> setButtonActive(btnSalida, btnEntrada));

        // Registros dinámicos
        List<RegistroAdapter.Registro> registros = Arrays.asList(
            new RegistroAdapter.Registro(
                "Entrada de turno",
                "09:02 AM • 24 Oct 2023",
                "A tiempo", "#2E7D32"),
            new RegistroAdapter.Registro(
                "Entrada de Turno",
                "08:45 AM • 23 Oct 2023",
                "A Tiempo", "#2E7D32"),
            new RegistroAdapter.Registro(
                "Entrada de Turno",
                "10:15 AM • 22 Oct 2023",
                "TARDE", "#753D30")
        );

        if (layoutRegistros != null) {
            RegistroAdapter.cargar(layoutRegistros, registros);
        }
    }

    private void generarQr(String contenido) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(contenido, BarcodeFormat.QR_CODE, 400, 400);
            if (ivQr != null) {
                ivQr.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciarContador() {
        if (timer != null) timer.cancel();
        
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long ms) {
                segundos = (int)(ms / 1000);
                if (tvContador != null) tvContador.setText(segundos + "s");
                if (progressContador != null) progressContador.setProgress(segundos);
            }

            @Override
            public void onFinish() {
                // Regenerar QR al expirar
                SharedPreferences sesion = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
                generarQr(sesion.getString("id", "001"));
                iniciarContador();
            }
        }.start();
    }

    private void setButtonActive(MaterialButton active, MaterialButton inactive) {
        // Estilo para el botón activo (Verde con texto/icono blanco)
        active.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
        active.setTextColor(Color.WHITE);
        active.setIconTint(ColorStateList.valueOf(Color.WHITE));

        // Estilo para el botón inactivo (Blanco con texto/icono gris)
        inactive.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        inactive.setTextColor(Color.parseColor("#857661"));
        inactive.setIconTint(ColorStateList.valueOf(Color.parseColor("#857661")));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) timer.cancel();
    }
}
