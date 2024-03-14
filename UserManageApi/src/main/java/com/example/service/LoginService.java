package com.example.service;

import com.example.dto.SignUpForm;
import com.example.dto.LoginForm;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;


public interface LoginService {
    Mono<ResponseEntity<Void>> checkAuth(ServerHttpRequest request);
    Mono<ResponseEntity<Void>> login(LoginForm loginForm, ServerHttpRequest request, ServerHttpResponse response);
    Mono<ResponseEntity<Void>> signUp(SignUpForm signUpForm);
}
