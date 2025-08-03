package com.pungu.store.auth_service;

import com.pungu.store.auth_service.dtos.UserRequestDTO;
import com.pungu.store.auth_service.entities.User;
import com.pungu.store.auth_service.repository.UserRepository;
import com.pungu.store.auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("Email is already registered", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
    }

    @Test
    void registerUser_ShouldEncryptPasswordAndSaveUser() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("securepassword");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encryptedPassword");

        // Act
        userService.registerUser(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }
}
