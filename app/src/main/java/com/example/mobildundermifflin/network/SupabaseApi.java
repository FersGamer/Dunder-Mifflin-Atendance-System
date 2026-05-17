package com.example.mobildundermifflin.network;

import com.example.mobildundermifflin.models.Cuenta;
import com.example.mobildundermifflin.models.Empleado;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.Query;

public interface SupabaseApi {

    @GET("cuenta")
    Call<List<Cuenta>> getCuentaPorUsuario(
            @retrofit2.http.Query("usuario_cuenta") String filtro,
            @retrofit2.http.Query("select") String select
    );

    @GET("empleado")
    Call<List<Empleado>> getEmpleadoPorId(
            @Query("id_empleado") String id,
            @Query("select") String select
    );

    @Headers("Prefer: return=minimal")
    @PATCH("empleado")
    Call<Void> actualizarFoto(
            @Query("id_empleado") String id,
            @Body Map<String, String> body
    );

    @Headers("Prefer: return=minimal")

    // Método para actualizar campos específicos de la tabla cuenta
    @PATCH("rest/v1/cuenta")
    Call<Void> actualizarPrimerInicio(
            @Query("id_cuenta") String idCuentaEq, // Filtro para saber qué fila actualizar
            @Body Map<String, Object> body         // Los datos a cambiar
    );
}