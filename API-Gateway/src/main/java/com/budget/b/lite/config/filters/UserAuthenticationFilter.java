package com.budget.b.lite.config.filters;

import com.budget.b.lite.services.RoutingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RoutingService service;
    private final ObjectMapper objectMapper;
    @Value("${user.acc:http://AUTH-SERVICE}")
    String authURL;

    public UserAuthenticationFilter(RoutingService service){
        this.service = service;
        objectMapper = new ObjectMapper();
    }

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
                String url = authURL + "/api/acc/token/validate";
                String refreshUrl = authURL + "/api/acc/token/refresh";
                String path = request.getServletPath();

                if("/api/acc/token/refresh".equals(path)) url = refreshUrl;

                ResponseEntity<String> validationResponse = service.forward(url, HttpMethod.GET, null);

                int valid = validationResponse.getStatusCode().value();

                if (valid < 200 || valid > 299) {
                    log.error("Token rejected by Auth Service");
                    sendUnauthorized(response, validationResponse.getBody());
                    return;
                }

                String responseBody = validationResponse.getBody();
                if (responseBody != null && !"/api/acc/token/refresh".equals(path)) {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        String email = jsonNode.get("email").asText();
                        request.setAttribute("email", email);
                    } catch (Exception e) {
                        log.error("Could not extract email from validation response", e);
                    }
                }

            } catch (Exception e) {
                log.error("Token validation failed", e);
                sendUnauthorized(response, "{ \"error\" : \""+ e.getMessage() + "\" }");
                return;
            }
        } else {
            sendUnauthorized(response, "{ \"error\" : \"Missing or invalid Authorization header\" }");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(message);
    }
}