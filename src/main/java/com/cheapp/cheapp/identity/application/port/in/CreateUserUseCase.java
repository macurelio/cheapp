package com.cheapp.cheapp.identity.application.port.in;

import java.util.Set;

public interface CreateUserUseCase {

    record Command(String email, String password, Set<String> roleNames) {
    }

    record Result(Long id, String email) {
    }

    Result create(Command command);
}
