package com.example.guardarfoto;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String fotoPerfil;  // Ruta de la imagen
    private String email;
    private String password;

    public Usuario(String fotoPerfil, String email, String password) {
        this.fotoPerfil = fotoPerfil;
        this.email = email;
        this.password = password;
    }

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Usuario() {
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
