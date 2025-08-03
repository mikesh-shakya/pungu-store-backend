package com.pungu.store.book_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "authorId"})
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;
    private Long authorId; // Fetched from Author Service
    private String genre;
    private String language;
    private LocalDate publicationDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String ebookUrl;
    private String coverImageUrl;
    private boolean availableForReading;
    private boolean availableForDownload;
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
