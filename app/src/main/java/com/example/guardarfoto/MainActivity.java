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

        // Relacionar vista con view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instanciar ViewModel
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MainActivityViewModel.class);

        // Rutina de permisos y callbacks para abrir galería
        setupGalleryLauncher();
        // Observadores
        observeViewModel();

        // Listeners para los botones. Se usan lambdas porque las clases internas son un dolor de ojos
        binding.buttonSelectImage.setOnClickListener(v -> checkGalleryPermissionAndOpenGallery());
        binding.btRegGuardar.setOnClickListener(v -> saveUsuario());

        // Cargar datos de usuario si existen
        viewModel.loadUsuario();  // Al final del onCreate para asegurarse de que se creen los observadores
    }

    private void setupGalleryLauncher() {
        // Registrar el callback para abrir la galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        viewModel.setSelectedImageUri(imageUri);  // Solo previsualización
                    }
                });
        // Registrar el callback para manejar la respuesta al permiso solicitado
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso a galería denegado", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void observeViewModel() {
        // Observer para los datos de usuario
        viewModel.getUsuarioLiveData().observe(this, usuario -> {
            if (usuario != null) {
                binding.etEmail.setText(usuario.getEmail());
                binding.etPassword.setText(usuario.getPassword());
                if (usuario.getProfileImagePath() != null) {
                    viewModel.loadSavedImage(usuario.getProfileImagePath());
                }
            } else {
                Toast.makeText(this, "No hay datos de Usuario", Toast.LENGTH_SHORT).show();
            }
        });

        // Observer para la view image
        viewModel.getBitmapLiveData().observe(this, bitmap -> {
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "No hay imagen de Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkGalleryPermissionAndOpenGallery() {
        // Verificar si el permiso ya fue otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
            // Si el permiso no fue otorgado porque requiere una explicación, darla
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Se requiere permiso para acceder a la galería", Toast.LENGTH_LONG).show();
            // Si el permiso no fue otorgado, solicitarlo
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void saveUsuario() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.saveUsuario(email, password);
        Toast.makeText(this, "Datos de Usuario guardados", Toast.LENGTH_SHORT).show();
    }
}
