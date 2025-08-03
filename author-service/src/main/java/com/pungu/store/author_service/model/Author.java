package com.pungu.store.author_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorId;
    @Column(nullable = false, unique = true, length = 100)
    private String fullName;
    @Column(nullable = false, unique = true, length = 100)
    private String penName;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private String profilePictureUrl;
    private String nationality;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
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
}
