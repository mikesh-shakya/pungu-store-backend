package com.pungu.store.api_gateway_service;

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
@Slf4j
public class JwtAuthFilter implements GlobalFilter {

    private final WebClient.Builder webClient;


    private static final String AUTH_SERVICE_VALIDATE_URI = "/api/auth/validate";
    private static final String PUBLIC_PATH_PREFIX = "/api/auth/";

    public JwtAuthFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder;
    }

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
        log.info("Incoming request to {} with Authorization: {}", path, authHeader == null ? "null" : "present");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

//        // Validate token with auth service
//        return webClientConfig.post()
//                .uri("lb://auth-service" + AUTH_SERVICE_VALIDATE_URI)
//                .header(HttpHeaders.AUTHORIZATION, authHeader)
//                .retrieve()
//                .onStatus(HttpStatusCode::isError, clientResponse -> {
//                    log.warn("Token validation failed with status: {}", clientResponse.statusCode());
//                    return Mono.error(new RuntimeException("Token validation failed"));
//                })
//                .toBodilessEntity()
//                .then(chain.filter(exchange))
//                .onErrorResume(error -> {
//                    log.error("Token validation error: {}", error.getMessage());
//                    return unauthorized(exchange, "Invalid or expired token");
//                });

        // Validate token with auth service
        Mono<Void> authCheck = webClient.build()
                .post()
                .uri("lb://auth-service" + AUTH_SERVICE_VALIDATE_URI)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                // If auth-service returns a 4xx, read body and map to InvalidTokenException
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Invalid or expired token")
                                .flatMap(body -> {
                                    log.warn("Auth service returned {}: {}", clientResponse.statusCode(), body);
                                    return Mono.error(new InvalidTokenException((HttpStatus) clientResponse.statusCode(), body));
                                })
                )
                // If auth-service returns 5xx, bubble up an AuthServiceUnavailableException
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("Auth service error")
                                .flatMap(body -> {
                                    log.error("Auth service error {}: {}", clientResponse.statusCode(), body);
                                    return Mono.error(new AuthServiceUnavailableException("Auth service error"));
                                })
                )
                .toBodilessEntity()
                .then();

        // Only convert InvalidTokenException into a client 401/forward body.
        // Let other exceptions (including exceptions raised by chain.filter) propagate normally,
        // except AuthServiceUnavailableException which we convert to 503.
        return authCheck
                .then(chain.filter(exchange))
                .onErrorResume(InvalidTokenException.class, ex -> {
                    InvalidTokenException itx = (InvalidTokenException) ex;
                    log.info("Authentication failed: {} - {}", itx.getStatus(), itx.getMessage());
                    // forward status and message from auth-service if available, otherwise 401
                    HttpStatus status = itx.getStatus() != null ? itx.getStatus() : HttpStatus.UNAUTHORIZED;
                    return unauthorized(exchange, status, itx.getMessage());
                })
                .onErrorResume(AuthServiceUnavailableException.class, ex -> {
                    log.error("Auth service unavailable: {}", ex.getMessage());
                    return unauthorized(exchange, HttpStatus.SERVICE_UNAVAILABLE, "Authentication service unavailable");
                })
                // Do NOT catch other exceptions here — let them propagate so downstream errors are returned.
                ;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), sanitize(status.getReasonPhrase()), sanitize(message));
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private String sanitize(String raw) {
        if (raw == null) return "";
        return raw.replace("\"", "'");
    }
}