package com.example.config;

import com.example.Filter.JwtAuthenticationGatewayFilterFactory;
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
    private final JwtAuthenticationGatewayFilterFactory jwtAuthenticationGatewayFilterFactory;

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
                                .filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config("ROLE_USER"))))
                        .uri(USER_MANAGE_API_URI))
                .route("login", p -> p
                        .path("/api/user/login")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/user/login", "/user/login"))
                        .uri(USER_MANAGE_API_URI))
                .route("signUp", p -> p
                        .path("/api/user/signUp")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/user/signUp", "/user/signUp"))
                        .uri(USER_MANAGE_API_URI))
                .route("user-manage-api", p -> p
                        .path("/api/user/**")
                        .filters(r -> r.rewritePath("/api/user/(?<path>.*)", "/user/$\\{path}")
                                .filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config("ROLE_USER"))))
                        .uri(USER_MANAGE_API_URI))
                .route("create-new-post", p -> p
                        .path("/api/post")
                        .and().method("POST")
                        .filters(r -> r.rewritePath("/api/post", "/post")
                                .filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config("ROLE_USER"))))
                        .uri(NOTICE_BOARD_API_URI))
                .route("show-board-by-authorization", p -> p
                        .path("/api/post/**")
                        .filters(r -> r.rewritePath("/api/post/(?<path>.*)", "/post/$\\{path}")
                                .filter(jwtAuthenticationGatewayFilterFactory.apply(new JwtAuthenticationGatewayFilterFactory.Config("ROLE_USER"))))
                        .uri(NOTICE_BOARD_API_URI))
                .build();
    }

}
