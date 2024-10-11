package com.example.guardarfoto;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String profileImagePath;
    private String email;
    private String password;

    public Usuario(String email, String password, @Nullable String imagePath) {
        this.email = email;
        this.password = password;
        this.profileImagePath = imagePath != null ? imagePath : "";  // Valor por defecto
    }

    // Getters y Setters
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
