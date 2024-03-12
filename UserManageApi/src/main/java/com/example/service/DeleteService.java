package com.example.service;

import reactor.core.publisher.Mono;

public interface DeleteService {
    Mono<Void> deleteSiteUser(String id);
}
