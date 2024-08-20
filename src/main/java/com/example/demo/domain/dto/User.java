package com.example.demo.domain.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private UUID accessToken;

    public User() {}

    public User(String username, String email, UUID accessToken){
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(UUID accessToken) {
        this.accessToken = accessToken;
    }
}
