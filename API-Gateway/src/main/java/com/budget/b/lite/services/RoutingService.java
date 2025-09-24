package com.budget.b.lite.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

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
            // Get the original request to copy headers
            HttpServletRequest request = getCurrentHttpRequest();
            HttpHeaders headers = new HttpHeaders();

            // Copy all headers from the original request
            if (request != null) {
                copyHeaders(request, headers);
            }

            // Ensure content type is set for POST/PUT requests
            if ((method == HttpMethod.POST || method == HttpMethod.PUT) && body != null) {
                if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                }
            }

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
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Service temporarily unavailable\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            // Skip certain headers that shouldn't be forwarded or might cause issues
            if (shouldSkipHeader(headerName)) {
                continue;
            }

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
    }

    private boolean shouldSkipHeader(String headerName) {
        String lowerCaseHeaderName = headerName.toLowerCase();

        // Skip headers that might cause issues when forwarding
        return lowerCaseHeaderName.equals("host") ||
                lowerCaseHeaderName.equals("content-length") ||
                lowerCaseHeaderName.equals("connection") ||
                lowerCaseHeaderName.equals("transfer-encoding") ||
                lowerCaseHeaderName.equals("upgrade");
    }
}