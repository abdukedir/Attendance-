package com.example.myapplication;

public class User {
    private String email;
    private String role;

    // Constructor for Firebase Firestore
    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
