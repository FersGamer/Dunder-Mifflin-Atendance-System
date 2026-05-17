package com.example.mobildundermifflin.models;

import com.google.gson.annotations.SerializedName;

public class Vacaciones {
    @SerializedName("dias_otorgados")
    public int diasOtorgados;

    @SerializedName("dias_consumidos")
    public int diasConsumidos;
}