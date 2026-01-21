package com.cheapp.cheapp.identity.application.port.in;

import com.cheapp.cheapp.identity.domain.model.Permission;

import java.util.List;

public interface ListPermissionsUseCase {
    List<Permission> list();
}
