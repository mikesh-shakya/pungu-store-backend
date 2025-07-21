package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.entities.Users;
import com.pungu.store.auth_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication requests.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final UserService userService;

    @PostMapping("/register") // API endpoint to register a new user
    public ResponseEntity<Users> registerUser(@RequestBody UserRegistrationRequest request) {
        Users registeredUsers = userService.registerUser(request);
        return ResponseEntity.ok(registeredUsers);
    }

    @PostMapping("/login") // API endpoint for user login
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        String token = userService.loginUser(request);
        return ResponseEntity.ok(token);
    }

}
