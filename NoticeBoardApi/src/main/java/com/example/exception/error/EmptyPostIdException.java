package com.example.exception.error;

public class EmptyPostIdException extends RuntimeException {
    public EmptyPostIdException(String methodName) {
        super(String.format("postId 부재(%s)",methodName));
    }
}
