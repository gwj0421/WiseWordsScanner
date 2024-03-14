package com.example.repository;

import com.example.dao.SiteUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SiteUserRepository extends ReactiveMongoRepository<SiteUser, String> {
    Mono<SiteUser> findSiteUserByUserId(String userId);
    Mono<SiteUser> findSiteUserById(String id);
    Mono<Boolean> existsSiteUserByUserId(String userId);
    Mono<Boolean> existsSiteUserById(String id);
}
