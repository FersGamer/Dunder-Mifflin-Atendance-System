package com.example.mobildundermifflin;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RegistroAdapter {

    // ✅ Modelo — agrega aquí los campos que necesites
    public static class Registro {
        public String tipo;       // "Entrada de Turno" / "Salida" / "Uso de Bici"
        public String fecha;      // "09:02 AM • 24 Oct 2023"
        public String estado;     // "A TIEMPO" / "TARDE" / "COMPLETADO"
        public String colorEstado;// hex del color del badge

        public Registro(String tipo, String fecha,
                        String estado, String colorEstado) {
            this.tipo        = tipo;
            this.fecha       = fecha;
            this.estado      = estado;
            this.colorEstado = colorEstado;
        }
    }

    // ✅ Llenar el LinearLayout con los registros
    public static void cargar(LinearLayout contenedor, List<Registro> registros) {
        contenedor.removeAllViews();

        for (Registro r : registros) {
            View item = LayoutInflater.from(contenedor.getContext())
                    .inflate(R.layout.item_registro, contenedor, false);

            ((TextView) item.findViewById(R.id.tvTipoRegistro)).setText(r.tipo);
            ((TextView) item.findViewById(R.id.tvFechaRegistro)).setText(r.fecha);

            TextView tvEstado = item.findViewById(R.id.tvEstado);
            tvEstado.setText(r.estado);
            tvEstado.getBackground().setTint(Color.parseColor(r.colorEstado));

            contenedor.addView(item);
        }
    }
}
