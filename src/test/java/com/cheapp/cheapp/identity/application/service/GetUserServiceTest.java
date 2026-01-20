package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Role;
import com.cheapp.cheapp.identity.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetUserServiceTest {
    private static final Instant FIXED_NOW = Instant.parse("2020-01-01T00:00:00Z");

    @Test
    void devuelveUsuarioConRoles() {
        var repo = new StaticUserRepo(new User(
                1L,
                "test@cheapp.com",
                "hash",
                true,
                Set.of(new Role(10L, "USER", Set.of()), new Role(11L, "ADMIN", Set.of())),
                FIXED_NOW
        ));

        var service = new GetUserService(repo);
        var result = service.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("test@cheapp.com");
        assertThat(result.enabled()).isTrue();
        assertThat(result.roles()).containsExactlyInAnyOrder("USER", "ADMIN");
    }

    @Test
    void usuarioNoExiste_lanzaNotFound() {
        var repo = new StaticUserRepo(null);
        var service = new GetUserService(repo);

        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    private static final class StaticUserRepo implements UserRepositoryPort {
        private final User user;

        private StaticUserRepo(User user) {
            this.user = user;
        }

        @Override
        public User save(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(user);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(user);
        }

        @Override
        public boolean existsByEmail(String email) {
            return user != null && user.email().equals(email);
        }
    }
}
