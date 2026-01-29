package com.cheapp.cheapp.identity.adapters.in.web;

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

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final CreateUserUseCase createUserUseCase;

    public AdminUserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }
}
