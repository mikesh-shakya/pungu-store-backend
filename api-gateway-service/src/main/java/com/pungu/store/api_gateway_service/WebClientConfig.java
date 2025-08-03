package com.pungu.store.api_gateway_service;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced  // This enables Eureka-based resolution
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}

