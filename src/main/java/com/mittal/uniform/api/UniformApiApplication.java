package com.mittal.uniform.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <--- Activates the background worker thread pool
public class UniformApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniformApiApplication.class, args);
    }
}
