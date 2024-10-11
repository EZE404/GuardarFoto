package com.example.guardarfoto;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginActivityViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        // Verificar si los datos coinciden con el usuario registrado
        Usuario usuario = ApiClient.getUsuario(getApplication());
        if (usuario != null && usuario.getEmail().equals(email) && usuario.getPassword().equals(password)) {
            loginResult.setValue(true);
        } else {
            loginResult.setValue(false);
        }
    }
}
