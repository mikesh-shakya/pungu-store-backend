package com.pungu.store.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PunguStoreAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(PunguStoreAuthApplication.class, args);
        System.out.println("Hello! Welcome to the Pungu Store.");
    }
}
