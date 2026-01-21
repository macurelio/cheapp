package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Permission;
import com.cheapp.cheapp.identity.domain.model.Role;
import com.cheapp.cheapp.identity.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetUserPermissionsServiceTest {

    @Test
    void agregaPermisosDeTodosLosRoles_y_deduplica() {
        var repo = new InMemoryUserRepo();
        repo.save(new User(1L, "test@cheapp.com", "hash", true,
                Set.of(
                        new Role(10L, "USER", Set.of(new Permission(1L, "READ"), new Permission(2L, "WRITE"))),
                        new Role(11L, "ADMIN", Set.of(new Permission(2L, "WRITE"), new Permission(3L, "DELETE")))
                ),
                Instant.now()));

        var service = new GetUserPermissionsService(repo);
        var perms = service.getEffectivePermissions(1L);

        assertThat(perms).containsExactlyInAnyOrder("READ", "WRITE", "DELETE");
    }

    @Test
    void usuarioNoExiste_lanzaNotFound() {
        var repo = new InMemoryUserRepo();
        var service = new GetUserPermissionsService(repo);

        assertThatThrownBy(() -> service.getEffectivePermissions(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    private static final class InMemoryUserRepo implements UserRepositoryPort {
        private final Map<Long, User> byId = new HashMap<>();

        @Override
        public User save(User user) {
            byId.put(user.id(), user);
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return byId.values().stream().filter(u -> u.email().equals(email)).findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return byId.values().stream().anyMatch(u -> u.email().equals(email));
        }
    }
}
