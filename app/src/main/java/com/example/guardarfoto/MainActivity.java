package com.example.guardarfoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.guardarfoto.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar la vista usando View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instanciar ViewModel usando AndroidViewModelFactory
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MainActivityViewModel.class);

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Permiso a galería rechazado", Toast.LENGTH_LONG).show();
                    }
                });

        // Observar cambios en el usuario
        viewModel.getUsuarioLiveData().observe(this, usuario -> {
            if (usuario != null) {
                // Cargar email y password en los campos de texto
                binding.etEmail.setText(usuario.getEmail());
                binding.etPassword.setText(usuario.getPassword());
            } else {
                Toast.makeText(this, "No se encontraron datos de usuario", Toast.LENGTH_SHORT).show();
            }
        });

        // Observar cambios en el LiveData del Bitmap
        viewModel.getBitmapMutableLiveData().observe(this, bitmap -> {
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "No tiene imagen de perfil", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar lanzador de galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        viewModel.setSelectedImageUri(imageUri);  // Pasar el Uri al ViewModel
                    }
                });



        // Seleccionar imagen
        binding.buttonSelectImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Se necesita permiso para acceder a la galería", Toast.LENGTH_LONG).show();
            } else {
                requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        // Guardar usuario e imagen
        binding.btRegGuardar.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese email y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }
            // Crear o actualizar el objeto Usuario en el ViewModel
            //viewModel.saveImagenSeteada(); // va a generar el imagePath para saveUsuario en el viewmodel
            viewModel.saveUsuario(new Usuario(email, password)); // el imagePath lo tiene el viewmodel
            Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show();
        });

        // Cargar datos del usuario al iniciar la app
        viewModel.loadUsuario();  // Cargar los datos guardados del usuario
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}
