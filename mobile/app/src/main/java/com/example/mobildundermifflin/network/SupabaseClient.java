package com.example.mobildundermifflin.network;

import com.example.mobildundermifflin.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

public class SupabaseClient {

    private static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    public static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;


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
