package com.pungu.store.auth_service.repository;

import com.pungu.store.auth_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their email.
     *
     * @param email the email to search
     * @return Optional containing the User, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their userId.
     *
     * @param userId the user ID
     * @return Optional containing the User, if found
     */
    Optional<User> findByUserId(Long userId);

    /**
     * Check if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}
