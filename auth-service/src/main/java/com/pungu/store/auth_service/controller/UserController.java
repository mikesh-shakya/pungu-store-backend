package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.UserRegisterDTO;
import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.dtos.UserUpdateDTO;
import com.pungu.store.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(userService.getUserById(userId));
    }


    /**
     * Update the role of a user by ID.
     *
     * @param userId the ID of the user
     * @param role   the new role to assign
     * @return Updated user details
     */
    @PostMapping("/{userId}/role")
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
    @PatchMapping("/{userId}")
    public UserResponseDTO updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateDTO request
    ) {
        return userService.updateUser(userId, request);
    }

    /**
     * Get a username by their unique ID.
     *
     * @param userId the ID of the user
     * @return User details if found, otherwise 404 Not Found
     */
    @GetMapping("/username/{userId}")
    public ResponseEntity<String> getUserNameById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserNameById(userId));
    }


}