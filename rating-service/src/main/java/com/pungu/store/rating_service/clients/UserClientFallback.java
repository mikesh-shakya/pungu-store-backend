package com.pungu.store.rating_service.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public String getUserNameById(Long userId) {
        log.warn("Fallback triggered: Unable to fetch user from user ID: '{}'", userId);
        return null;
    }
}
