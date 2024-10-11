package com.example.guardarfoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ApiClient {

    private static final String USUARIO_DAT = "usuario.dat";
    private static final String IMAGEN_NAME = "fotoPerfil.png";

    // Metodo estático para guardar imagen en almacenamiento interno
    public static String saveImageToInternalStorage(Context context, Uri imageUri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        File imageFile = new File(context.getFilesDir(), IMAGEN_NAME);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, fos);
        }

        inputStream.close();
        Log.d("ApiClient", "Imagen guardada en: " + imageFile.getAbsolutePath());
        return imageFile.getAbsolutePath();  // Devolver la ruta del archivo guardado
    }

    // Metodo estático para cargar imagen desde el almacenamiento interno
    public static Bitmap loadImageFromPath(Context context, String imagePath) {
        File imageFile = new File(imagePath);
        Uri uri = Uri.fromFile(imageFile);
        return loadImageFromUri(context, uri);
    }

    // Cargar Bitmap a partir del Uri
    public static Bitmap loadImageFromUri(Context context, Uri uri) {
        if (uri != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Guardar el objeto Usuario
    public static void saveUsuario(Context context, Usuario usuario) {
        try (FileOutputStream fos = context.openFileOutput(USUARIO_DAT, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Leer el objeto Usuario desde el archivo
    public static Usuario getUsuario(Context context) {
        Usuario usuario = null;
        try (FileInputStream fis = context.openFileInput(USUARIO_DAT);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            usuario = (Usuario) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }
}
