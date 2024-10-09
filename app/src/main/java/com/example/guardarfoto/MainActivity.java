package com.example.guardarfoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.guardarfoto.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private ActivityResultLauncher<Intent> galleryLauncher;

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

        // Observar cambios en el LiveData del Bitmap
        viewModel.getImageBitmap().observe(this, bitmap -> {
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show();
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

        // Cargar imagen al iniciar la app
        viewModel.loadSavedImage();

        // Seleccionar imagen
        binding.buttonSelectImage.setOnClickListener(v -> {
            if (!PermissionUtils.checkGalleryPermission(this)) {
                PermissionUtils.requestGalleryPermission(this, 100);
            } else {
                openGallery();
            }
        });

        // Guardar imagen
        binding.buttonSaveImage.setOnClickListener(v -> {
            viewModel.saveImageToInternalStorage();  // Guardar la imagen a través del ViewModel
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
}
