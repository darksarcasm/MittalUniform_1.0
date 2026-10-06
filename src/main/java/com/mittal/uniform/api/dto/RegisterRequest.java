package com.mittal.uniform.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class RegisterRequest {

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8)
    private String password;

    private Set<String> roles;

    // --- Profile Enrichment Data ---
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotBlank
    private String gender;

    // --- Embedded Address Data ---
    private AddressRequest addressRequest;

    // Getters and Setters for all fields...
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public String getPhone() { return phone; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressRequest getAddressRequest() {
        return addressRequest;
    }

    public void setAddressRequest(AddressRequest addressRequest) {
        this.addressRequest = addressRequest;
    }
}