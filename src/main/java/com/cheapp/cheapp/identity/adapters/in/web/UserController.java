package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserResponse;
import com.cheapp.cheapp.identity.adapters.in.web.dto.UserResponse;
import com.cheapp.cheapp.identity.application.port.in.CreateUserUseCase;
import com.cheapp.cheapp.identity.application.port.in.GetUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        var user = getUserUseCase.getById(id);
        return ResponseEntity.ok(new UserResponse(user.id(), user.email(), user.enabled(), user.roles()));
    }
}
