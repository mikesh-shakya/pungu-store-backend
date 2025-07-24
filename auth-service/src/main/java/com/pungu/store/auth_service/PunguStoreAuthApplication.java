package com.pungu.store.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PunguStoreAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(PunguStoreAuthApplication.class, args);
        System.out.println("Hello! Welcome to the Pungu Store.");
    }
}
