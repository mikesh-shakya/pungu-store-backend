package com.pungu.store.auth_service.repository;

import com.pungu.store.auth_service.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository interface for managing User entity operations.
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username); // Find a user by their username

    Optional<Users> findByEmail(String email); // Find a user by their email

    Optional<Users> findByUserId(Long userId); // Find a user by their UserId

    boolean existsByEmail(String email); // Check if a user with the given email already exists

    boolean existsByUsername(String username); // Check if a user with the given username already exists

}

