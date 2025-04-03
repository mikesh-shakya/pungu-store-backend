package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.entities.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    Optional<Users> getUserById(Long userId); // Fetch user by ID

    Optional<Users> getUserByUsername(String username); // Fetch user by username

    Users registerUser(UserRegistrationRequest request); // Register a new user

    String loginUser(AuthRequest request); // Returns JWT token after logging in a user

    List<UserRegistrationRequest> getAllUsers();

    Users updateRole(long userId, String role);
}
