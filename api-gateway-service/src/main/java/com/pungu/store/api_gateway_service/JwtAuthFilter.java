package com.pungu.store.api_gateway_service;

import com.pungu.store.api_gateway_service.exceptions.AuthServiceUnavailableException;
import com.pungu.store.api_gateway_service.exceptions.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Global JWT filter that validates Bearer tokens by calling the Auth Service.
 */
@Component
@Slf4j
@Order(1)
public class JwtAuthFilter implements GlobalFilter {

    private final WebClient authWebClient;

    private static final String VALIDATE_URI = "/validate";
    private static final String PUBLIC_PATH_PREFIX = "/api/auth/";

    public JwtAuthFilter(@Qualifier("authWebClient") WebClient authWebClient) {
        this.authWebClient = authWebClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // Skip authentication for login/register endpoints
        if (path.startsWith(PUBLIC_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // Allow CORS preflight
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // Allow all GET requests without login (your current policy)
        if (method == HttpMethod.GET) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for {}", path);
            return unauthorized(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        // Validate JWT via auth service
        return authWebClient.post()
                .uri(VALIDATE_URI)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Invalid or expired token")
                                .flatMap(body -> {
                                    log.warn("Auth service 4xx: {}", body);
                                    return Mono.error(new InvalidTokenException((HttpStatus) clientResponse.statusCode(), body));
                                })
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Auth service unavailable")
                                .flatMap(body -> {
                                    log.error("Auth service 5xx: {}", body);
                                    return Mono.error(new AuthServiceUnavailableException(body));
                                })
                )
                .toBodilessEntity()
                .then(chain.filter(exchange))
                .onErrorResume(InvalidTokenException.class, ex -> {
                    log.info("Token validation failed: {}", ex.getMessage());
                    HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.UNAUTHORIZED;
                    return unauthorized(exchange, status, ex.getMessage());
                })
                .onErrorResume(AuthServiceUnavailableException.class, ex -> {
                    log.error("Auth service unavailable: {}", ex.getMessage());
                    return unauthorized(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Authentication service unavailable");
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Unexpected WebClient error: {}", ex.getMessage());
                    return unauthorized(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Auth validation failed");
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(),
                sanitize(status.getReasonPhrase()),
                sanitize(message)
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private String sanitize(String raw) {
        if (raw == null) return "";
        return raw.replace("\"", "'").replace("\n", "").trim();
    }
}
