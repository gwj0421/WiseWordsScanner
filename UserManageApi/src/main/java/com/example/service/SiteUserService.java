package com.example.service;

import com.example.dao.SiteUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SiteUserService {
    Flux<SiteUser> getAllUser();
    Mono<SiteUser> getUserById(String id);
    Flux<String> getUserIdsByIds(List<String> ids);
    Mono<SiteUser> getUserByUserId(String userId);
    Mono<SiteUser> createSiteUser(SiteUser user);
    Mono<Void> deleteUser(String id);
}
