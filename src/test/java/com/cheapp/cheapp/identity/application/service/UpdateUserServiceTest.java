package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
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

class UpdateUserServiceTest {

    private static final Instant FIXED_NOW = Instant.parse("2020-01-01T00:00:00Z");

    @Test
    void actualizaEmail_normaliza_y_persiste() {
        var repo = new InMemoryUserRepo();
        repo.save(new User(1L, "old@cheapp.com", "hash", true, Set.of(new Role(1L, "USER", Set.of())), FIXED_NOW));

        var service = new UpdateUserService(repo);
        service.update(new com.cheapp.cheapp.identity.application.port.in.UpdateUserUseCase.Command(1L, "  New@Cheapp.com ", null));

        var updated = repo.findById(1L).orElseThrow();
        assertThat(updated.email()).isEqualTo("new@cheapp.com");
        assertThat(updated.enabled()).isTrue();
    }

    @Test
    void actualizaEnabled_sinCambiarEmail() {
        var repo = new InMemoryUserRepo();
        repo.save(new User(1L, "user@cheapp.com", "hash", true, Set.of(), FIXED_NOW));

        var service = new UpdateUserService(repo);
        service.update(new com.cheapp.cheapp.identity.application.port.in.UpdateUserUseCase.Command(1L, null, false));

        var updated = repo.findById(1L).orElseThrow();
        assertThat(updated.email()).isEqualTo("user@cheapp.com");
        assertThat(updated.enabled()).isFalse();
    }

    @Test
    void usuarioNoExiste_lanzaNotFound() {
        var repo = new InMemoryUserRepo();
        var service = new UpdateUserService(repo);

        assertThatThrownBy(() -> service.update(new com.cheapp.cheapp.identity.application.port.in.UpdateUserUseCase.Command(99L, "a@b.com", true)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    void emailYaRegistrado_lanzaConflict() {
        var repo = new InMemoryUserRepo();
        repo.save(new User(1L, "one@cheapp.com", "hash", true, Set.of(), FIXED_NOW));
        repo.save(new User(2L, "two@cheapp.com", "hash", true, Set.of(), FIXED_NOW));

        var service = new UpdateUserService(repo);

        assertThatThrownBy(() -> service.update(new com.cheapp.cheapp.identity.application.port.in.UpdateUserUseCase.Command(1L, "two@cheapp.com", null)))
                .isInstanceOf(ConflictException.class);
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
