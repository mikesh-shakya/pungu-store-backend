package com.pungu.store.api_gateway_service;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Shared builder with Spring Cloud LoadBalancer support.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient ratingWebClient(WebClient.Builder builder) {
        return builder.baseUrl("lb://rating-service/api/ratings").build();
    }

    @Bean
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder.baseUrl("lb://auth-service/api/users").build();
    }

    @Bean
    public WebClient authWebClient(WebClient.Builder builder) {
        return builder.baseUrl("lb://auth-service/api/auth").build();
    }

    @Bean
    public WebClient bookWebClient(WebClient.Builder builder) {
        return builder.baseUrl("lb://book-service/api/books").build();
    }

}
