package com.pungu.store.auth_service;

import com.pungu.store.auth_service.dtos.UserRegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterDTOTest {

    private final Validator validator;

    public UserRegisterDTOTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserRegistrationRequest() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordTooShort() {
        UserRegisterDTO request = new UserRegisterDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("123");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Password must be at least 8 characters long", violations.iterator().next().getMessage());
    }
}
