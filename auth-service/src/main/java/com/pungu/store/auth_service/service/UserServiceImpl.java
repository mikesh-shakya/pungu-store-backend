package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.AuthResponse;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.dtos.UserResponseDTO;
import com.pungu.store.auth_service.entities.Role;
import com.pungu.store.auth_service.entities.User;
import com.pungu.store.auth_service.repository.UserRepository;
import com.pungu.store.auth_service.security.JWTTokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.http.impl.auth.BasicScheme.authenticate;

/**
 * Implementation of UserService interface handling authentication and registration.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenHelper jwtTokenHelper;
    private final CustomUserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * Fetch user by ID and convert to DTO.
     */
    @Override
    public Optional<UserResponseDTO> getUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .map(this::convertUserToUserResponseDTO);
    }

    /**
     * Fetch user by username and convert to DTO.
     */
    @Override
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertUserToUserResponseDTO);
    }

    /**
     * Register a new user.
     */
    @Override
    public UserResponseDTO registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // use enum
        user.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(user);

        return convertUserToUserResponseDTO(user);
    }

    /**
     * Authenticate and return JWT token.
     */
    @Override
    public AuthResponse loginUser(AuthRequest request) {
        authenticate(request.getEmail(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt_token = jwtTokenHelper.generateToken(userDetails);
        AuthResponse response = AuthResponse.builder().token(jwt_token).username(userDetails.getUsername()).build();
        return response;
    }

    /**
     * Fetch all users as UserRegistrationRequest (consider changing this to a proper UserDto).
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertUserToUserResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update the role of a user.
     */
    @Override
    public UserResponseDTO updateRole(long userId, String role) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(Role.valueOf(role.toUpperCase())); // throws IllegalArgumentException if invalid
        userRepository.save(user);
        return convertUserToUserResponseDTO(user);
    }

    /**
     * Converts a User entity to a registration-style DTO (temporary).
     */
    private UserResponseDTO convertUserToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDateOfBirth(user.getDateOfBirth());

        // Compute age
        if (user.getDateOfBirth() != null) {
            dto.setAge(Period.between(user.getDateOfBirth(), LocalDate.now()).getYears());
        } else {
            dto.setAge(0);
        }

        return dto;
    }

    /**
     * Internal method to authenticate username and password.
     */
    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
