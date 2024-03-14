package com.example.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SessionAuthenticationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<SessionAuthenticationGatewayFilterFactory.Config> {
    public static final String UID_KEY = "Uid";
    public static final String REQUIRE_AUTHORIZATION_ROLE_KEY = "X-Require-Authorization-Role";


    public SessionAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Getter
    public static class Config {
        private String role;

        public Config(String role) {
            this.role = role;
        }
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            if (!containsAuthorization(request)) {
                return onError(response, "authorization information is not in request", HttpStatus.UNAUTHORIZED);
            }

            request.mutate().header(REQUIRE_AUTHORIZATION_ROLE_KEY, config.getRole());

            return chain.filter(exchange);
        });
    }

    private boolean containsAuthorization(ServerHttpRequest request) {
        return request.getCookies().containsKey(UID_KEY);
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
