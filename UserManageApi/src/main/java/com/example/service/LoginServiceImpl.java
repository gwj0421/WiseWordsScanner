package com.example.service;

import com.example.dao.SiteUser;
import com.example.dto.LoginForm;
import com.example.dto.SignUpForm;
import com.example.repository.SiteUserRepository;
import com.example.utils.CookieUtils;
import com.example.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final SiteUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<ResponseEntity<Void>> checkAuth(ServerHttpRequest request) {
        Optional<String> initAuthRole = HeaderUtils.getAuthRole(request);
        Optional<HttpCookie> initAuthorId = CookieUtils.getCookie(request);
        if (initAuthRole.isPresent() && initAuthorId.isPresent()) {
            String reqAuthRole = initAuthRole.get();
            String authorId = initAuthorId.get().getValue();
            return userRepository.findSiteUserById(authorId)
                    .filter(user -> user.getRole().equals(reqAuthRole))
                    .map(user -> ResponseEntity.status(HttpStatus.OK).<Void>build())
                    .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Override
    public Mono<ResponseEntity<Void>> login(@RequestBody LoginForm loginForm, ServerHttpRequest request, ServerHttpResponse response) {
        return userRepository.findSiteUserByUserId(loginForm.getUserId())
                .filter(user -> passwordEncoder.matches(loginForm.getPassword(), user.getPassword()))
                .map(user -> {
                    CookieUtils.deleteCookie(request,response);
                    CookieUtils.addCookie(response,user);
                    return ResponseEntity.status(HttpStatus.OK).<Void>build();
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Override
    public Mono<ResponseEntity<Void>> signUp(@RequestBody SignUpForm signUpForm) {
        return userRepository.findSiteUserByUserId(signUpForm.getUserId())
                .flatMap(user -> userRepository.save(new SiteUser(signUpForm.getName(), signUpForm.getUserId(), passwordEncoder.encode(signUpForm.getPassword1()), signUpForm.getEmail()))
                        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).<Void>build()))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }
}
