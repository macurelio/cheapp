package com.cheapp.cheapp.identity.adapters.in.web.dto;

import java.util.Set;

public record RoleResponse(Long id, String name, Set<String> permissions) {
}
