package com.pungu.store.auth_service.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public enum Gender {
    MALE, FEMALE;

    private static final Logger logger = LoggerFactory.getLogger(Gender.class);

    public static Gender fromString(String genderStr) {
        if (genderStr == null || genderStr.isBlank()) {
            throw new IllegalArgumentException("Gender string is null or empty");
        }
        logger.debug("Received gender string: {}", genderStr);
        logger.debug("Available genders: {}", Arrays.toString(Gender.values()));

        return Arrays.stream(Gender.values())
                .filter(g -> g.name().equalsIgnoreCase(genderStr))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid gender: " + genderStr));
    }
}
