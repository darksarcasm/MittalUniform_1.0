package com.mittal.uniform.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class LoginRequest {
    @NotBlank(message = "Email or phone no. is required")
    private String loginIdentifier;

    @NotBlank(message = "Password is required")
    private String password;

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public String getPassword() {
        return password;
    }
}