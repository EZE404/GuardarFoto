package com.example.guardarfoto;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Usuario> usuarioLiveData = new MutableLiveData<>();
    private String savedImagePath;

    public MainActivityViewModel(Application application) {
        super(application);
        loadUsuario();  // Cargar los datos del usuario al iniciar
    }

    public LiveData<Bitmap> getBitmapMutableLiveData() {
        return bitmapMutableLiveData;
    }

    public LiveData<Usuario> getUsuarioLiveData() {
        return usuarioLiveData;
    }

    public void setSelectedImageUri(Uri uri) {
        try {
            selectedImageUri.setValue(uri);
            bitmapMutableLiveData.setValue(ApiClient.loadImageFromUri(getApplication(), uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImagenSeteada() {
        if (selectedImageUri.getValue() != null) {
            try {
                savedImagePath = ApiClient.saveImageToInternalStorage(getApplication(), selectedImageUri.getValue());
                Bitmap bitmap = ApiClient.loadImageFromPath(getApplication(), savedImagePath);
                bitmapMutableLiveData.setValue(bitmap);  // Actualizar el LiveData con el nuevo Bitmap
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadUsuario() {
        try {
            Usuario usuario = ApiClient.getUsuario(getApplication());
            if (usuario != null) {
                if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
                    Bitmap bitmap = ApiClient.loadImageFromPath(getApplication(), usuario.getFotoPerfil());
                    bitmapMutableLiveData.setValue(bitmap);
                } else {
                    bitmapMutableLiveData.setValue(null);  // Sin imagen, poner valor nulo o predeterminado
                }
                usuarioLiveData.setValue(usuario);
            }
            Log.d("loadUsuario", "Imagen cargada desde: " + usuario.getFotoPerfil());
            Log.d("loadUsuario", "Usuario cargado: " + usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveUsuario(Usuario usuario) {
        try {
            saveImagenSeteada();  // Guarda la imagen si hay alguna seleccionada
            if (savedImagePath != null) {
                usuario.setFotoPerfil(savedImagePath);
            } else {
                usuario.setFotoPerfil("");  // Asegura que no sea null
            }
            ApiClient.saveUsuario(getApplication(), usuario);
            usuarioLiveData.setValue(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
