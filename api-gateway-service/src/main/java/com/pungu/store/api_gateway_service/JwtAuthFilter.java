package com.pungu.store.api_gateway_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter {

    private final WebClient webClientConfig;

    private static final String AUTH_SERVICE_VALIDATE_URI = "/api/auth/validate";
    private static final String PUBLIC_PATH_PREFIX = "/api/auth/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip auth for public endpoints like login/register
        if (path.startsWith(PUBLIC_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // Always allow CORS preflight
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Allow all GET requests without login
        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
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
                .uri("lb://auth-service" + AUTH_SERVICE_VALIDATE_URI)
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
                    return unauthorized(exchange, "Invalid or expired token");
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        String body = String.format("{\"error\": \"%s\"}", message);
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

}