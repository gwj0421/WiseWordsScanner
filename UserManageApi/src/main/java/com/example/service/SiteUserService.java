package com.example.service;

import com.example.dao.SiteUser;
import reactor.core.publisher.Mono;

public interface SiteUserService {
    Mono<SiteUser> getUserById(String id);
    Mono<SiteUser> getUserByUserId(String userId);
    Mono<SiteUser> createSiteUser(SiteUser user);
    Mono<Void> deleteUser(String id);
}
