package com.example.guardarfoto.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.guardarfoto.repositorio.ApiClient;
import com.example.guardarfoto.modelo.Usuario;

public class MainActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> bitmapLiveData = new MutableLiveData<>();
    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private String savedImagePath;
    private boolean imageRemoved = false;

    public MainActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<Bitmap> getBitmapLiveData() {
        return bitmapLiveData;
    }
    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    // Metodo para establecer la imagen seleccionada en la previsualización
    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
        bitmapLiveData.setValue(ApiClient.loadImageFromUri(getApplication(), uri));  // Preview image
        imageRemoved = false;
    }

    // Metodo para eliminar la imagen solo de la previsualización
    public void removeSelectedImage() {
        bitmapLiveData.setValue(null);  // Eliminar de la previsualización
        selectedImageUri.setValue(null);  // Limpiar Uri seleccionada
        imageRemoved = true;  // Marcar como eliminada para su futura eliminación
    }
    public void loadSavedImage(String imagePath) {
        Bitmap bitmap = ApiClient.loadImageFromPath(getApplication(), imagePath);
        // Provoca que se cargue la imagen seleccionada en la vista
        bitmapLiveData.setValue(bitmap);
        savedImagePath = imagePath; // Si el usuario loguea, y guarda sin reseleccionar una imagen, se debe mantener la ruta
    }

    public void loadUsuario() {
        Usuario usuario = ApiClient.getUsuario(getApplication());
        if (usuario != null) {
            usuarioLiveData.setValue(usuario);
            if (usuario.getProfileImagePath() != null) {
                // Provoca que se cargue la imagen guardada en la vista
                loadSavedImage(usuario.getProfileImagePath());
            }
        }
    }

    public void saveUsuario(String email, String password, String name, String lastName, String dni) {
        if (imageRemoved) {
            if (savedImagePath != null) {
                ApiClient.deleteImageFromInternalStorage(getApplication(), savedImagePath);  // Eliminar la imagen del almacenamiento
                savedImagePath = null;  // Limpiar la ruta de la imagen guardada
            }
        } else if (selectedImageUri.getValue() != null) {
            try {
                savedImagePath = ApiClient.saveImageToInternalStorage(getApplication(), selectedImageUri.getValue());
                bitmapLiveData.setValue(ApiClient.loadImageFromPath(getApplication(), savedImagePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Usuario usuario = new Usuario(email, password, savedImagePath, name, lastName, dni);
        ApiClient.saveUsuario(getApplication(), usuario);
        loadUsuario();
    }
}
