package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class Asistencia {
    @SerializedName("id_asistencias")
    public int idAsistencias;

    @SerializedName("estado")
    public String estado;

    @SerializedName("estatus")
    public String estatus;

    @SerializedName("fecha")
    public String fecha;

    @SerializedName("hora_entrada")
    public String hora_entrada;

    @SerializedName("hora_salida")
    public String hora_salida;

    @SerializedName("id_empleado")
    public int idEmpleado;
}