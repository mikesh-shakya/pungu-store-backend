package com.pungu.store.rating_service.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingRequest {

    @NotNull
    private Long bookId;
    private Long userId;

    @Min(1)
    @Max(5)
    private int rating;
    private String review;
}

