package com.example.exception.error;

public class IllegalRecommendableException extends RuntimeException {
    public IllegalRecommendableException(String methodName) {
        super(String.format("잘못된 recommendable 객체 (%s)",methodName));
    }
}
