package com.budget.b.lite.controllers.auth_service;

import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.RegisterRequest;
import com.budget.b.lite.services.RoutingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RoutingService routingService;
    private final String authServiceUrl;

    public AuthController(RoutingService routingService,
                          @Value("${services.auth.url:}") String directUrl) {
        this.routingService = routingService;
        this.authServiceUrl = directUrl;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String url = authServiceUrl + "/api/auth/login";
        return routingService.forward(url, HttpMethod.POST, request);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String url = authServiceUrl + "/api/auth/register";
        return routingService.forward(url, HttpMethod.POST, request);
    }

}