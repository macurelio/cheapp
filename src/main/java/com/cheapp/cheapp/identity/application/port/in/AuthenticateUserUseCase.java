package com.cheapp.cheapp.identity.application.port.in;

import java.util.Set;

public interface AuthenticateUserUseCase {

    record Command(String email, String password) {
    }

    record Result(String accessToken,
                  String tokenType,
                  long expiresInSeconds,
                  Long userId,
                  String email,
                  Set<String> roles,
                  Set<String> permissions) {
    }

    Result authenticate(Command command);
}
