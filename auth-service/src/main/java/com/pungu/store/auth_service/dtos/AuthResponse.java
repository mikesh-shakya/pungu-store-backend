package com.pungu.store.auth_service.dtos;

import com.pungu.store.auth_service.entities.Role;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;
    private String email;
    private Role role;
    private String token;
}
