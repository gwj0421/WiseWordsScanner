package com.example.exception.error;

public class NoticeBoardApiError extends RuntimeException {
    public NoticeBoardApiError(String methodName) {
        super(String.format("NoticeBoardApi 자체 에러 : (%s)",methodName));
    }
}
