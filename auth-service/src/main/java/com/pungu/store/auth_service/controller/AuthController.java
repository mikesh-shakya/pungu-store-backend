package com.pungu.store.auth_service.controller;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.AuthResponse;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.security.JWTTokenHelper;
import com.pungu.store.auth_service.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling authentication requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JWTTokenHelper jwtTokenHelper;

    /**
     * Registers a new user and returns safe user details.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRegistrationRequest request) {
        UserResponseDTO registeredUser = userService.registerUser(request);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * Authenticates the user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = userService.loginUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Validates the provided JWT token sent in the Authorization header.
     * Used by the API Gateway or other microservices to check token validity.
     *
     * @param authHeader the Authorization header (must start with "Bearer ")
     * @return 200 OK with token username and roles if valid, 401 Unauthorized if invalid or expired
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            // Extract required info (example: username and roles).. These methods first extract all claims and then returns the info...
            String username = jwtTokenHelper.getUserNameFromToken(token);
            List<String> roles = jwtTokenHelper.getRolesFromToken(token);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("roles", roles);
            response.put("valid", true);

            return ResponseEntity.ok(response);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token expired"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }
    }
}