package com.example.exception.error;

public class SiteUserApiServerException extends RuntimeException {
    public SiteUserApiServerException(String methodName) {
        super(String.format("SiteUserApiServer 에서 문제 발생 (%s)",methodName));
    }
}
