package com.example.lab2_20222297;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2_20222297.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String CODIGO_PUCP = "20222297";

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Muestra el nombre solo cuando el código ingresado coincide
        binding.etCodigoPucp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().equals(CODIGO_PUCP)) {
                    binding.tvNombreEstudiante.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNombreEstudiante.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnLogin.setOnClickListener(v -> intentarIngresar());
    }

    private void intentarIngresar() {
        String codigoIngresado = binding.etCodigoPucp.getText().toString().trim();

        if (codigoIngresado.isEmpty()) {
            binding.etCodigoPucp.setError("Ingrese su código PUCP");
            return;
        }

        if (!codigoIngresado.equals(CODIGO_PUCP)) {
            Toast.makeText(this, "Código PUCP incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!hayConexionInternet()) {
            Toast.makeText(this, R.string.error_sin_internet, Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
