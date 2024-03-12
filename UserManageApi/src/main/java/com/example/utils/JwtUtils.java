package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Slf4j
public class JwtUtils {
    public static final String ROLE_CLAIM_KEY = "role";
    public static final String PRINCIPAL_CLAIM_KEY = "principal";

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiration-time}")
    private long expirationTime;
    @Value("${jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    public String generate(String id) {
        Instant expiredAt = Instant.now().plusSeconds(expirationTime);
        return JWT.create()
                .withIssuer(issuer)
                .withClaim(PRINCIPAL_CLAIM_KEY, id)
                .withClaim(ROLE_CLAIM_KEY, "ROLE_USER")
                .withExpiresAt(expiredAt)
                .sign(algorithm());
    }

    public String generateRefreshToken() {
        Instant expiredAt = Instant.now().plusSeconds(refreshExpirationTime);
        return JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(expiredAt)
                .sign(algorithm());
    }
    private Algorithm algorithm() {
        return Algorithm.HMAC512(secretKey);
    }

    public boolean isValid(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm()).withIssuer(issuer).build();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Mono<DecodedJWT> verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm()).withIssuer(issuer).build();
            return Mono.just(verifier.verify(token));
        } catch (Exception e) {
            return Mono.empty();
        }
    }
}
