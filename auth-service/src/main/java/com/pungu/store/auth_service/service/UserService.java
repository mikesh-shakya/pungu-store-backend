package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.AuthResponse;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing user authentication and registration.
 */
@Service
public interface UserService {

    /**
     * Fetch a user by their user ID.
     *
     * @param userId the ID of the user
     * @return Optional containing the User, if found
     */
    Optional<UserResponseDTO> getUserById(Long userId);

    /**
     * Fetch a user by their username.
     *
     * @param username the username
     * @return Optional containing the User, if found
     */
    Optional<UserResponseDTO> getUserByUsername(String username);

    /**
     * Register a new user using the registration request.
     *
     * @param request the user registration data
     * @return the registered User
     */
    UserResponseDTO registerUser(UserRegistrationRequest request);

    /**
     * Log in a user using their credentials.
     *
     * @param request the authentication request containing username/email and password
     * @return a JWT token if credentials are valid
     */
    AuthResponse loginUser(AuthRequest request);

    /**
     * Get a list of all registered users.
     *
     * @return list of users (currently returns registration DTOs for simplicity)
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Update the role of a user.
     *
     * @param userId the ID of the user
     * @param role the new role (e.g., ADMIN, USER, AUTHOR)
     * @return the updated User
     */
    UserResponseDTO updateRole(long userId, String role);
}
