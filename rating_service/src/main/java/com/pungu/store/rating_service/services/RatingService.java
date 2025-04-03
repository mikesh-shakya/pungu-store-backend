package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.entities.Rating;
import java.util.List;

public interface RatingService {

    Rating addRating(Rating rating);

    List<Rating> getRatingsByBook(Long bookId);

    List<Rating> getRatingsByUser(Long userId);

    double getAverageRating(Long bookId);
}
