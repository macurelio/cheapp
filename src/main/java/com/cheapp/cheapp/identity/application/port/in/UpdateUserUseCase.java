package com.cheapp.cheapp.identity.application.port.in;

public interface UpdateUserUseCase {

    record Command(Long id, String email, Boolean enabled) {
    }

    void update(Command command);
}
