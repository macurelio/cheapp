package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ListRolesServiceTest {

    @Test
    void listaRoles_deRepos() {
        var repo = new FakeRoleRepo(List.of(
                new Role(1L, "USER", Set.of()),
                new Role(2L, "ADMIN", Set.of())
        ));

        var service = new ListRolesService(repo);
        assertThat(service.list()).extracting(Role::name).containsExactly("USER", "ADMIN");
    }

    private static final class FakeRoleRepo implements RoleRepositoryPort {
        private final List<Role> roles;

        private FakeRoleRepo(List<Role> roles) {
            this.roles = roles;
        }

        @Override
        public Optional<Role> findByName(String name) {
            return roles.stream().filter(r -> r.name().equals(name)).findFirst();
        }

        @Override
        public List<Role> findAll() {
            return roles;
        }
    }
}
