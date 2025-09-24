package com.budget.b.lite.controllers.auth_service;

import com.budget.b.lite.services.RoutingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/acc")
public class AccountController {
    private final RoutingService routingService;
    private final String accountServiceUrl;

    public AccountController(RoutingService routingService,
                             @Value("${services.account.name:AUTH-SERVICE}") String accountServiceName,
                             @Value("${services.account.url:}") String directUrl) {
        this.routingService = routingService;

        // Use direct URL if provided, otherwise use service name for discovery
        if (!directUrl.isEmpty()) {
            this.accountServiceUrl = directUrl;
        } else {
            this.accountServiceUrl = "http://" + accountServiceName;
        }
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<String> refresh() {
        String url = accountServiceUrl + "/api/acc/token/refresh";
        return routingService.forward(url, HttpMethod.GET, null);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() {
        String url = accountServiceUrl + "/api/acc/delete";
        return routingService.forward(url, HttpMethod.DELETE, null);
    }

    @GetMapping("/token/validate")
    public ResponseEntity<String> validate() {
        String url = accountServiceUrl + "/api/acc/token/validate";
        return routingService.forward(url, HttpMethod.GET, null);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        String url = accountServiceUrl + "/api/acc/logout";
        return routingService.forward(url, HttpMethod.GET, null);
    }
}