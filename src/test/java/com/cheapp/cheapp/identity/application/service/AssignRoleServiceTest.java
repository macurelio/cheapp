package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.application.port.out.RoleRepositoryPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.event.DomainEvent;
import com.cheapp.cheapp.identity.domain.model.Role;
import com.cheapp.cheapp.identity.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssignRoleServiceTest {

    @Test
    void asignaRolExistente_y_publicaEvento() {
        var userRepo = new InMemoryUserRepo();
        var roleRepo = new StaticRoleRepo(new Role(2L, "ADMIN", Set.of()));
        var events = new CapturingEventPublisher();

        var existing = new User(1L, "test@cheapp.com", "hash", true, Set.of(), Instant.now());
        userRepo.save(existing);

        var service = new AssignRoleService(userRepo, roleRepo, events);
        service.assign(new com.cheapp.cheapp.identity.application.port.in.AssignRoleUseCase.Command(1L, "ADMIN"));

        var updated = userRepo.findById(1L).orElseThrow();
        assertThat(updated.roles()).extracting(Role::name).contains("ADMIN");
        assertThat(events.events).hasSize(1);
        assertThat(events.events.getFirst().getClass().getSimpleName()).isEqualTo("RoleAssignedEvent");
    }

    @Test
    void usuarioNoExiste_lanzaNotFound() {
        var userRepo = new InMemoryUserRepo();
        var roleRepo = new StaticRoleRepo(new Role(2L, "ADMIN", Set.of()));
        var events = new CapturingEventPublisher();

        var service = new AssignRoleService(userRepo, roleRepo, events);

        assertThatThrownBy(() -> service.assign(new com.cheapp.cheapp.identity.application.port.in.AssignRoleUseCase.Command(99L, "ADMIN")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rolNoExiste_lanzaNotFound() {
        var userRepo = new InMemoryUserRepo();
        var roleRepo = new StaticRoleRepo(null);
        var events = new CapturingEventPublisher();

        userRepo.save(new User(1L, "test@cheapp.com", "hash", true, Set.of(), Instant.now()));

        var service = new AssignRoleService(userRepo, roleRepo, events);

        assertThatThrownBy(() -> service.assign(new com.cheapp.cheapp.identity.application.port.in.AssignRoleUseCase.Command(1L, "ADMIN")))
                .isInstanceOf(NotFoundException.class);
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

    private static final class StaticRoleRepo implements RoleRepositoryPort {
        private final Role role;

        private StaticRoleRepo(Role role) {
            this.role = role;
        }

        @Override
        public Optional<Role> findByName(String name) {
            if (role == null) return Optional.empty();
            return role.name().equals(name) ? Optional.of(role) : Optional.empty();
        }
    }

    private static final class CapturingEventPublisher implements EventPublisherPort {
        private final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            events.add(event);
        }
    }
}
