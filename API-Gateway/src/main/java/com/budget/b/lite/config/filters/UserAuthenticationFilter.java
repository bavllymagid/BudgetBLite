package com.budget.b.lite.config.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(UserAuthenticationFilter.class.getCanonicalName());

    @Autowired
    RestTemplate restTemplate;
    @Value("${user.auth}")
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
            token = requestTokenHeader.substring(7);
            String url = authURL+"validate-token?token={token}";
            Boolean valid = restTemplate.getForObject(url, Boolean.class, token);
            if(Boolean.FALSE.equals(valid)) throw new ServletException("invalid token");
        } else {
            throw new ServletException("Invalid token Type");
        }

        filterChain.doFilter(request, response);
    }
}
