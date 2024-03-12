package com.example.security;

import com.example.dto.SignUpForm;
import com.example.repository.SiteUserRepository;
import com.example.service.LoginService;
import com.example.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Slf4j
@Import({SecurityConfig.class,TokenSecurityContextRepository.class,TokenAuthenticationManager.class, JwtUtils.class})
class LoginServiceImplTest {
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private LoginService loginService;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        userRepository.deleteAll().block();
    }

    @Test
    void Should_signUp_When_givenUserIdIsNotExists() {
        // given
        SignUpForm signUpForm = new SignUpForm("testName123", "testUserId123", "testPassword123", "testPassword123", "testEmail@gamil.com");

        // when
        Mono<ResponseEntity<Void>> signUp = loginService.signUp(signUpForm);

        // then
        StepVerifier.create(signUp)
                .expectNextMatches(response -> response.getStatusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void Should_pass_When_givenUserIdIsExists() {
        // given
        SignUpForm signUpForm = new SignUpForm("testName123", "testUserId123", "testPassword123", "testPassword123", "testEmail@gamil.com");

        // when
        Mono<ResponseEntity<Void>> signUp = loginService.signUp(signUpForm);

        // then
        StepVerifier.create(signUp)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
        StepVerifier.create(signUp)
                .expectNextMatches(responseEntity -> responseEntity.getStatusCode().equals(HttpStatus.CONFLICT))
                .verifyComplete();
    }
}