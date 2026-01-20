package com.cheapp.cheapp.identity.adapters.out.security;

import com.cheapp.cheapp.identity.application.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordEncoderAdapter(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
