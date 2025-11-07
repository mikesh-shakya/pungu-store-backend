package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.*;
import jakarta.validation.Valid;

/**
 * Service interface for managing user authentication and registration.
 */
public interface UserService {

    /**
     * Fetch a user by their user ID.
     *
     * @param userId the ID of the user
     * @return Optional containing the User, if found
     */
    UserResponseDTO getUserById(Long userId);


    /**
     * Fetch a username by their user ID.
     *
     * @param userId the ID of the user
     * @return Optional containing the User, if found
     */
    String getUserNameById(Long userId);


    /**
     * Register a new user using the registration request.
     *
     * @param request the user registration data
     * @return the registered User
     */
    UserResponseDTO registerUser(UserRegisterDTO request);

    /**
     * Log in a user using their credentials.
     *
     * @param request the authentication request containing username/email and password
     * @return a JWT token if credentials are valid
     */
    AuthResponse loginUser(AuthRequest request);

    /**
     * Update the role of a user.
     *
     * @param userId the ID of the user
     * @param role the new role (e.g., ADMIN, USER, AUTHOR)
     * @return the updated User
     */
    UserResponseDTO updateRole(long userId, String role);

    UserResponseDTO updateUser(Long userId, @Valid UserUpdateDTO request);
}
