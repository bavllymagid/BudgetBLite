package com.budget.b.lite.utils.dto;

import com.budget.b.lite.entities.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpensesRequest(String userEmail, BigDecimal amount, String categoryName) {
}
