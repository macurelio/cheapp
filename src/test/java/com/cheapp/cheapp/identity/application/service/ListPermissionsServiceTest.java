package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.port.out.PermissionRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Permission;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListPermissionsServiceTest {

    @Test
    void listaPermisos_deRepos() {
        var repo = new FakePermissionRepo(List.of(
                new Permission(1L, "READ"),
                new Permission(2L, "WRITE")
        ));

        var service = new ListPermissionsService(repo);
        assertThat(service.list()).extracting(Permission::name).containsExactly("READ", "WRITE");
    }

    private static final class FakePermissionRepo implements PermissionRepositoryPort {
        private final List<Permission> permissions;

        private FakePermissionRepo(List<Permission> permissions) {
            this.permissions = permissions;
        }

        @Override
        public List<Permission> findAll() {
            return permissions;
        }
    }
}
