package com.univhis; // Assuming the base package

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced; // Import LoadBalanced

@SpringBootApplication
@EnableDiscoveryClient // Enable Nacos discovery client
public class NotificationLogServiceApplication { // Changed class name from default to be more specific to module

    public static void main(String[] args) {
        SpringApplication.run(NotificationLogServiceApplication.class, args);
    }

    @Bean
    @LoadBalanced // Enable LoadBalancer for RestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}