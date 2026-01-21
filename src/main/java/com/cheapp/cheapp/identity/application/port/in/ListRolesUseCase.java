package com.cheapp.cheapp.identity.application.port.in;

import com.cheapp.cheapp.identity.domain.model.Role;

import java.util.List;

public interface ListRolesUseCase {
    List<Role> list();
}
