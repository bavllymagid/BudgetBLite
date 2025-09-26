package com.budget.b.lite.controllers;

import com.budget.b.lite.entities.Expenses;
import com.budget.b.lite.entities.Income;
import com.budget.b.lite.services.BudgetService;
import com.budget.b.lite.utils.dto.ExpensesDTO;
import com.budget.b.lite.utils.dto.ExpensesRequest;
import com.budget.b.lite.utils.dto.IncomeRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final BudgetService service;

    public BudgetController(BudgetService service){
        this.service = service;
    }

    // ---------- INCOME ----------

    @PostMapping("/income")
    public ResponseEntity<Income> addIncome(@RequestBody IncomeRequest request) {
        return ResponseEntity.ok(service.addIncome(request));
    }

    @GetMapping("/income/{email}")
    public ResponseEntity<Income> getUserIncome(@PathVariable String email) {
        return ResponseEntity.ok(service.getUserIncome(email));
    }

    // ---------- EXPENSES ----------

    @PostMapping("/expenses")
    public ResponseEntity<Expenses> addExpense(@RequestBody ExpensesRequest request) {
        return ResponseEntity.ok(service.addExpenses(request));
    }

    @GetMapping("/expenses/{email}")
    public ResponseEntity<Page<ExpensesDTO>> getUserExpenses(
            @PathVariable String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getUserExpenses(email, page, size));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expenses> updateExpense(
            @PathVariable("id") Long expenseId,
            @RequestBody ExpensesRequest request
    ) {
        return ResponseEntity.ok(service.updateExpense(expenseId, request));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long expenseId) {
        service.deleteExpenseById(expenseId);
        return ResponseEntity.noContent().build(); // 204
    }

}
