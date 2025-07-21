package com.pungu.store.auth_service.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

/**
 * DTO for capturing login request data.
 */
@Getter
@Setter
@Configuration
public class AuthRequest {

    private String username;
    private String password;
}
