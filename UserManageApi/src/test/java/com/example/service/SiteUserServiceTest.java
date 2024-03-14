package com.example.service;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.SiteUser;
import com.example.repository.SiteUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class})
@Slf4j
class SiteUserServiceTest {
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private SiteUserService userService;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        userRepository.deleteAll().block();
    }

    @Test
    void Should_returnSiteUser_When_givenUserInfo() {
        // given
        SiteUser newUser = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");

        // when
        Mono<SiteUser> createdUser = userService.createSiteUser(newUser);
        Mono<SiteUser> getUser = userService.getUserByUserId("testUserId");

        // then
        StepVerifier.create(createdUser)
                .expectNextMatches(retrievedUser -> retrievedUser.getId() != null)
                .verifyComplete();

        StepVerifier.create(getUser)
                .expectNextMatches(retrievedUser -> retrievedUser.getId() != null && retrievedUser.getUserId().equals("testUserId"))
                .verifyComplete();
    }
}