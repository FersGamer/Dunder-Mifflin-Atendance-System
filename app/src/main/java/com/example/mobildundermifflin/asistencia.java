package com.example.mobildundermifflin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobildundermifflin.models.Asistencia;
import com.example.mobildundermifflin.network.SupabaseClient;
import com.example.mobildundermifflin.utils.UIHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class asistencia extends Fragment {

    public enum TipoRegistro { PUNTUAL, RETRASADO, AUSENTE, INCAPACIDAD, DESCONOCIDO }

    public static class RegistroAsistencia {
        public String fecha;
        public String horaEntrada;
        public String horaSalida;
        public TipoRegistro tipo;
        public int mes;
        public int anio;

        public RegistroAsistencia(String fecha, String horaEntrada,
                                  String horaSalida, TipoRegistro tipo,
                                  int mes, int anio) {
            this.fecha       = fecha;
            this.horaEntrada = horaEntrada;
            this.horaSalida  = horaSalida;
            this.tipo        = tipo;
            this.mes         = mes;
            this.anio        = anio;
        }
    }

    private List<RegistroAsistencia> todosLosRegistros = new ArrayList<>();
    private Calendar mesActual = Calendar.getInstance();
    private TipoRegistro filtroActivo = null;
    private LinearLayout layoutRegistros;
    private LinearLayout layoutFiltros;
    private TextView tvMesAnio;
    private ImageButton btnMesSiguiente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asistencia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutRegistros = view.findViewById(R.id.layoutRegistros);
        layoutFiltros   = view.findViewById(R.id.layoutFiltros);
        tvMesAnio       = view.findViewById(R.id.tvMesAnio);
        btnMesSiguiente = view.findViewById(R.id.btnMesSiguiente);

        // Cargar foto en toolbar
        ShapeableImageView ivToolbar = view.findViewById(R.id.ivProfileToolbar);
        if (ivToolbar != null) {
            UIHelper.cargarFotoToolbar(requireContext(), ivToolbar);
        }

        view.findViewById(R.id.btnMesAnterior).setOnClickListener(v -> {
            mesActual.add(Calendar.MONTH, -1);
            actualizarVista();
        });

        if (btnMesSiguiente != null) {
            btnMesSiguiente.setOnClickListener(v -> {
                Calendar hoy = Calendar.getInstance();
                if (mesActual.get(Calendar.YEAR) < hoy.get(Calendar.YEAR) ||
                        (mesActual.get(Calendar.YEAR) == hoy.get(Calendar.YEAR) &&
                                mesActual.get(Calendar.MONTH) < hoy.get(Calendar.MONTH))) {
                    mesActual.add(Calendar.MONTH, 1);
                    actualizarVista();
                }
            });
        }

        fetchAsistencias();
    }

    private void fetchAsistencias() {
        int idEmpleado = SessionManager.getIdEmpleado(requireContext());
        if (idEmpleado == -1) return;

        SupabaseClient.getApi()
                .getAsistenciasPorEmpleado("eq." + idEmpleado, "fecha,hora_entrada,hora_salida,estado")
                .enqueue(new Callback<List<Asistencia>>() {
                    @Override
                    public void onResponse(Call<List<Asistencia>> call, Response<List<Asistencia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            mapearRegistros(response.body());
                            actualizarVista();
                        } else {
                            Log.e("ASISTENCIA", "Error en respuesta: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Asistencia>> call, Throwable t) {
                        Log.e("ASISTENCIA", "Error de red: " + t.getMessage());
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Error al cargar asistencias", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mapearRegistros(List<Asistencia> listaApi) {
        todosLosRegistros.clear();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'de' MMMM", new Locale("es", "MX"));

        for (Asistencia api : listaApi) {
            try {
                Date date = inputFormat.parse(api.fecha);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                TipoRegistro tipo;
                switch (api.estado.toLowerCase()) {
                    case "activo": tipo = TipoRegistro.PUNTUAL; break;
                    case "leve_retraso": tipo = TipoRegistro.RETRASADO; break;
                    case "falta": tipo = TipoRegistro.AUSENTE; break;
                    case "incapacidad": tipo = TipoRegistro.INCAPACIDAD; break;
                    default: tipo = TipoRegistro.DESCONOCIDO; break;
                }

                todosLosRegistros.add(new RegistroAsistencia(
                        outputFormat.format(date),
                        formatTime(api.hora_entrada),
                        formatTime(api.hora_salida),
                        tipo,
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR)
                ));
            } catch (ParseException e) {
                Log.e("ASISTENCIA", "Error parseando fecha: " + api.fecha);
            }
        }
    }

    private String formatTime(String time) {
        if (time == null || time.isEmpty()) return null;
        try {
            // Asumiendo formato "HH:mm:ss" de la BD
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = sdf24.parse(time);
            return sdf12.format(date).toUpperCase();
        } catch (Exception e) {
            return time;
        }
    }

    private void actualizarVista() {
        if (!isAdded()) return;
        actualizarTituloMes();
        construirFiltros();
        aplicarFiltro(filtroActivo);
    }

    private void actualizarTituloMes() {
        if (tvMesAnio == null) return;
        
        String mes = mesActual.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "MX")).toUpperCase();
        int anio = mesActual.get(Calendar.YEAR);
        tvMesAnio.setText(mes + "\n" + anio);

        Calendar hoy = Calendar.getInstance();
        if (btnMesSiguiente != null) {
            boolean esMesActual = mesActual.get(Calendar.MONTH) == hoy.get(Calendar.MONTH) &&
                                 mesActual.get(Calendar.YEAR)  == hoy.get(Calendar.YEAR);
            btnMesSiguiente.setAlpha(esMesActual ? 0.3f : 1.0f);
            btnMesSiguiente.setEnabled(!esMesActual);
        }
    }

    private void construirFiltros() {
        if (layoutFiltros == null) return;
        layoutFiltros.removeAllViews();

        List<RegistroAsistencia> delMes = getRegistrosDelMes();
        long puntual    = delMes.stream().filter(r -> r.tipo == TipoRegistro.PUNTUAL).count();
        long retrasado  = delMes.stream().filter(r -> r.tipo == TipoRegistro.RETRASADO).count();
        long ausente    = delMes.stream().filter(r -> r.tipo == TipoRegistro.AUSENTE).count();
        long incapacidad= delMes.stream().filter(r -> r.tipo == TipoRegistro.INCAPACIDAD).count();

        agregarChip("✓ Todos", "#3B4C39", null);
        agregarChip("⊙ Asistencias " + puntual, "#2E7D32", TipoRegistro.PUNTUAL);
        agregarChip("⊗ Ausencias " + ausente, "#B71C1C", TipoRegistro.AUSENTE);
        agregarChip("⏱ Retrasos " + retrasado, "#E65100", TipoRegistro.RETRASADO);
        agregarChip("✦ Incapacidades " + incapacidad, "#1565C0", TipoRegistro.INCAPACIDAD);
    }

    private void agregarChip(String texto, String colorHex, TipoRegistro tipo) {
        TextView chip = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(20);
        chip.setLayoutParams(params);
        chip.setText(texto);
        chip.setTextSize(12);
        chip.setPadding(40, 20, 40, 20);

        boolean activo = (filtroActivo == tipo);
        chip.setBackgroundResource(activo ? R.drawable.bg_filtro_activo : R.drawable.bg_filtro_inactivo);
        
        int color = Color.parseColor(colorHex);
        if (activo) {
            chip.getBackground().setTint(color);
            chip.setTextColor(Color.WHITE);
        } else {
            chip.setTextColor(color);
            chip.getBackground().setTintList(null);
        }

        chip.setOnClickListener(v -> {
            filtroActivo = tipo;
            actualizarVista();
        });

        layoutFiltros.addView(chip);
    }

    private void aplicarFiltro(TipoRegistro filtro) {
        List<RegistroAsistencia> delMes = getRegistrosDelMes();
        List<RegistroAsistencia> filtrados = (filtro == null) ? delMes :
                delMes.stream().filter(r -> r.tipo == filtro).collect(Collectors.toList());
        mostrarRegistros(filtrados);
    }

    private List<RegistroAsistencia> getRegistrosDelMes() {
        int mes  = mesActual.get(Calendar.MONTH);
        int anio = mesActual.get(Calendar.YEAR);
        List<RegistroAsistencia> result = new ArrayList<>();
        for (RegistroAsistencia r : todosLosRegistros) {
            if (r.mes == mes && r.anio == anio) result.add(r);
        }
        return result;
    }

    private void mostrarRegistros(List<RegistroAsistencia> registros) {
        if (layoutRegistros == null) return;
        layoutRegistros.removeAllViews();

        if (registros.isEmpty()) {
            TextView tvVacio = new TextView(requireContext());
            tvVacio.setText("Sin registros para este período");
            tvVacio.setTextColor(Color.GRAY);
            tvVacio.setPadding(20, 40, 20, 40);
            layoutRegistros.addView(tvVacio);
            return;
        }

        for (RegistroAsistencia r : registros) {
            View item = LayoutInflater.from(requireContext()).inflate(R.layout.item_asistencia, layoutRegistros, false);
            ((TextView) item.findViewById(R.id.tvFecha)).setText(r.fecha);

            View layoutHoras = item.findViewById(R.id.layoutHoras);
            View tvSinRegistro = item.findViewById(R.id.tvSinRegistro);

            if (r.horaEntrada != null && !r.horaEntrada.isEmpty()) {
                layoutHoras.setVisibility(View.VISIBLE);
                tvSinRegistro.setVisibility(View.GONE);
                ((TextView) item.findViewById(R.id.tvEntrada)).setText(r.horaEntrada);
                ((TextView) item.findViewById(R.id.tvSalida)).setText(r.horaSalida != null ? r.horaSalida : "--:--");
                if (r.tipo == TipoRegistro.RETRASADO) {
                    ((TextView) item.findViewById(R.id.tvEntrada)).setTextColor(Color.RED);
                }
            } else {
                layoutHoras.setVisibility(View.GONE);
                tvSinRegistro.setVisibility(View.VISIBLE);
            }

            TextView tvEstado = item.findViewById(R.id.tvEstado);
            tvEstado.setText(getNombreEstado(r.tipo));
            
            int colorEstado = Color.GRAY;
            switch (r.tipo) {
                case PUNTUAL: colorEstado = Color.parseColor("#2E7D32"); break;
                case RETRASADO: colorEstado = Color.parseColor("#E65100"); break;
                case AUSENTE: colorEstado = Color.parseColor("#B71C1C"); break;
                case INCAPACIDAD: colorEstado = Color.parseColor("#1565C0"); break;
            }
            tvEstado.getBackground().setTint(colorEstado);
            tvEstado.setTextColor(Color.WHITE);

            layoutRegistros.addView(item);
        }
    }

    private String getNombreEstado(TipoRegistro tipo) {
        switch (tipo) {
            case PUNTUAL: return "PUNTUAL";
            case RETRASADO: return "RETRASO";
            case AUSENTE: return "AUSENTE";
            case INCAPACIDAD: return "INCAPACIDAD";
            default: return "OTRO";
        }
    }
}
