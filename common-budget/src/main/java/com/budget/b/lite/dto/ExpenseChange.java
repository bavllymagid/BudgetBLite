package com.budget.b.lite.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExpenseChange {
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private Long oldCategory;
    private Long newCategory;
}