package com.example.exception.handler;

import com.example.exception.error.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({SiteUserApiServerException.class, EmptyPostIdException.class, AuthorizationException.class, NoticeBoardApiError.class})
    public ResponseEntity<String> handler(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler(RecommendException.class)
    public ResponseEntity<Integer> recommendHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }
}
