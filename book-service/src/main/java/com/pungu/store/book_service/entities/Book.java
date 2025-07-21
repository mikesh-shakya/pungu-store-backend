package com.pungu.store.book_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;
    private Long authorId; // Fetched from Author Service
    private String genre;
    private LocalDate publicationDate;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String fileUrl;
    private String coverImageUrl;
    private boolean availableForReading;
    private boolean availableForDownload;
}
