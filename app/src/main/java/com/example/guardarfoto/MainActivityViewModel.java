package com.example.guardarfoto;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> bitmapLiveData = new MutableLiveData<>();
    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private String savedImagePath;

    public MainActivityViewModel(Application application) {
        super(application);
    }

    public LiveData<Bitmap> getBitmapLiveData() {
        return bitmapLiveData;
    }

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
        bitmapLiveData.setValue(ApiClient.loadImageFromUri(getApplication(), uri));  // Preview image
    }

    public void loadSavedImage(String imagePath) {
        Bitmap bitmap = ApiClient.loadImageFromPath(getApplication(), imagePath);
        // Provoca que se cargue la imagen seleccionada en la vista
        bitmapLiveData.setValue(bitmap);
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

    public void saveUsuario(String email, String password) {
        if (selectedImageUri.getValue() != null) {
            try {
                savedImagePath = ApiClient.saveImageToInternalStorage(getApplication(), selectedImageUri.getValue());
                bitmapLiveData.setValue(ApiClient.loadImageFromPath(getApplication(), savedImagePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Usuario usuario = new Usuario(email, password, savedImagePath);
        ApiClient.saveUsuario(getApplication(), usuario);
        usuarioLiveData.setValue(usuario);  // Guardar usuario y actualizar vista
    }
}
