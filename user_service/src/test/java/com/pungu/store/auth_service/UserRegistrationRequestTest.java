package com.pungu.store.auth_service;

import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import jakarta.validation.*;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationRequestTest {

    private final Validator validator;

    public UserRegistrationRequestTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordTooShort() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("123");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("Password must be at least 8 characters long", violations.iterator().next().getMessage());
    }
}
