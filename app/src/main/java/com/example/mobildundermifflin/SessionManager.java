package com.example.mobildundermifflin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobildundermifflin.models.Cuenta;

public class SessionManager {
    private static final String PREF_NAME = "DunderSession";

    public static void guardar(Context ctx, String token, Cuenta cuenta) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putInt("id_empleado", cuenta.idEmpleado);
        editor.putBoolean("primer_inicio", cuenta.primerInicio);
        editor.apply();
    }

    public static String getToken(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString("token", "");
    }

    public static int getIdEmpleado(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt("id_empleado", -1);
    }

    public static void cerrarSesion(Context ctx) {
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}