package com.example.mobildundermifflin;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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

import com.example.mobildundermifflin.models.Asistencia;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.example.mobildundermifflin.utils.UIHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class qrLector extends Fragment {

    private CountDownTimer timer;
    private TextView tvContador, tvNombreQr;
    private ImageView ivQr;
    private ProgressBar progressContador;
    private MaterialButton btnEntrada, btnSalida;
    private LinearLayout layoutRegistros;
    private boolean modoEntrada = true;
    private List<Asistencia> listaAsistencias;

    public qrLector() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_lector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvContador     = view.findViewById(R.id.tvContador);
        ivQr           = view.findViewById(R.id.ivQrCode);
        progressContador = view.findViewById(R.id.progressContador);
        btnEntrada     = view.findViewById(R.id.btnEntrada);
        btnSalida      = view.findViewById(R.id.btnSalida);
        tvNombreQr     = view.findViewById(R.id.tvNombreQr);
        layoutRegistros = view.findViewById(R.id.layoutRegistros);

        // Cargar foto en toolbar
        ShapeableImageView ivToolbar = view.findViewById(R.id.ivProfileToolbar);
        if (ivToolbar != null) UIHelper.cargarFotoToolbar(requireContext(), ivToolbar);

        // Datos de sesión
        String nombres  = SessionManager.getNombres(requireContext());
        String apellido = SessionManager.getApellidoPaterno(requireContext());
        int idEmpleado  = SessionManager.getIdEmpleado(requireContext());

        if (tvNombreQr != null) {
            tvNombreQr.setText(nombres + " " + apellido);
        }

        // Generar QR con datos del empleado
        // El QR contiene: id_empleado|tipo (entrada o salida)
        generarQr(idEmpleado + "|entrada");
        iniciarContador(idEmpleado);

        // Botones entrada/salida
        btnEntrada.setOnClickListener(v -> {
            if (modoEntrada) return; // Evita recargar si ya está en entrada
            modoEntrada = true;
            setButtonActive(btnEntrada, btnSalida);
            generarQr(idEmpleado + "|entrada");
            iniciarContador(idEmpleado);
            mostrarRegistros(); // Actualiza los textos de la lista
        });

        btnSalida.setOnClickListener(v -> {
            if (!modoEntrada) return; // Evita recargar si ya está en salida
            modoEntrada = false;
            setButtonActive(btnSalida, btnEntrada);
            generarQr(idEmpleado + "|salida");
            iniciarContador(idEmpleado);
            mostrarRegistros(); // Actualiza los textos de la lista
        });

        // Cargar últimas asistencias
        cargarUltimasAsistencias(idEmpleado);
    }

    // Método nuevo para limpiar y volver a dibujar la lista desde la caché
    private void mostrarRegistros() {
        if (!isAdded() || layoutRegistros == null || listaAsistencias == null) return;

        layoutRegistros.removeAllViews();
        for (Asistencia a : listaAsistencias) {
            agregarRegistro(a);
        }
    }

    private void generarQr(String contenido) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(contenido, BarcodeFormat.QR_CODE, 400, 400);
            if (ivQr != null) ivQr.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("QR", "Error generando QR: " + e.getMessage());
        }
    }

    private void iniciarContador(int idEmpleado) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long ms) {
                int seg = (int)(ms / 1000);
                if (tvContador != null) tvContador.setText(seg + "s");
                if (progressContador != null) progressContador.setProgress(seg);
            }

            @Override
            public void onFinish() {
                // Regenerar QR al expirar
                String tipo = modoEntrada ? "entrada" : "salida";
                generarQr(idEmpleado + "|" + tipo);
                iniciarContador(idEmpleado);
            }
        }.start();
    }

    private void cargarUltimasAsistencias(int idEmpleado) {
        SupabaseClient.getApi()
                .getUltimasAsistencias("eq." + idEmpleado, "estado,estatus,fecha,hora_entrada,hora_salida", "fecha.desc", "5")
                .enqueue(new Callback<List<Asistencia>>() {
                    @Override
                    public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {
                        if (!isAdded() || getActivity() == null) return;
                        if (response.isSuccessful() && response.body() != null) {
                            listaAsistencias = response.body();
                            getActivity().runOnUiThread(() -> {
                                // Llamamos al método que dibuja la lista
                                mostrarRegistros();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                        Log.e("QR", "Error asistencias: " + t.getMessage());
                    }
                });
    }

    private void agregarRegistro(Asistencia a) {
        // 1. Determinar qué estatus evaluar según la pestaña seleccionada
        String estatusActual;
        String horaActual;
        String prefijoHora;

        if (modoEntrada) {
            estatusActual = a.estado != null ? a.estado.toLowerCase() : "sin registro";
            horaActual = a.hora_entrada;
            prefijoHora = "Entrada: ";
        } else {
            estatusActual = a.estatus != null ? a.estatus.toLowerCase() : "sin registro";
            horaActual = a.hora_salida;
            prefijoHora = "Salida: ";
        }

        // 2. Asignar color y etiqueta basándonos en el estatus evaluado
        String color;
        String etiqueta;

        // Ajusta estos "case" según los valores exactos que guardes en tu base de datos
        switch (estatusActual) {
            case "activo":
            case "salida a tiempo":
            case "puntual":
                color = "#2E7D32"; // Verde
                etiqueta = "A tiempo";
                break;
            case "leve_retraso":
            case "retraso":
            case "retraso mayor":
                color = "#A67857"; // Naranja/Marrón
                etiqueta = "Retardo";
                break;
            case "salida anticipada": // Ejemplo de estatus de salida
                color = "#F57C00"; // Naranja
                etiqueta = "Salida anticipada";
                break;
            case "sin registro":
                color = "#9E9E9E"; // Gris
                etiqueta = "Sin registro";
                break;
            default:
                color = "#753D30"; // Rojo oscuro
                etiqueta = "Falta / Irregular";
                break;
        }

        // 3. Crear vista del registro
        View item = LayoutInflater.from(getContext())
                .inflate(R.layout.item_registro, layoutRegistros, false);

        TextView tvTipo   = item.findViewById(R.id.tvTipoRegistro);
        TextView tvFecha  = item.findViewById(R.id.tvFechaRegistro);
        TextView tvEstado = item.findViewById(R.id.tvEstado);

        // 4. Poblar los datos en la vista
        tvTipo.setText(horaActual != null ? prefijoHora + horaActual : prefijoHora + "--:--");
        tvFecha.setText(a.fecha != null ? a.fecha : "Sin fecha");
        tvEstado.setText(etiqueta);
        tvEstado.setTextColor(Color.parseColor(color));

        layoutRegistros.addView(item);
    }

    private void setButtonActive(MaterialButton active, MaterialButton inactive) {
        active.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
        active.setTextColor(Color.WHITE);
        active.setIconTint(ColorStateList.valueOf(Color.WHITE));

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