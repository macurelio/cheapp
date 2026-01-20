package com.cheapp.cheapp.identity.application.service;

import com.cheapp.cheapp.identity.application.exception.NotFoundException;
import com.cheapp.cheapp.identity.application.port.in.GetUserUseCase;
import com.cheapp.cheapp.identity.application.port.out.UserRepositoryPort;

import java.util.stream.Collectors;

public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepository;

    public GetUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result getById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        var roles = user.roles().stream().map(r -> r.name()).collect(Collectors.toUnmodifiableSet());
        return new Result(user.id(), user.email(), user.enabled(), roles);
    }
}
