package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.dtos.AuthRequest;
import com.pungu.store.auth_service.dtos.UserRegistrationRequest;
import com.pungu.store.auth_service.entities.Users;
import com.pungu.store.auth_service.repository.UserRepository;
import com.pungu.store.auth_service.security.JWTTokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class handling user-related operations.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JWTTokenHelper jwtTokenHelper;
//    @Autowired
//    private final AuthRequest authRequest;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Optional<Users> getUserById(Long userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public Optional<Users> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Users registerUser(UserRegistrationRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }

        // Create new user entity
        Users users = new Users();
        users.setUsername(request.getUsername());
        users.setFirstName(request.getFirstName());
        users.setLastName(request.getLastName());
        users.setMiddleName(request.getMiddleName());
        users.setEmail(request.getEmail());
        users.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        users.setRole("USER");
        users.setDateOfBirth(request.getDateOfBirth());

        // Save user
        return userRepository.save(users);
    }

    @Override
    public String loginUser(AuthRequest request) {
        this.authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        return this.jwtTokenHelper.generateToken(userDetails);
    }

    @Override
    public List<UserRegistrationRequest> getAllUsers() {
        List<Users> list = userRepository.findAll();
        return list.stream().map(this::convertUserIntoUserResponseRequest).collect(Collectors.toList());
    }

    @Override
    public Users updateRole(long userId, String role) {
        Users users = this.userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
        users.setRole(role);
        return this.userRepository.save(users);
    }


    public UserRegistrationRequest convertUserIntoUserResponseRequest(Users users) {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();

        userRegistrationRequest.setFirstName(users.getFirstName());
        userRegistrationRequest.setMiddleName(users.getMiddleName());
        userRegistrationRequest.setLastName(users.getLastName());
        userRegistrationRequest.setUsername(users.getUsername());
        userRegistrationRequest.setEmail(users.getEmail());
        userRegistrationRequest.setDateOfBirth(users.getDateOfBirth());
        return userRegistrationRequest;
    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }


}