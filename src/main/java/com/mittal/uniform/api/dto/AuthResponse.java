package com.mittal.uniform.api.dto;


import java.util.Set;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String email;
    private Set<String> roles;

    public AuthResponse(String token, String email, Set<String> roles) {
        this.token = token;
        this.email = email;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}