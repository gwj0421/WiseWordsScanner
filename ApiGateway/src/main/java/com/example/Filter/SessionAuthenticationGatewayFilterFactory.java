package com.example.Filter;

import com.example.dto.TokenUser;
import com.example.utils.JwtUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class SessionAuthenticationGatewayFilterFactory extends
        AbstractGatewayFilterFactory<SessionAuthenticationGatewayFilterFactory.Config> {
    private static final String ROLE_KEY = "role";
    public SessionAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Getter
    public static class Config {
        private String roleCode;

        public Config(String roleCode) {
            this.roleCode = roleCode;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(ROLE_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            if (!jwtUtils.isValid(token)) {
                return onError(response, "not valid jwt token", HttpStatus.BAD_REQUEST);
            }

            TokenUser tokenUser = jwtUtils.decode(token);
            if (!hasRole(tokenUser, config.getRoleCode())) {
                return onError(response, "invalid role", HttpStatus.FORBIDDEN);
            }

            addAuthorizationHeaders(request, tokenUser);
            return chain.filter(exchange);
        });
        return filter;
    }

    private boolean containsAuthorization(ServerHttpRequest request) {
        return request.getCookies().containsKey()
    }

    private boolean checkBearerToken(String bearerToken) {
        return bearerToken.length() > 7 && bearerToken.startsWith("Bearer ");
    }

    private boolean hasRole(TokenUser tokenUser, String role) {
        return role.equals(tokenUser.getRole());
    }

    private void addAuthorizationHeaders(ServerHttpRequest request, TokenUser tokenUser) {
        request.mutate()
                .header("X-Authorization-Id", tokenUser.getId())
                .header("X-Authorization-Role", tokenUser.getRole())
                .build();
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
