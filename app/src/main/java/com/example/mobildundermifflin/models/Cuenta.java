package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class Cuenta {
    @SerializedName("id_cuenta")
    public int idCuenta;

    @SerializedName("usuario_cuenta")
    public String usuarioCuenta;

    @SerializedName("primer_inicio")
    public boolean primerInicio;

    @SerializedName("id_empleado")
    public int idEmpleado;
}