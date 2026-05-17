package com.example.mobildundermifflin.network;

import com.example.mobildundermifflin.models.AuthRequest;
import com.example.mobildundermifflin.models.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SupabaseAuthApi {
    @POST("token?grant_type=password")
    Call<AuthResponse> signIn(@Body AuthRequest request);
}