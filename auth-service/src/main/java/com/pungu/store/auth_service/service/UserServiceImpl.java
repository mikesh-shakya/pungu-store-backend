package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.*;
import com.pungu.store.auth_service.entities.Role;
import com.pungu.store.auth_service.entities.User;
import com.pungu.store.auth_service.exceptions.DuplicateUserException;
import com.pungu.store.auth_service.exceptions.InvalidLoginRequest;
import com.pungu.store.auth_service.exceptions.UserNotFoundException;
import com.pungu.store.auth_service.repository.UserRepository;
import com.pungu.store.auth_service.security.JWTTokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    private final UserMapper userMapper;

    /**
     * Fetch user by ID and convert to DTO.
     */
    @Override
    public Optional<UserResponseDTO> getUserById(Long userId) {
        return userRepository.findByUserId(userId)
                .map(userMapper::convertUserToUserResponseDTO);
    }

    /**
     * Register a new user.
     */
    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("User with this email already exists.");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(request.getProfilePictureUrl())
                .nationality(request.getNationality())
                .dateOfBirth(request.getDateOfBirth())
                .role(Role.USER)
                .build();

        return userMapper.convertUserToUserResponseDTO(userRepository.save(user));
    }

    /**
     * Authenticate and return JWT token.
     */
    @Override
    public AuthResponse loginUser(AuthRequest request) {
        authenticate(request.getEmail(), request.getPassword());

        // Load user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found with this email."));

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwtToken = jwtTokenHelper.generateToken(userDetails);

        return AuthResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .token(jwtToken)
                .build();
    }

    /**
     * Update the role of a user.
     */
    @Override
    public UserResponseDTO updateRole(long userId, String role) {
        Role roleEnum = Role.fromString(role);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user found."));

        user.setRole(roleEnum);
        return userMapper.convertUserToUserResponseDTO( userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("No such user found.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO request) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("No such user found.");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(request.getProfilePictureUrl())
                .nationality(request.getNationality())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        return userMapper.convertUserToUserResponseDTO(userRepository.save(user));
    }

    /**
     * Internal method to authenticate username and password.
     */
    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            throw new InvalidLoginRequest("Invalid email or password");
        }
    }
}
