package com.pungu.store.auth_service.entities;


import java.util.Arrays;

/**
 * Enum representing different user roles.
 */
public enum Role {
    ADMIN,
    USER,
    AUTHOR;

    public static Role fromString(String roleStr) {
        return Arrays.stream(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(roleStr))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + roleStr));
    }
}