package com.cheapp.cheapp.identity.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(@NotBlank String roleName) {
}
