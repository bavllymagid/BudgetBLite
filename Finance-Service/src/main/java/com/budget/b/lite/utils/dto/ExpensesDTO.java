package com.budget.b.lite.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpensesDTO {
    private Long id;
    @JsonProperty("user_email")
    private String userEmail;
    private BigDecimal amount;
    private LocalDate date;
    @JsonProperty("category")
    private String categoryName;

    public ExpensesDTO(Long id, String userEmail, BigDecimal amount, LocalDate date, String categoryName) {
        this.id = id;
        this.userEmail = userEmail;
        this.amount = amount;
        this.date = date;
        this.categoryName = categoryName;
    }
}
