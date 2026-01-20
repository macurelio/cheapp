package com.cheapp.cheapp.identity.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email String email,
        Boolean enabled
) {
}
