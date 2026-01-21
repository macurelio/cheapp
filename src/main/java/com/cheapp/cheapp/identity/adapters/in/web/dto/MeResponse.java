package com.cheapp.cheapp.identity.adapters.in.web.dto;

import java.util.Set;

public record MeResponse(Long userId, String email, Set<String> roles, Set<String> permissions) {
}
