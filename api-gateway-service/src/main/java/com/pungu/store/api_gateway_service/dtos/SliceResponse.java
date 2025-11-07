package com.pungu.store.api_gateway_service.dtos;

import java.util.List;

public record SliceResponse<T>(List<T> items,
                               Integer currentPageNumber,
                               Integer numberOfElements,
                               Integer pageSize,
                               Boolean hasMore) {
}
