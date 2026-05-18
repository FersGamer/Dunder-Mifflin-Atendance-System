package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class Horario {
    @SerializedName("turno")
    public String turno;

    @SerializedName("hora_entrada")
    public String horaEntrada;

    @SerializedName("hora_salida")
    public String horaSalida;
}
