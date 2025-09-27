package com.budget.b.lite.payload.reports;

import lombok.Data;

import java.util.List;

@Data
public class IncomeReport {
    private Double total;
    private List<MonthlyIncome> monthly;

    @Data
    public static class MonthlyIncome {
        private String month;
        private Double amount;
    }
}
