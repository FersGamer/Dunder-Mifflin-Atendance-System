package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Empleado {
    @SerializedName("id_empleado")
    public int idEmpleado;

    @SerializedName("nombres")
    public String nombres;

    @SerializedName("apellido_paterno")
    public String apellidoPaterno;

    @SerializedName("apellido_materno")
    public String apellidoMaterno;

    @SerializedName("email")
    public String email;

    @SerializedName("foto_url")
    public String fotoUrl;

    @SerializedName("id_departamento")
    public int idDepartamento;

    @SerializedName("departamento")
    public Departamento departamento;


    @SerializedName("horario")
    public List<Horario> horario;
}