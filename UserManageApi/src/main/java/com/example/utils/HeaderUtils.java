package com.example.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Optional;

public class HeaderUtils {
    public static final String REQUIRE_AUTHORIZATION_ROLE_KEY = "X-Require-Authorization-Role";

    private HeaderUtils() {
    }

    public static Optional<String> getAuthRole(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(REQUIRE_AUTHORIZATION_ROLE_KEY));
    }
}
