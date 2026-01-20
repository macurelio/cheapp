package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.ConflictException;
import com.cheapp.cheapp.identity.application.port.out.EventPublisherPort;
import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.event.DomainEvent;
import com.cheapp.cheapp.identity.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateUserServiceTest {

    @Test
    void creaUsuario_publicaEvento_y_devuelveId() {
        var repo = new InMemoryUserRepo();
        var encoder = new FakePasswordEncoder();
        var events = new CapturingEventPublisher();

        var service = new com.cheapp.cheapp.identity.application.service.CreateUserService(repo, encoder, events);

        var result = service.create(new com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase.Command(
                "Test@Cheapp.com",
                "password123"
        ));

        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("test@cheapp.com");
        assertThat(repo.findById(result.id())).isPresent();
        assertThat(events.events).hasSize(1);
        assertThat(events.events.getFirst().getClass().getSimpleName()).isEqualTo("UserCreatedEvent");
    }

    @Test
    void crearUsuario_conEmailExistente_lanzaConflict() {
        var repo = new InMemoryUserRepo();
        var encoder = new FakePasswordEncoder();
        var events = new CapturingEventPublisher();

        var service = new com.cheapp.cheapp.identity.application.service.CreateUserService(repo, encoder, events);

        service.create(new com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase.Command(
                "test@cheapp.com",
                "password123"
        ));

        assertThatThrownBy(() -> service.create(new com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase.Command(
                "test@cheapp.com",
                "password123"
        ))).isInstanceOf(ConflictException.class);
    }

    private static final class InMemoryUserRepo implements UserRepositoryPort {

        private long seq = 0;
        private final java.util.Map<Long, User> byId = new java.util.HashMap<>();
        private final java.util.Map<String, Long> idByEmail = new java.util.HashMap<>();

        @Override
        public User save(User user) {
            if (user.id() == null) {
                var id = ++seq;
                var saved = user.withId(id);
                byId.put(id, saved);
                idByEmail.put(saved.email(), id);
                return saved;
            }
            byId.put(user.id(), user);
            idByEmail.put(user.email(), user.id());
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            var id = idByEmail.get(email);
            return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
        }

        @Override
        public boolean existsByEmail(String email) {
            return idByEmail.containsKey(email);
        }
    }

    private static final class FakePasswordEncoder implements PasswordEncoderPort {
        @Override
        public String encode(String rawPassword) {
            return "ENC(" + rawPassword + ")";
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
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
