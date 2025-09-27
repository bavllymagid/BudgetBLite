package com.budget.b.lite.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record IncomeRequest(BigDecimal amount) {
}
