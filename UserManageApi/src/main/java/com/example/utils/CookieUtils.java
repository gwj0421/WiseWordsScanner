package com.example.utils;


import com.example.dao.SiteUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.util.Optional;

@Slf4j
public class CookieUtils {
    public static final String UID_KEY = "Uid";
    public static final String COOKIE_DOMAIN = "localhost";
    public static final String COOKIE_PATH = "/";

    private CookieUtils() {
    }

    public static Optional<HttpCookie> getCookie(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst(UID_KEY));
    }

    public static void addCookie(ServerHttpResponse response, SiteUser user) {
        ResponseCookie cookie = ResponseCookie.from(UID_KEY, user.getId())
                .httpOnly(true)
                .maxAge(3600000)
                .sameSite("None")
                .secure(true)
                .domain(COOKIE_DOMAIN)
                .path(COOKIE_PATH)
                .build();
        response.addCookie(cookie);
    }

    public static void deleteCookie(ServerHttpRequest request, ServerHttpResponse response) {
        if (CookieUtils.getCookie(request).isPresent()) {
            response.addCookie(ResponseCookie.from(UID_KEY, "")
                    .httpOnly(true)
                    .maxAge(0)
                    .sameSite("None")
                    .secure(true)
                    .domain(COOKIE_DOMAIN)
                    .path(COOKIE_PATH)
                    .build());
        }
    }
}
