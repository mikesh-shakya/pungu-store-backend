package com.pungu.store.rating_service.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @Column(nullable = false)
    private Long bookId;  // Foreign Key (Book)

    @Column(nullable = false)
    private Long userId;  // Foreign Key (User)

    @Column(nullable = false)
    private int rating; // 1 to 5 stars

    @Column(columnDefinition = "TEXT", length = 2000)
    private String review; // Optional review text

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;
}
