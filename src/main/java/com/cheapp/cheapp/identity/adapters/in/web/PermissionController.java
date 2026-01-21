package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.PermissionResponse;
import com.cheapp.cheapp.identity.application.port.in.ListPermissionsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final ListPermissionsUseCase listPermissionsUseCase;

    public PermissionController(ListPermissionsUseCase listPermissionsUseCase) {
        this.listPermissionsUseCase = listPermissionsUseCase;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        var permissions = listPermissionsUseCase.list().stream()
                .map(p -> new PermissionResponse(p.id(), p.name()))
                .toList();
        return ResponseEntity.ok(permissions);
    }
}
