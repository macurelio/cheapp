package com.cheapp.cheapp.identity.config;

import com.cheapp.cheapp.identity.adapters.out.event.LoggingEventPublisherAdapter;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.RoleRepositoryAdapter;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.UserRepositoryAdapter;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.RoleSpringDataRepository;
import com.cheapp.cheapp.identity.adapters.out.persistence.jpa.repository.UserSpringDataRepository;
import com.cheapp.cheapp.identity.adapters.out.security.BCryptPasswordEncoderAdapter;
import com.cheapp.cheapp.identity.adapters.out.security.jwt.JjwtJwtProviderAdapter;
import com.cheapp.cheapp.identity.adapters.out.security.jwt.JwtProperties;
import com.cheapp.cheapp.identity.application.port.in.*;
import com.cheapp.cheapp.identity.application.port.out.*;
import com.cheapp.cheapp.identity.application.service.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class IdentityBeansConfig {

    // Ports (out)
    @Bean
    UserRepositoryPort userRepositoryPort(UserSpringDataRepository userRepo, RoleSpringDataRepository roleRepo) {
        return new UserRepositoryAdapter(userRepo, roleRepo);
    }

    @Bean
    RoleRepositoryPort roleRepositoryPort(RoleSpringDataRepository roleRepo) {
        return new RoleRepositoryAdapter(roleRepo);
    }

    @Bean
    PasswordEncoderPort passwordEncoderPort(BCryptPasswordEncoder encoder) {
        return new BCryptPasswordEncoderAdapter(encoder);
    }

    @Bean
    JwtProviderPort jwtProviderPort(JwtProperties props) {
        return new JjwtJwtProviderAdapter(props);
    }

    @Bean
    EventPublisherPort eventPublisherPort() {
        return new LoggingEventPublisherAdapter();
    }

    // Use cases (in)
    @Bean
    CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort,
                                        PasswordEncoderPort passwordEncoderPort,
                                        EventPublisherPort eventPublisherPort) {
        return new CreateUserService(userRepositoryPort, passwordEncoderPort, eventPublisherPort);
    }

    @Bean
    AssignRoleUseCase assignRoleUseCase(UserRepositoryPort userRepositoryPort,
                                        RoleRepositoryPort roleRepositoryPort,
                                        EventPublisherPort eventPublisherPort) {
        return new AssignRoleService(userRepositoryPort, roleRepositoryPort, eventPublisherPort);
    }

    @Bean
    AuthenticateUserUseCase authenticateUserUseCase(UserRepositoryPort userRepositoryPort,
                                                    PasswordEncoderPort passwordEncoderPort,
                                                    JwtProviderPort jwtProviderPort) {
        return new AuthenticateUserService(userRepositoryPort, passwordEncoderPort, jwtProviderPort);
    }

    @Bean
    GetUserUseCase getUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserService(userRepositoryPort);
    }

    @Bean
    UpdateUserUseCase updateUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new UpdateUserService(userRepositoryPort);
    }

    @Bean
    UnassignRoleUseCase unassignRoleUseCase(UserRepositoryPort userRepositoryPort,
                                            RoleRepositoryPort roleRepositoryPort) {
        return new UnassignRoleService(userRepositoryPort, roleRepositoryPort);
    }
}
