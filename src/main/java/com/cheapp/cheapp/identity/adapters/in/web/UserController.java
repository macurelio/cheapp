package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.AssignRoleRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.CreateUserResponse;
import com.cheapp.cheapp.identity.adapters.in.web.dto.UpdateUserRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.UserResponse;
import com.cheapp.cheapp.identity.application.port.in.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final UnassignRoleUseCase unassignRoleUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase,
                          UpdateUserUseCase updateUserUseCase, AssignRoleUseCase assignRoleUseCase,
                          UnassignRoleUseCase unassignRoleUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.unassignRoleUseCase = unassignRoleUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var result = createUserUseCase.create(new CreateUserUseCase.Command(
                request.email(),
                request.password(),
                java.util.Set.of()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(result.id(), result.email()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        var user = getUserUseCase.getById(id);
        return ResponseEntity.ok(new UserResponse(user.id(), user.email(), user.enabled(), user.roles()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        updateUserUseCase.update(new UpdateUserUseCase.Command(id, request.email(), request.enabled()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable Long id, @Valid @RequestBody AssignRoleRequest request) {
        assignRoleUseCase.assign(new AssignRoleUseCase.Command(id, request.roleName()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> unassignRole(@PathVariable Long id, @PathVariable String roleName) {
        unassignRoleUseCase.unassign(new UnassignRoleUseCase.Command(id, roleName));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
