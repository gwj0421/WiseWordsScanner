package com.example.repository;

import com.example.dao.SiteUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SiteUserRepository extends ReactiveMongoRepository<SiteUser, String> {
    Mono<SiteUser> findSiteUserByUserId(String userId);
}
