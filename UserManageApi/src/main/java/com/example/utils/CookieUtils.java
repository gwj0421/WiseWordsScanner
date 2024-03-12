package com.example.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Slf4j
public class CookieUtils {
    public static final String REFRESH_TOKEN_KEY = "RefreshToken";
    private CookieUtils() {
    }

    public static void addCookie(ServerHttpResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(true)
                .maxAge(3600000)
                .sameSite("None")
                .domain("localhost")
                .path("/")
                .build();
        response.addCookie(cookie);
    }

    public static void deleteCookie(ServerHttpRequest request, ServerHttpResponse response) {
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (cookies != null) {
            for (Map.Entry<String, List<HttpCookie>> entry : cookies.entrySet()) {
                for (HttpCookie cookie : entry.getValue()) {
                    if (cookie.getName().equals(REFRESH_TOKEN_KEY)) {
                        response.addCookie(ResponseCookie.from(cookie.getName(), cookie.getValue()).maxAge(0).build());
                    }
                }
            }
        }
    }
}
