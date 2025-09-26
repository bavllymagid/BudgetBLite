package com.budget.b.lite.controllers.finance_service;


import com.budget.b.lite.services.RoutingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final RoutingService routingService;
    private final String financeServiceUrl;

    public BudgetController(RoutingService routingService,
                            @Value("${services.finance.url:}") String directUrl) {
        this.routingService = routingService;
        this.financeServiceUrl = directUrl;
    }

    // ---------- INCOME ----------

    @PostMapping("/income")
    public ResponseEntity<String> addIncome(@RequestBody Object request) {
        String url = financeServiceUrl + "/api/budget/income";
        return routingService.forward(url, HttpMethod.POST, request);
    }

    @GetMapping("/income/{email}")
    public ResponseEntity<String> getUserIncome(@PathVariable String email) {
        String url = financeServiceUrl + "/api/budget/income/" + email;
        return routingService.forward(url, HttpMethod.GET, null);
    }

    // ---------- EXPENSES ----------

    @PostMapping("/expenses")
    public ResponseEntity<String> addExpense(@RequestBody Object request) {
        String url = financeServiceUrl + "/api/budget/expenses";
        return routingService.forward(url, HttpMethod.POST, request);
    }

    @GetMapping("/expenses/{email}")
    public ResponseEntity<String> getUserExpenses(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
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
