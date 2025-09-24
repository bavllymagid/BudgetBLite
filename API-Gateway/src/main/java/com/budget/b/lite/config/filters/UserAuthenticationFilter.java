package com.budget.b.lite.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(UserAuthenticationFilter.class.getCanonicalName());

    @Autowired
    RestTemplate restTemplate;
    @Value("${user.acc:http://AUTH-SERVICE}")
    String authURL;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestTokenHeader = request.getHeader("Authorization");
        String token = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            try {
                // Create headers for the validation request
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", requestTokenHeader);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                String url = authURL + "/api/acc/token/validate";
                String refreshUrl = authURL + "/api/acc/token/refresh";
                String path = request.getServletPath();

                if("/api/acc/token/refresh".equals(path)) url = refreshUrl;

                ResponseEntity<Object> validationResponse = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Object.class
                );

                int valid = validationResponse.getStatusCode().value();

                if (valid < 200 || valid > 299) {
                    throw new ServletException("Invalid token");
                }
            } catch (Exception e) {
                log.error("Token validation failed", e);
                throw new ServletException("Token validation failed: " + e.getMessage());
            }
        } else {
            throw new ServletException("Invalid token type");
        }

        filterChain.doFilter(request, response);
    }
}