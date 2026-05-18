package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class SolicitudAusencia {
    @SerializedName("id_ausencia")
    public int idAusencia;

    @SerializedName("fecha_inicio")
    public String fechaInicio;

    @SerializedName("fecha_fin")
    public String fechaFin;

    @SerializedName("aprobacion")
    public String aprobacion;

    @SerializedName("fecha_solicitud")
    public String fechaSolicitud;
}