package com.example.mobildundermifflin;

import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class asistencia extends Fragment {

    public enum TipoRegistro { PUNTUAL, RETRASADO, AUSENTE, INCAPACIDAD }

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

    private final List<RegistroAsistencia> todosLosRegistros = Arrays.asList(
            new RegistroAsistencia("17 de Mayo","08:55 AM","06:05 PM", TipoRegistro.PUNTUAL, Calendar.MAY, 2026),
            new RegistroAsistencia("16 de Mayo","09:12 AM","06:00 PM", TipoRegistro.RETRASADO, Calendar.MAY, 2026),
            new RegistroAsistencia("14 de Mayo", null, null, TipoRegistro.AUSENTE, Calendar.MAY, 2026),
            new RegistroAsistencia("15 de Mayo","08:48 AM","06:15 PM", TipoRegistro.PUNTUAL, Calendar.MAY, 2026),
            new RegistroAsistencia("1 de Abril","08:58 AM","06:00 PM", TipoRegistro.PUNTUAL, Calendar.APRIL, 2026),
            new RegistroAsistencia("2 de Abril","09:00 AM","06:00 PM", TipoRegistro.PUNTUAL, Calendar.APRIL, 2026),
            new RegistroAsistencia("31 de Enero", null, null, TipoRegistro.INCAPACIDAD, Calendar.JANUARY, 2026)
    );

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

        actualizarVista();
    }

    private void actualizarVista() {
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
            chip.getBackground().setTintList(null); // Reset tint if needed or use specific color
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

            if (r.horaEntrada != null) {
                layoutHoras.setVisibility(View.VISIBLE);
                tvSinRegistro.setVisibility(View.GONE);
                ((TextView) item.findViewById(R.id.tvEntrada)).setText(r.horaEntrada);
                ((TextView) item.findViewById(R.id.tvSalida)).setText(r.horaSalida);
                if (r.tipo == TipoRegistro.RETRASADO) {
                    ((TextView) item.findViewById(R.id.tvEntrada)).setTextColor(Color.RED);
                }
            } else {
                layoutHoras.setVisibility(View.GONE);
                tvSinRegistro.setVisibility(View.VISIBLE);
            }

            TextView tvEstado = item.findViewById(R.id.tvEstado);
            tvEstado.setText(r.tipo.name());
            
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
}