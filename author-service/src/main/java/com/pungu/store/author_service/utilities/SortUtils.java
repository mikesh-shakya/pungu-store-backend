package com.pungu.store.author_service.utilities;

import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortUtils {

    /**
     * Parse a client provided sort string into a Spring Sort.
     * Examples supported:
     *  - "name,createdDate"
     *  - "-name,createdDate"      (dash prefix means DESC)
     *  - "name:asc,createdDate:desc"
     *
     * @param sortBy the raw client string (maybe null/blank)
     * @return Sort (possibly unsorted if nothing valid)
     */
    public static Sort parseSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = Stream.of(sortBy.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> {
                    // Accept formats: "-field", "field", "field:asc", "field:desc"
                    Sort.Direction dir = Sort.Direction.ASC;
                    String fieldToken = token;

                    if (token.startsWith("-")) {
                        dir = Sort.Direction.DESC;
                        fieldToken = token.substring(1).trim();
                    } else if (token.contains(":")) {
                        String[] parts = token.split(":", 2);
                        fieldToken = parts[0].trim();
                        String dirStr = parts[1].trim().toLowerCase(Locale.ROOT);
                        if ("desc".equals(dirStr) || "d".equals(dirStr)) dir = Sort.Direction.DESC;
                    }

                    return new Sort.Order(dir, fieldToken);
                })
//                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }
}
