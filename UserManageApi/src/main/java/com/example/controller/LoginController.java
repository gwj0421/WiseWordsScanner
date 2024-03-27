package com.example.controller;

import com.example.dto.LoginForm;
import com.example.dto.SignUpForm;
import com.example.service.LoginService;
import com.example.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/auth")
    public Mono<ResponseEntity<String>> checkAuth(ServerHttpRequest request) {
        return loginService.checkAuth(request);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(@RequestBody LoginForm loginForm, ServerHttpRequest request, ServerHttpResponse response) {
        return loginService.login(loginForm, request, response);
    }

    @GetMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerHttpRequest request,ServerHttpResponse response) {
        CookieUtils.deleteCookie(request,response);
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/signUp")
    public Mono<ResponseEntity<Void>> signUp(@RequestBody SignUpForm signUpForm) {
        return loginService.signUp(signUpForm);
    }
}
