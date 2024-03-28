package com.example.exception.error;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String methodName) {
        super(String.format("auth 에러(%s)",methodName));
    }
}
