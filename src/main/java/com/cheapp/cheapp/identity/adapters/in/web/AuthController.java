package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.LoginRequest;
import com.cheapp.cheapp.identity.adapters.in.web.dto.LoginResponse;
import com.cheapp.cheapp.identity.application.port.in.AuthenticateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authenticateUserUseCase.authenticate(new AuthenticateUserUseCase.Command(request.email(), request.password()));
        return ResponseEntity.ok(new LoginResponse(
                result.accessToken(),
                result.tokenType(),
                result.expiresInSeconds(),
                result.userId(),
                result.email(),
                result.roles(),
                result.permissions()
        ));
    }
}
