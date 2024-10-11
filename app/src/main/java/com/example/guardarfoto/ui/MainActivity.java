package com.example.guardarfoto.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
        // Ver si se debe precargar o no
        boolean precargarDatos = getIntent().getBooleanExtra("precargarDatos", false);
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
        binding.buttonDeleteImage.setOnClickListener(v -> removeImage());
        // Cargar datos de usuario si existen
        // Al final del onCreate para asegurarse de que se creen los observadores
        if (precargarDatos) {
            viewModel.loadUsuario();  // Precargar datos del usuario
        } else {
            binding.etName.setText("");
            binding.etLastName.setText("");
            binding.etDni.setText("");
            binding.etEmail.setText("");
            binding.etPassword.setText("");
            binding.imageView.setImageBitmap(null);
            binding.buttonDeleteImage.setVisibility(View.GONE);
        }
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
                binding.etName.setText(usuario.getName());
                binding.etLastName.setText(usuario.getLastName());
                binding.etDni.setText(usuario.getDni());
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
                binding.buttonDeleteImage.setVisibility(View.VISIBLE);  // Mostrar botón de eliminar
            } else {
                binding.imageView.setImageBitmap(null);
                binding.buttonDeleteImage.setVisibility(View.GONE);  // Ocultar botón de eliminar
                Toast.makeText(this, "No hay imagen de Usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeImage() {
        viewModel.eliminarImagenPrevisualizacion();  // Solo eliminar previsualización
        Toast.makeText(this, "Imagen eliminada de la previsualización", Toast.LENGTH_SHORT).show();
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
        String name = binding.etName.getText().toString();
        String lastName = binding.etLastName.getText().toString();
        String dni = binding.etDni.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.saveUsuario(email, password, name, lastName, dni);
        Toast.makeText(this, "Datos de Usuario guardados", Toast.LENGTH_SHORT).show();
    }
}
