package com.pungu.store.rating_service.dtos;

import java.util.List;

public record SliceResponse<T>(
    List<T> items,
    int currentPageNumber,
    int numberOfElements,
    int pageSize,
    boolean hasMore
) {}
