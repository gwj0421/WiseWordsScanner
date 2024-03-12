package com.example.service;

import com.example.dao.RefreshToken;
import com.example.dao.SiteUser;
import com.example.dto.LoginForm;
import com.example.dto.SignUpForm;
import com.example.repository.RefreshTokenRepository;
import com.example.repository.SiteUserRepository;
import com.example.utils.CookieUtils;
import com.example.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import static com.example.utils.CookieUtils.REFRESH_TOKEN_KEY;


@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {
    private final JwtUtils jwtUtils;
    private final SiteUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Boolean> checkAuth(ServerHttpRequest request) {
        return userRepository.existsSiteUserById(request.getHeaders().get("X-Authorization-Id").get(0));
    }

    @Override
    public Mono<ResponseEntity<Void>> login(@RequestBody LoginForm loginForm, ServerHttpRequest request, ServerHttpResponse response) {
        return userRepository.findSiteUserByUserId(loginForm.getUserId())
                .filter(user -> passwordEncoder.matches(loginForm.getPassword(), user.getPassword()))
                .flatMap(user -> {
                    response.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtUtils.generate(user.getId()));
                    return refreshTokenRepository.findRefreshTokenByUserId(loginForm.getUserId())
                            .switchIfEmpty(Mono.defer(()->refreshTokenRepository.save(new RefreshToken(user.getUserId(), jwtUtils.generateRefreshToken()))))
                            .map(refreshToken -> {
                                CookieUtils.deleteCookie(request, response);
                                CookieUtils.addCookie(response, refreshToken.getToken());
                                return ResponseEntity.status(HttpStatus.OK).<Void>build();
                            });
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Override
    public Mono<ResponseEntity<Void>> signUp(@RequestBody SignUpForm signUpForm) {
        return userRepository.existsSiteUserByUserId(signUpForm.getUserId())
                .flatMap(isExist -> {
                    if (!isExist) {
                        return userRepository.save(new SiteUser(signUpForm.getName(), signUpForm.getUserId(), passwordEncoder.encode(signUpForm.getPassword1()), signUpForm.getEmail()))
                                .map(user -> refreshTokenRepository.save(new RefreshToken(user.getUserId(), jwtUtils.generateRefreshToken())))
                                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<Void>> refresh(ServerHttpRequest request) {
        if (request.getHeaders().get(HttpHeaders.AUTHORIZATION) == null) {
            if (request.getCookies().get(REFRESH_TOKEN_KEY) == null) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            } else {
                String accessToken = jwtUtils.generate();
            }
        }
        return null;
    }
}
