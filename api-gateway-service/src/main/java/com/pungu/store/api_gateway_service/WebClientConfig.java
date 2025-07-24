package com.pungu.store.api_gateway_service;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced  // THIS enables Eureka-based resolution
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
