package com.pungu.store.auth_service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entity class representing a User in the authentication system.
 * Implements UserDetails for Spring Security integration.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDate dateOfBirth;
    private String profilePictureUrl;
    private String nationality;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }

    /**
     * Returns the authorities granted to the user.
     * Based on the role.
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Returns the username used to authenticate the user.
     */
    @Override
    @JsonIgnore
    public String getUsername() {
        return this.getEmail();
    }

    /**
     * Account is not expired.
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account is not locked.
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credentials are not expired.
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Account is enabled.
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}