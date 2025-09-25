package com.pungu.store.rating_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", fallback = UserClientFallback.class)
public interface UserClient {
    /**
     * Retrieves the name of a user given their ID.
     *
     * @param userId the unique ID of the user
     * @return the id and full name of the user
     */
    @GetMapping(value = "/api/users/username/{userId}", produces = MediaType.TEXT_PLAIN_VALUE)
    String getUserNameById(@PathVariable("userId") Long userId);
}
