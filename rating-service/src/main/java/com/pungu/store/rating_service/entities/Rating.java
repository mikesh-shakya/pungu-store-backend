package com.pungu.store.rating_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "ratings",
        indexes = {
                @Index(name = "idx_book_id", columnList = "bookId"),
                @Index(name = "idx_user_id", columnList = "userId")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bookId", "userId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Rating {

    /**
     * Unique identifier for each rating entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    /**
     * ID of the book being rated.
     * Acts as a foreign key to the Book entity.
     */
    @Column(nullable = false)
    private Long bookId;

    /**
     * ID of the user who submitted the rating.
     * Acts as a foreign key to the User entity.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Star rating value (from 1 to 5).
     */
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;

    /**
     * Optional review message submitted by the user.
     */
    @Column(columnDefinition = "TEXT", length = 2000)
    private String review;

    /**
     * Timestamp when the rating was created.
     * Automatically managed by Spring JPA auditing.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;
}
