package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.UnauthorizedException;
import com.cheapp.cheapp.identity.application.port.out.JwtProviderPort;
import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;
import com.cheapp.cheapp.identity.domain.model.Role;
import com.cheapp.cheapp.identity.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticateUserServiceTest {

    @Test
    void autenticaCredencialesValidas_y_devuelveToken() {
        var repo = new StaticUserRepo(new User(
                1L,
                "test@cheapp.com",
                "ENC(password123)",
                true,
                Set.of(new Role(10L, "USER", Set.of())),
                Instant.now()
        ));
        var encoder = new FakePasswordEncoder();
        var jwt = new FakeJwtProvider();

        var service = new AuthenticateUserService(repo, encoder, jwt);

        var result = service.authenticate(new com.cheapp.cheapp.identity.application.port.in.AuthenticateUserUseCase.Command(
                "test@cheapp.com",
                "password123"
        ));

        assertThat(result.accessToken()).isEqualTo("token-1");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.roles()).containsExactly("USER");
    }

    @Test
    void credencialesInvalidas_lanzaUnauthorized() {
        var repo = new StaticUserRepo(new User(
                1L,
                "test@cheapp.com",
                "ENC(password123)",
                true,
                Set.of(new Role(10L, "USER", Set.of())),
                Instant.now()
        ));
        var encoder = new FakePasswordEncoder();
        var jwt = new FakeJwtProvider();

        var service = new AuthenticateUserService(repo, encoder, jwt);

        assertThatThrownBy(() -> service.authenticate(new com.cheapp.cheapp.identity.application.port.in.AuthenticateUserUseCase.Command(
                "test@cheapp.com",
                "wrong"
        ))).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void usuarioNoExiste_lanzaUnauthorized() {
        var repo = new StaticUserRepo(null);
        var encoder = new FakePasswordEncoder();
        var jwt = new FakeJwtProvider();

        var service = new AuthenticateUserService(repo, encoder, jwt);

        assertThatThrownBy(() -> service.authenticate(new com.cheapp.cheapp.identity.application.port.in.AuthenticateUserUseCase.Command(
                "missing@cheapp.com",
                "password123"
        ))).isInstanceOf(UnauthorizedException.class);
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
            if (user == null) return Optional.empty();
            return user.email().equals(email) ? Optional.of(user) : Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            if (user == null) return false;
            return user.email().equals(email);
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

    private static final class FakeJwtProvider implements JwtProviderPort {
        @Override
        public Token createToken(Long userId, String email, Set<String> roles) {
            return new Token("token-" + userId, 3600);
        }

        @Override
        public DecodedToken decodeAndValidate(String token) {
            throw new UnsupportedOperationException();
        }
    }
}
