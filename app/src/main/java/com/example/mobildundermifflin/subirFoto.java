package com.example.mobildundermifflin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class subirFoto extends Fragment {

    public subirFoto() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subir_foto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnFinRegistro = view.findViewById(R.id.btnFinRegistro);
        TextView tvOmitir = view.findViewById(R.id.tvOmitir);

        if (btnFinRegistro != null) {
            btnFinRegistro.setOnClickListener(v -> finalizarFlujo());
        }

        if (tvOmitir != null) {
            tvOmitir.setOnClickListener(v -> finalizarFlujo());
        }
    }

    private void finalizarFlujo() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            // Cerramos PrimerInicio para que no se pueda volver al registro con el botón "atrás"
            getActivity().finish();
        }
    }
}