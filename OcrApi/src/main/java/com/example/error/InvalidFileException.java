package com.example.error;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String methodName) {
        super(String.format("Invalid file : %s",methodName));
    }
}
