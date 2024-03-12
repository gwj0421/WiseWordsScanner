package com.example.security;

import com.example.dao.SiteUser;
import com.example.dto.LoginForm;
import com.example.repository.SiteUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class LoginControllerTest {
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        userRepository.deleteAll().block();
    }

    @Test
    void Should_successLogin_When_enteringCorrectUserIdAndPassword() {
        // given
        LoginForm loginForm = new LoginForm("testUserId123", "testPassword123");

        // when
        userRepository.save(new SiteUser("testName123", "testUserId123", passwordEncoder.encode("testPassword123"), "testEmail@gmail.com")).block();

        // then
        webTestClient.post()
                .uri("/user/login")
                .bodyValue(loginForm)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void Should_failLogin_When_enteringIncorrectUserIdAndPassword() {
        // given
        LoginForm loginFormByWrongUserId = new LoginForm("wrongUserId", "testPassword123");
        LoginForm loginFormByWrongPassword = new LoginForm("testUserId123", "wrongPassword");

        // when
        userRepository.save(new SiteUser("testName123", "testUserId123", passwordEncoder.encode("testPassword1234"), "testEmail@gmail.com")).block();

        // then
        webTestClient.post()
                .uri("/user/login")
                .bodyValue(loginFormByWrongUserId)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().doesNotExist(HttpHeaders.AUTHORIZATION);
        webTestClient.post()
                .uri("/user/login")
                .bodyValue(loginFormByWrongPassword)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().doesNotExist(HttpHeaders.AUTHORIZATION);
    }
}