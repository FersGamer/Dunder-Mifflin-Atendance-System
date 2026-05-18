package com.example.mobildundermifflin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobildundermifflin.network.SupabaseClient;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class subirFoto extends Fragment {

    private android.widget.ImageView btnFoto;
    private static final int REQUEST_CAMERA = 100;
    private Bitmap fotoBitmap = null;
    private ActivityResultLauncher<Intent> camaraLauncher;

    public subirFoto() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registrar el launcher de la cámara
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        fotoBitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (fotoBitmap != null) {
                            // Cambia esto
                            btnFoto.setImageBitmap(fotoBitmap);
                            btnFoto.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                            btnFoto.setPadding(0, 0, 0, 0);
                            btnFoto.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subir_foto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnFoto = (android.widget.ImageView) view.findViewById(R.id.btnFoto);
        Button btnFinRegistro = view.findViewById(R.id.btnFinRegistro);
        TextView tvOmitir = view.findViewById(R.id.tvOmitir);

        // Abrir cámara al tocar la foto
        btnFoto.setOnClickListener(v -> abrirCamara());

        btnFinRegistro.setOnClickListener(v -> {
            if (fotoBitmap == null) {
                Toast.makeText(getContext(), "Toma una foto primero", Toast.LENGTH_SHORT).show();
            } else {

                subirFotoASupabase();
            }
        });

        tvOmitir.setOnClickListener(v -> finalizarFlujo());
    }

    // Reemplaza abrirCamara() con esto:
    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Ya tiene permiso, abrir cámara
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camaraLauncher.launch(intent);
        } else {
            // Solicitar permiso
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    // Agrega este método para manejar la respuesta del permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, abrir cámara
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camaraLauncher.launch(intent);
            } else {
                Toast.makeText(getContext(), "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subirFotoASupabase() {
        int idEmpleado = SessionManager.getIdEmpleado(getContext());
        String token = SessionManager.getToken(getContext());
        String nombres = SessionManager.getNombres(getContext());
        String apellido = SessionManager.getApellidoPaterno(getContext());

        Log.d("SUBIR_FOTO", "idEmpleado: " + idEmpleado);
        Log.d("SUBIR_FOTO", "token vacío: " + token.isEmpty());

        if (idEmpleado == -1 || token.isEmpty()) {
            Toast.makeText(getContext(), "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                // 1. Convertir bitmap a bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] fotoBytes = baos.toByteArray();

                // Generar nombre del archivo: nombre-apellidopaterno
                String nombreLimpio = nombres.split(" ")[0].toLowerCase()
                        .replace("á","a").replace("é","e").replace("í","i")
                        .replace("ó","o").replace("ú","u");
                String apellidoLimpio = apellido.toLowerCase()
                        .replace("á","a").replace("é","e").replace("í","i")
                        .replace("ó","o").replace("ú","u");
                String nombreArchivo = nombreLimpio + "-" + apellidoLimpio + ".jpg";

                String urlStorage = "https://gwbppbdkedatauevokbp.supabase.co/storage/v1/object/avatars/" + nombreArchivo;

                // 2. Subir al Storage
                OkHttpClient client = new OkHttpClient();
                Request uploadRequest = new Request.Builder()
                        .url(urlStorage)
                        .addHeader("apikey", SupabaseClient.SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "image/jpeg")
                        .addHeader("x-upsert", "true")
                        .post(RequestBody.create(fotoBytes, MediaType.parse("image/jpeg")))
                        .build();

                Response uploadResponse = client.newCall(uploadRequest).execute();
                Log.d("SUBIR_FOTO", "Upload código: " + uploadResponse.code());

                if (!uploadResponse.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error al subir foto", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                // 3. Guardar URL pública en empleado
                String fotoUrl = "https://gwbppbdkedatauevokbp.supabase.co/storage/v1/object/public/avatars/" + nombreArchivo;
                Log.d("SUBIR_FOTO", "Actualizando primer_inicio para id_empleado: eq." + idEmpleado);

                Map<String, String> bodyFoto = new HashMap<>();
                bodyFoto.put("foto_url", fotoUrl);
                Log.d("SUBIR_FOTO", "Body foto: " + bodyFoto.toString());
                Log.d("SUBIR_FOTO", "fotoUrl a guardar: " + fotoUrl);


                SupabaseClient.getAuthenticatedApi(token)
                        .actualizarFoto("eq." + idEmpleado, bodyFoto)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                Log.d("SUBIR_FOTO", "foto_url actualizada: " + response.code());

                                // 4. Marcar primer_inicio = false
                                Map<String, Object> bodyInicio = new HashMap<>();
                                bodyInicio.put("primer_inicio", false);
                                Log.d("SUBIR_FOTO", "Body primer_inicio: " + bodyInicio.toString());

                                Log.d("SUBIR_FOTO", "PATCH cuenta URL: cuenta?id_empleado=eq." + idEmpleado);
                                Log.d("SUBIR_FOTO", "PATCH empleado URL: empleado?id_empleado=eq." + idEmpleado);

                                SupabaseClient.getAuthenticatedApi(token)
                                        .actualizarPrimerInicio("eq." + idEmpleado, bodyInicio)
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, retrofit2.Response<Void> r) {
                                                Log.d("SUBIR_FOTO", "primer_inicio actualizado: " + r.code());
                                                requireActivity().runOnUiThread(() -> {
                                                    Toast.makeText(getContext(), "¡Registro completado!", Toast.LENGTH_SHORT).show();
                                                    finalizarFlujo();
                                                });
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                Log.e("SUBIR_FOTO", "Error primer_inicio: " + t.getMessage());
                                            }
                                        });
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("SUBIR_FOTO", "Error foto_url: " + t.getMessage());
                            }
                        });

            } catch (Exception e) {
                Log.e("SUBIR_FOTO", "Error: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    private void finalizarFlujo() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}