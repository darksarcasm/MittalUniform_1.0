package com.mittal.uniform.api.models;

public enum UserRole {
    ROLE_CUSTOMER,
    ROLE_WHOLESALER,
    ROLE_PARTNER,
    ROLE_ADMIN;

    public static UserRole fromString(String roleName) {
        // Normalize the string to uppercase and ensure it has the ROLE_ prefix
        String normalized = roleName.toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        try {
            return UserRole.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Handle invalid roles gracefully or throw a custom exception
            throw new IllegalArgumentException("Unknown security role: " + roleName);
        }
    }
}
