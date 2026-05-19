package com.example.mobildundermifflin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobildundermifflin.models.Cuenta;

public class SessionManager {
    private static final String PREF_NAME = "DunderSession";

    public static void guardar(Context ctx, String token, Cuenta cuenta, String nombres, String apellidoPaterno) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putInt("id_empleado", cuenta.idEmpleado);
        editor.putBoolean("primer_inicio", cuenta.primerInicio);
        editor.putString("nombres", nombres);
        editor.putString("apellido_paterno", apellidoPaterno);
        editor.apply();
    }

    public static String getNombres(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("nombres", "");
    }

    public static String getApellidoPaterno(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("apellido_paterno", "");
    }

    public static String getToken(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("token", "");
    }

    public static int getIdEmpleado(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt("id_empleado", -1);
    }

    public static void cerrarSesion(Context ctx) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }

    public static void guardarDatosEmpleado(Context ctx, String nombre, String apellido,
                                            String fotoUrl, String departamento, String turno) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("nombres", nombre);
        editor.putString("apellido_paterno", apellido);
        editor.putString("foto_url", fotoUrl);
        editor.putString("departamento", departamento);
        editor.putString("turno", turno);
        editor.apply();
    }

    public static String getFotoUrl(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("foto_url", "");
    }

    public static String getDepartamento(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("departamento", "");
    }

    public static String getTurno(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("turno", "");
    }
    public static void guardarPrimerInicio(Context ctx, boolean valor) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean("primer_inicio", valor).apply();
    }

    public static void setLastNotificationCount(Context ctx, int count) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putInt("notif_count", count).apply();
    }

    public static int getLastNotificationCount(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt("notif_count", 0);
    }
}