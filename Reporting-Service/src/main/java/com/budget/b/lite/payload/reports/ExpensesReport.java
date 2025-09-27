package com.budget.b.lite.payload.reports;

import lombok.Data;

import java.util.List;

@Data
public class ExpensesReport {
    private Double total;
    private List<CategoryExpense> categories;
    private String mostUsedCategory;

    @Data
    public static class CategoryExpense {
        private String category;
        private Double amount;
        private Double percentage;
    }
}
