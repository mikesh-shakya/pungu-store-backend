package com.pungu.store.author_service;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private String name;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private String nationality;
    private LocalDate dateOfBirth;
    private LocalDate createdAt;
    private LocalDate lastUpdatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDate.now();
    }
}
