package com.example.demo.entity;

public class JwtAuthenticationDetails {
    private String email;
    private Long userId;
    private String role;

    public JwtAuthenticationDetails(String email, Long userId, String role) {
        this.email = email;
        this.userId = userId;
        this.role = role;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
