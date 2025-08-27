package com.pungu.store.rating_service.clients;
import com.pungu.store.rating_service.dtos.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long userId) {
        log.warn("Fallback triggered: Unable to fetch user from user ID: '{}'", userId);
        // Returning null explicitly so the caller can distinguish fallback behavior
        return null;
    }
}
