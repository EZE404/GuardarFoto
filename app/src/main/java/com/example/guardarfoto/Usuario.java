package com.example.guardarfoto;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String profileImagePath;
    private String email;
    private String password;
    private String name;
    private String lastName;
    private String dni;

    public Usuario(String email, String password, @Nullable String imagePath, String name, String lastName, String dni) {
        this.email = email;
        this.password = password;
        this.profileImagePath = imagePath != null ? imagePath : "";  // Valor por defecto
        this.name = name;
        this.lastName = lastName;
        this.dni = dni;
    }

    // Getters y Setters
    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
