package com.example.mobildundermifflin.network;

import com.example.mobildundermifflin.models.Asistencia;
import com.example.mobildundermifflin.models.Cuenta;
import com.example.mobildundermifflin.models.Empleado;
import com.example.mobildundermifflin.models.Vacaciones;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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

    @GET("empleado")
    Call<List<Empleado>> getEmpleadoCompleto(
            @Query("id_empleado") String id,
            @Query("select") String select
    );

    @GET("asistencias")
    Call<List<Asistencia>> getAsistenciasPorEmpleado(
            @Query("id_empleado") String id,
            @Query("select") String select
    );

    @GET("saldo_vacaciones")
    Call<List<Vacaciones>> getVacacionesPorEmpleado(
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
    @PATCH("cuenta")
    Call<Void> actualizarPrimerInicio(
            @Query("id_empleado") String idEmpleado,
            @Body Map<String, Object> body
    );

    @GET("asistencias")
    Call<List<Asistencia>> getUltimasAsistencias(
            @Query("id_empleado") String id,
            @Query("select") String select,
            @Query("order") String order,
            @Query("limit") String limit
    );

    @Headers("Prefer: return=minimal")
    @POST("faltas")
    Call<Void> insertarFalta(@Body Map<String, Object> body);

    @Headers("Prefer: return=minimal")
    @POST("solicitudes_ausencia")
    Call<Void> insertarSolicitud(@Body Map<String, Object> body);

    @GET("solicitudes_ausencia")
    Call<List<com.example.mobildundermifflin.models.SolicitudAusencia>> getSolicitudesPorEmpleado(
            @Query("id_empleado") String id,
            @Query("select") String select,
            @Query("order") String order
    );

    @Headers("Prefer: return=minimal")
    @PATCH("solicitudes_ausencia") // Cambia "solicitud_ausencia" por el nombre exacto de tu tabla
    Call<ResponseBody> marcarSolicitudesComoVistas(
            @Query("id_empleado") String filtroEmpleado, // Ejemplo: "eq.5"
            @Query("visto") String filtroVisto,         // Filtramos las que aún están "eq.false"
            @Body Map<String, Object> camposActualizar
    );
}