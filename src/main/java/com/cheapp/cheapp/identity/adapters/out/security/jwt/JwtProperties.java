package com.cheapp.cheapp.identity.adapters.out.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(String issuer, String secret, long ttlSeconds) {
}
