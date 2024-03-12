package com.example.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Slf4j
class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void test() {
        String token = jwtUtils.generate("gwj0421");
        Mono<DecodedJWT> verify = jwtUtils.verify(token);
        StepVerifier.create(verify)
                .expectNextMatches(decodedJWT -> decodedJWT != null)
                .verifyComplete();

    }

}