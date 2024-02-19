package com.example.services;

import com.example.dao.SiteUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SiteUserService {
    Flux<SiteUser> getAllUser();
    Mono<SiteUser> getUserById(String id);
    Mono<SiteUser> getUserByUserId(String userId);
    Mono<SiteUser> createSiteUser(SiteUser user);
    Mono<Void> deleteUser(String id);
}
