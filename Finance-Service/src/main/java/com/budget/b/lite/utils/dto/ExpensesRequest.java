package com.budget.b.lite.utils.dto;

import com.budget.b.lite.entities.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ExpensesRequest(BigDecimal amount,
                              @JsonProperty("category_id")
                              Long categoryId) {
}
