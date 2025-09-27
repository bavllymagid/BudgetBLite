package com.budget.b.lite.controllers.finance_service;


import com.budget.b.lite.services.RoutingService;
import com.budget.b.lite.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final RoutingService routingService;
    private final String financeServiceUrl;
    private final TokenUtils tokenUtils;

    public BudgetController(RoutingService routingService,
                            TokenUtils tokenUtils,
                            @Value("${services.finance.url:}") String directUrl) {
        this.routingService = routingService;
        this.financeServiceUrl = directUrl;
        this.tokenUtils = tokenUtils;
    }

    // ---------- INCOME ----------

    @PostMapping("/income")
    public ResponseEntity<String> addIncome(@RequestParam BigDecimal amount) {
        String email = tokenUtils.extractEmailFromToken();
        String url = financeServiceUrl + "/api/budget/income/" + email + "?amount=" + amount;
        return routingService.forward(url, HttpMethod.POST, null);
    }

    @GetMapping("/income")
    public ResponseEntity<String> getUserIncome() {
        String email = tokenUtils.extractEmailFromToken();
        String url = financeServiceUrl + "/api/budget/income/" + email;
        return routingService.forward(url, HttpMethod.GET, null);
    }

    // ---------- EXPENSES ----------

    @PostMapping("/expenses")
    public ResponseEntity<String> addExpense(@RequestBody Object request) {
        String email = tokenUtils.extractEmailFromToken();
        String url = financeServiceUrl + "/api/budget/expenses/"+email;
        return routingService.forward(url, HttpMethod.POST, request);
    }

    @GetMapping("/expenses")
    public ResponseEntity<String> getUserExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = tokenUtils.extractEmailFromToken();
        String url = financeServiceUrl + "/api/budget/expenses/" + email + "?page=" + page + "&size=" + size;
        return routingService.forward(url, HttpMethod.GET, null);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<String> updateExpense(
            @PathVariable("id") Long expenseId,
            @RequestBody Object request
    ) {
        String url = financeServiceUrl + "/api/budget/expenses/" + expenseId;
        return routingService.forward(url, HttpMethod.PUT, request);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable("id") Long expenseId) {
        String url = financeServiceUrl + "/api/budget/expenses/" + expenseId;
        return routingService.forward(url, HttpMethod.DELETE, null);
    }
}
