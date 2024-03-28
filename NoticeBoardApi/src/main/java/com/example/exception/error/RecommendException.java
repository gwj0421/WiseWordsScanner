package com.example.exception.error;

public class RecommendException extends RuntimeException {
    public RecommendException(String methodName) {
        super(String.format("Recommend 시스템 에러 (%s)",methodName));
    }
}
