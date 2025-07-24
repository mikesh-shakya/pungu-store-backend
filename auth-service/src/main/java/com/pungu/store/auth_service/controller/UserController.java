package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling user-related HTTP requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Get a list of all users in the system.
     *
     * @return List of user details (excluding sensitive fields)
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by their unique ID.
     *
     * @param userId the ID of the user
     * @return User details if found, otherwise 404 Not Found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("userId") Long userId) {
        Optional<UserResponseDTO> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update the role of a user by ID.
     *
     * @param userId the ID of the user
     * @param role the new role to assign
     * @return Updated user details
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable("userId") Long userId, @RequestBody String role) {
        UserResponseDTO updatedUser = userService.updateRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }
}