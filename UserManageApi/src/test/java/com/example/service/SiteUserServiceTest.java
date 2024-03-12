package com.example.service;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.SiteUser;
import com.example.repository.SiteUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class})
@Slf4j
class SiteUserServiceTest {
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private SiteUserService userService;

//    @BeforeEach
//    @AfterEach
//    void setUpAndTearDown() {
//        userRepository.deleteAll().block();
//    }

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

    @Test
    void should_returnUserIds_When_givenIds() {
        // given
        List<String> ids = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        for (int i = 100; i > 0; i--) {
            SiteUser user = userService.createSiteUser(new SiteUser("testName" + i, "testUserId" + i, "testPassword" + i, "testEmail@gamil.com")).block();
            ids.add(user.getId());
            userIds.add("testUserId"+i);
        }

        // when
        Flux<String> getUserIds = userService.getUserIdsByIds(ids);

        // then
        StepVerifier.create(getUserIds)
                .expectNextSequence(userIds)
                .verifyComplete();
    }

    @Test
    void test() {
        String authorId = "65e5b44e9c808f173edca6f3";
        String[] input = new String[10];
        Arrays.fill(input, authorId);
        Flux<String> allById = Flux.fromIterable(Arrays.asList(input))
                .flatMap(userRepository::findSiteUserById)
                .map(SiteUser::getUserId);
        log.info("gwj : " + allById.collectList().block());
//        StepVerifier.create(allById)
//                .expectNextCount(10)
//                .verifyComplete();

    }
}