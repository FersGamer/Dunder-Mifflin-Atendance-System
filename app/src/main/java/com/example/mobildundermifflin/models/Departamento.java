package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class Departamento {
    @SerializedName("id_departamento")
    public int idDepartamento;

    @SerializedName("nombre_departamento")
    public String nombreDepartamento;
}