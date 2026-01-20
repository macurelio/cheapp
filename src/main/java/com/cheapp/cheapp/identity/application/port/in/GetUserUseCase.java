package com.cheapp.cheapp.identity.application.port.in;

import java.util.Set;

public interface GetUserUseCase {

    record Result(Long id, String email, boolean enabled, Set<String> roles) {
    }

    Result getById(Long id);
}
