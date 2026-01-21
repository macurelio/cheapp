package com.cheapp.cheapp.identity.application.port.in;

import java.util.Set;

public interface GetUserPermissionsUseCase {

    Set<String> getEffectivePermissions(Long userId);
}
