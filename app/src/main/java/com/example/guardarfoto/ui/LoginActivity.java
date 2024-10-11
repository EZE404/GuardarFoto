package com.example.guardarfoto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.guardarfoto.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel viewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar ViewModel
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginActivityViewModel.class);

        // Observador para manejar los resultados del login
        viewModel.getLoginResult().observe(this, logged -> {
            if (logged) {
                // Redirigir a MainActivity con datos precargados
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("precargarDatos", true);
                startActivity(intent);
                //finish();
            } else {
                // Mostrar mensaje de error
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acciones de los botones
        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            viewModel.login(email, password);
        });

        binding.buttonRegister.setOnClickListener(v -> {
            // Redirigir a MainActivity sin datos precargados
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("isRegister", true);
            startActivity(intent);
            //finish();
        });
    }
}
