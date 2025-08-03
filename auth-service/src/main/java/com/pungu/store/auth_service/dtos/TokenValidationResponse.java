package com.pungu.store.auth_service.dtos;

import java.util.List;

public record TokenValidationResponse(String username, List<String> roles, boolean valid) {}
