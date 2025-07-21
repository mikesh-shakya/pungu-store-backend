package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public Rating addRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getRatingsByBook(Long bookId) {
        return ratingRepository.findByBookId(bookId);
    }

    @Override
    public List<Rating> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId);
    }

    @Override
    public double getAverageRating(Long bookId) {
        List<Rating> ratings = ratingRepository.findByBookId(bookId);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
    }
}
