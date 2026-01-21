package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.RoleResponse;
import com.cheapp.cheapp.identity.application.port.in.ListRolesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final ListRolesUseCase listRolesUseCase;

    public RoleController(ListRolesUseCase listRolesUseCase) {
        this.listRolesUseCase = listRolesUseCase;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        var roles = listRolesUseCase.list().stream()
                .map(r -> new RoleResponse(
                        r.id(),
                        r.name(),
                        r.permissions().stream().map(p -> p.name()).collect(java.util.stream.Collectors.toUnmodifiableSet())
                ))
                .toList();
        return ResponseEntity.ok(roles);
    }
}
