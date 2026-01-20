package com.cheapp.cheapp.identity.application.port.in;

public interface AssignRoleUseCase {

    record Command(Long userId, String roleName) {
    }

    void assign(Command command);
}
