package com.example.config;

import com.example.filter.SessionAuthenticationGatewayFilterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfiguration {
    private static final String NOTICE_BOARD_API_URI = "lb://notice-board-api";
    private static final String USER_MANAGE_API_URI = "lb://user-manage-api";
    private static final String OCR_API_URI = "lb://ocr-api";
    private static final String BASIC_AUTHORIZATION_ROLE = "ROLE_USER";
    private final SessionAuthenticationGatewayFilterFactory sessionAuthenticationGatewayFilterFactory;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("show-board-by-anonymous", p -> p
                        .path("/api/post/page")
                        .and().method("GET")
                        .filters(r -> r.rewritePath("/api/post/page", "/post/page"))
                        .uri(NOTICE_BOARD_API_URI))
                .route("check-authorization", p -> p
                        .path("/api/user/auth")
                        .and().method("GET")
                        .filters(r -> r.rewritePath("/api/user/auth", "/user/auth")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(USER_MANAGE_API_URI))
                .route("login", p -> p
                        .path("/api/user/login")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/user/login", "/user/login"))
                        .uri(USER_MANAGE_API_URI))
                .route("logout", p -> p
                        .path("/api/user/logout")
                        .and().method("GET")
                        .filters(r -> r.rewritePath("/api/user/logout", "/user/logout"))
                        .uri(USER_MANAGE_API_URI))
                .route("signUp", p -> p
                        .path("/api/user/signUp")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/user/signUp", "/user/signUp"))
                        .uri(USER_MANAGE_API_URI))
                .route("recommend", p -> p
                        .path("/api/reco/**")
                        .filters(r -> r.rewritePath("/api/reco/(?<path>.*)", "/reco/$\\{path}")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("user-manage-api", p -> p
                        .path("/api/user/**")
                        .filters(r -> r.rewritePath("/api/user/(?<path>.*)", "/user/$\\{path}")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(USER_MANAGE_API_URI))
                .route("create-new-post", p -> p
                        .path("/api/post")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/post", "/post")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("show-board-by-authorization", p -> p
                        .path("/api/post/**")
                        .filters(r -> r.rewritePath("/api/post/(?<path>.*)", "/post/$\\{path}")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("ocr-service", p -> p
                        .path("/api/ocr")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/ocr", "/ocr")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(OCR_API_URI))
                .route("create-comment-service", p -> p
                        .path("/api/comment")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/comment", "/comment")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("comment-service", p -> p
                        .path("/api/comment/**")
                        .filters(r -> r.rewritePath("/api/comment/(?<path>.*)", "/comment/$\\{path}")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("create-reply-service", p -> p
                        .path("/api/reply")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/reply", "/reply")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("reply-service", p -> p
                        .path("/api/reply/**")
                        .filters(r -> r.rewritePath("/api/reply/(?<path>.*)", "/reply/$\\{path}")
                                .filter(sessionAuthenticationGatewayFilterFactory.apply(new SessionAuthenticationGatewayFilterFactory.Config(BASIC_AUTHORIZATION_ROLE))))
                        .uri(NOTICE_BOARD_API_URI))
                .build();
    }
}
