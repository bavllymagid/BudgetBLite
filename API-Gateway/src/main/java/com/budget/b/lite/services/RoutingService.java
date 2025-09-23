package com.budget.b.lite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoutingService {
    private final RestTemplate restTemplate;

    // Use the load-balanced RestTemplate from your config
    public RoutingService(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setErrorHandler((response) -> false);
    }

    public <T> ResponseEntity<String> forward(String path, HttpMethod method, T body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<T> entity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(
                    path,
                    method,
                    entity,
                    String.class
            );
        } catch (Exception e) {
            // Log the error and return a meaningful response
            System.err.println("Error forwarding request to: " + path + " - " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Service temporarily unavailable\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}