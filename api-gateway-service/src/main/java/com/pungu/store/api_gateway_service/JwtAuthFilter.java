package com.pungu.store.api_gateway_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GatewayFilter {

    private final WebClient webClientConfig;

    private static final String AUTH_SERVICE_VALIDATE_URI = "http://auth-service/api/auth/validate";
    private static final String PUBLIC_PATH_PREFIX = "/api/auth";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip auth for public endpoints like login/register
        if (path.startsWith(PUBLIC_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Validate token with auth service
        return webClientConfig.post()
                .uri(AUTH_SERVICE_VALIDATE_URI)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    log.warn("Token validation failed with status: {}", clientResponse.statusCode());
                    return Mono.error(new RuntimeException("Token validation failed"));
                })
                .toBodilessEntity()
                .then(chain.filter(exchange))
                .onErrorResume(error -> {
                    log.error("Token validation error: {}", error.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}