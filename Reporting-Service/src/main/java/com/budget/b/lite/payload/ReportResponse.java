package com.budget.b.lite.payload;


import com.budget.b.lite.payload.reports.ExpensesReport;
import com.budget.b.lite.payload.reports.IncomeReport;
import com.budget.b.lite.payload.reports.SavingsReport;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportResponse {
    private String userEmail;
    private LocalDateTime reportGeneratedAt;
    private IncomeReport income;
    private ExpensesReport expenses;
    private SavingsReport savings;
}
