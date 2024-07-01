package com.example.utils;

import com.example.exception.error.AuthorizationException;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class CookieUtils {
    private static final String UID_KEY = "Uid";
    private CookieUtils() {
    }

    public static String getUserId(ServerHttpRequest request) {
        if (request.getCookies().containsKey(UID_KEY)) {
            return request.getCookies().getFirst(UID_KEY).getValue();
        }
        throw new AuthorizationException("getUserId");
    }

}
