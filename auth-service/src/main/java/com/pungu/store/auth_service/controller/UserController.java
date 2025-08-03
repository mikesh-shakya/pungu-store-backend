package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.UserRequestDTO;
import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user-related HTTP requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * Get a user by their unique ID.
     *
     * @param userId the ID of the user
     * @return User details if found, otherwise 404 Not Found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update the role of a user by ID.
     *
     * @param userId the ID of the user
     * @param role   the new role to assign
     * @return Updated user details
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable("userId") Long userId, @RequestBody String role) {
        UserResponseDTO updatedUser = userService.updateRole(userId, role.trim().toUpperCase());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Updates an existing user by ID.
     *
     * @param userId  the ID of the user to update
     * @param request the updated user details
     * @return the updated user response
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserRequestDTO request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }


    /**
     * Deletes an user by ID.
     *
     * @param userId the ID of the user to delete
     * @return a success message
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

}