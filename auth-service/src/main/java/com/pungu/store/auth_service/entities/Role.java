package com.pungu.store.auth_service.entities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Enum representing different user roles.
 */
public enum Role {
    ADMIN,
    USER,
    AUTHOR;

    private static final Logger logger = LoggerFactory.getLogger(Role.class);

    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            throw new IllegalArgumentException("Role string is null or empty");
        }
        logger.debug("Received role string: {}", roleStr);
        logger.debug("Available roles: {}", Arrays.toString(Role.values()));

        return Arrays.stream(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(roleStr))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + roleStr));
    }
}