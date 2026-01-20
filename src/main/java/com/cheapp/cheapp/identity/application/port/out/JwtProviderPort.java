package com.cheapp.cheapp.identity.application.port.out;

import java.util.Set;

public interface JwtProviderPort {

    record Token(String value, long expiresInSeconds) {
    }

    Token createToken(Long userId, String email, Set<String> roles);

    DecodedToken decodeAndValidate(String token);

    record DecodedToken(Long userId, String email, Set<String> roles) {
    }
}
