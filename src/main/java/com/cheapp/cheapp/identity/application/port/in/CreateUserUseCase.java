package com.cheapp.cheapp.identity.application.port.in;

public interface CreateUserUseCase {

    record Command(String email, String password) {
    }

    record Result(Long id, String email) {
    }

    Result create(Command command);
}
