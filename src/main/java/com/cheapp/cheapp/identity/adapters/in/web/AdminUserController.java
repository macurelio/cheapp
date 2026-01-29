package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.AdminCreateUserRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserResponse;
import com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_SUPER_USER = "ROLE_SUPER_USER";

    private final CreateUserUseCase createUserUseCase;

    public AdminUserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody AdminCreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(
                request.email(),
                request.password(),
                request.roles()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }

    @PostMapping("/admin")
    public ResponseEntity<CreateUserResponse> createAdmin(@Valid @RequestBody CreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(
                request.email(),
                request.password(),
                Set.of(ROLE_ADMIN)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }

    @PostMapping("/super-user")
    public ResponseEntity<CreateUserResponse> createSuperUser(@Valid @RequestBody CreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(
                request.email(),
                request.password(),
                Set.of(ROLE_SUPER_USER)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }
}
