package com.pungu.store.rating_service.repositories;

import com.pungu.store.rating_service.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Get all ratings for a specific book
    List<Rating> findByBookId(Long bookId);

    // Get all ratings given by a specific user
    List<Rating> findByUserId(Long userId);
}
