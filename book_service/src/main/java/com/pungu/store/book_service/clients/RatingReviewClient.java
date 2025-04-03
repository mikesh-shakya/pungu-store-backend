package com.pungu.store.book_service.clients;

import com.pungu.store.book_service.dtos.RatingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "rating-service", url = "http://localhost:8084/api/ratings-reviews")
public interface RatingReviewClient {

    @GetMapping("/{bookId}")
    List<RatingResponse> getRatingsForBook(@PathVariable Long bookId);

}
