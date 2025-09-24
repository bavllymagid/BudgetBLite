package com.budget.b.lite.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(30000);    // 30 seconds

        RestTemplate template = new RestTemplate(factory);

        // Add interceptor to handle headers properly
        template.getInterceptors().add((request, body, execution) -> {
            // Remove problematic headers that might cause chunking issues
            request.getHeaders().remove("Transfer-Encoding");
            request.getHeaders().remove("Connection");
            request.getHeaders().remove("Content-Length"); // Let the server calculate this

            // Set proper content type
            if (!request.getHeaders().containsKey("Content-Type") && body.length > 0) {
                request.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            }

            return execution.execute(request, body);
        });

        // Don't set a custom error handler - let RestTemplate throw exceptions for HTTP errors
        // This allows proper handling of 4xx and 5xx responses

        return template;
    }
}