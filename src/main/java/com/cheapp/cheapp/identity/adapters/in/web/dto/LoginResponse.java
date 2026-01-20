package com.cheapp.cheapp.identity.adapters.in.web.dto;

import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String email,
        Set<String> roles
) {
}
