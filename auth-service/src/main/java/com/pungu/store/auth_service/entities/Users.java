package com.pungu.store.auth_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;

/**
 * Entity class representing a User in the authentication system.
 */
@Entity
@Table(name = "users") // Table name in PostgreSQL
@Getter
@Setter
@NoArgsConstructor
public class Users implements UserDetails {

    /**
     * Unique identifier for each user (Primary Key).
     * Auto-generated using IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username; // Username for user authentication (must be unique).

    @Column(nullable = false, unique = true)
    private String email; // User's email address (must be unique).

    @Column(nullable = false)
    private String password; // Encrypted password for authentication.

    @Column(nullable = false)
    private String firstName; // The first name of the user.

    @Column
    private String middleName; // The middle name of the user.

    @Column(nullable = false)
    private String lastName; // The last name of the user.

    @Column
    private String role; // Role assigned to the user (e.g., ADMIN, USER, AUTHOR).

    @Column
    private LocalDate dateOfBirth; // Date of birth of the user.

    /**
     * Age is a transient field, meaning it won't be stored in the database.
     * It is dynamically calculated from the date of birth.
     */
    @Transient
    private int age;

    /**
     * Transient field to calculate age dynamically.
     */
    @Transient
    public int getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

