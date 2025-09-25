package com.pungu.store.author_service.dto;

import java.util.List;

public record SliceResponse<T>(
    List<T> items,
    int currentPageNumber,
    int numberOfElements,
    int pageSize,
    boolean hasMore
) {}
