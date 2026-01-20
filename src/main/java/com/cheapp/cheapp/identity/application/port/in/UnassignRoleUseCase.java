package com.cheapp.cheapp.identity.application.port.in;

public interface UnassignRoleUseCase {

    record Command(Long userId, String roleName) {
    }

    void unassign(Command command);
}
