package com.pungu.store.rating_service.clients;

import com.pungu.store.rating_service.dtos.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", fallback = UserClientFallback.class)
public interface UserClient {
    /**
     * Retrieves the name of a user given their ID.
     * Useful when displaying username using only the stored user ID.
     *
     * @param userId the unique ID of the user
     * @return the id and full name of the user
     */
    @GetMapping("/api/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);
}
