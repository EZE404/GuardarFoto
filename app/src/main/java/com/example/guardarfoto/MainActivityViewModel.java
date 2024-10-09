package com.example.guardarfoto;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();

    public MainActivityViewModel(Application application) {
        super(application);
    }

    // Obtener MutableLiveData para Bitmap
    public LiveData<Bitmap> getImageBitmap() {
        return imageBitmap;
    }

    // Asignar Uri de imagen seleccionada
    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
        loadImageFromUri();
    }

    // Cargar Bitmap a partir del Uri seleccionado
    private void loadImageFromUri() {
        if (selectedImageUri.getValue() != null) {
            try {
                Context context = getApplication();
                InputStream inputStream = context.getContentResolver().openInputStream(selectedImageUri.getValue());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                imageBitmap.setValue(bitmap);  // Actualizar el LiveData con el nuevo Bitmap
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Guardar la imagen seleccionada en el almacenamiento interno
    public void saveImageToInternalStorage() {
        if (selectedImageUri.getValue() != null) {
            try {
                Context context = getApplication();
                InputStream inputStream = context.getContentResolver().openInputStream(selectedImageUri.getValue());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                File imageFile = new File(context.getFilesDir(), "perfil.png");
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                }

                inputStream.close();
                imageBitmap.setValue(bitmap);  // Actualizar el LiveData con el nuevo Bitmap
                Toast.makeText(context, "Imagen guardada", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Cargar la imagen guardada desde el almacenamiento interno
    public void loadSavedImage() {
        Context context = getApplication();
        File imageFile = new File(context.getFilesDir(), "perfil.png");
        if (imageFile.exists()) {
            try (FileInputStream fis = new FileInputStream(imageFile)) {
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                imageBitmap.setValue(bitmap);  // Actualizar el LiveData con el Bitmap cargado
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageBitmap.setValue(null);  // Si no hay imagen, establecer null
        }
    }
}
