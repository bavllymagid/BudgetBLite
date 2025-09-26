package com.budget.b.lite.controllers.finance_service;

import com.budget.b.lite.services.RoutingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final RoutingService routingService;
    private final String financeServiceUrl;

    public CategoryController(RoutingService routingService,
                              @Value("${services.finance.url:}") String directUrl) {
        this.routingService = routingService;
        this.financeServiceUrl = directUrl;
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestParam String name) {
        String url = financeServiceUrl + "/api/categories?name=" + name;
        return routingService.forward(url, HttpMethod.POST, null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getCategory(@PathVariable Long id) {
        String url = financeServiceUrl + "/api/categories/" + id;
        return routingService.forward(url, HttpMethod.GET, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        String url = financeServiceUrl + "/api/categories/" + id;
        return routingService.forward(url, HttpMethod.DELETE, null);
    }

    @GetMapping
    public ResponseEntity<String> getAllCategories() {
        String url = financeServiceUrl + "/api/categories";
        return routingService.forward(url, HttpMethod.GET, null);
    }
}
