package com.example.mobildundermifflin.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {

    private static final String SUPABASE_URL = "https://gwbppbdkedatauevokbp.supabase.co";
    public static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd3YnBwYmRrZWRhdGF1ZXZva2JwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg1NDk0MjksImV4cCI6MjA5NDEyNTQyOX0.c_WWKWvQIGgWO0b2L0q5LIXjk5rCLs8_lOJhjHnm1Lo";

    private static Retrofit retrofit = null;
    private static Retrofit authRetrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(msg ->
                    android.util.Log.d("RETROFIT", msg)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("apikey", SUPABASE_KEY)
                                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(request);
                    })
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL + "/rest/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SupabaseAuthApi getAuthApi() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL + "/auth/v1/")
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request request = chain.request().newBuilder()
                                        .addHeader("apikey", SUPABASE_KEY)
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                return chain.proceed(request);
                            })
                            .build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return authRetrofit.create(SupabaseAuthApi.class);
    }

    public static SupabaseApi getApi() {
        return getClient().create(SupabaseApi.class);
    }

    public static SupabaseApi getAuthenticatedApi(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("apikey", SUPABASE_KEY)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(SUPABASE_URL + "/rest/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SupabaseApi.class);
    }
}
