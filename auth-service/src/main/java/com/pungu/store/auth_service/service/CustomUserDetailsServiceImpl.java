package com.pungu.store.auth_service.service;

import com.pungu.store.auth_service.entities.User;
import com.pungu.store.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to load user-specific data during authentication.
 * This class is used by Spring Security for user lookup during login.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user by username from the database.
     *
     * @param email the email identifying the user whose data is required.
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole())) // assuming 'role' is a String like "USER"
                .build();
    }
}
