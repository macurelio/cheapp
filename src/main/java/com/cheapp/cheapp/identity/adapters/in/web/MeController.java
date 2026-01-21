package com.cheapp.cheapp.identity.adapters.in.web;

import com.cheapp.cheapp.identity.adapters.in.web.dto.MeResponse;
import com.cheapp.cheapp.identity.application.port.out.JwtProviderPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class MeController {

    private final JwtProviderPort jwtProvider;

    public MeController(JwtProviderPort jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @GetMapping
    public ResponseEntity<MeResponse> me(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        var token = authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
        var decoded = jwtProvider.decodeAndValidate(token);
        return ResponseEntity.ok(new MeResponse(decoded.userId(), decoded.email(), decoded.roles(), decoded.permissions()));
    }
}
