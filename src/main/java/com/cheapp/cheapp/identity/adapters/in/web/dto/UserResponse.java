package com.cheapp.cheapp.identity.adapters.in.web.dto;

import java.util.Set;

public record UserResponse(Long id, String email, boolean enabled, Set<String> roles) {
}
