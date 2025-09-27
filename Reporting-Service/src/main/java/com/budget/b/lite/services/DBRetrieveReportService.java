package com.budget.b.lite.services;

import com.budget.b.lite.payload.ReportResponse;
import com.budget.b.lite.payload.reports.ExpensesReport;
import com.budget.b.lite.payload.reports.IncomeReport;
import com.budget.b.lite.payload.reports.SavingsReport;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DBRetrieveReportService {
    private final IncomeRepository incomeRepository;
    private final ExpensesRepository expensesRepository;

    public DBRetrieveReportService(IncomeRepository incomeRepository, ExpensesRepository expensesRepository) {
        this.incomeRepository = incomeRepository;
        this.expensesRepository = expensesRepository;
    }

    // Entry point that assembles the full report
    public ReportResponse generateReport(String email) {
        ReportResponse report = new ReportResponse();
        report.setUserEmail(email);
        report.setReportGeneratedAt(LocalDateTime.now());

        report.setIncome(buildIncome(email));
        report.setExpenses(buildExpenses(email));
        report.setSavings(buildSavings(email));

        return report;
    }

    // ================== INCOME ==================
    private IncomeReport buildIncome(String email) {
        // Get current month's income instead of total across all months
        BigDecimal currentMonthIncome = incomeRepository.getCurrentMonthIncome(email);
        List<Object[]> monthlyIncomeRows = incomeRepository.getMonthlyIncome(email);

        IncomeReport income = new IncomeReport();
        income.setTotal(currentMonthIncome.doubleValue());
        income.setMonthly(
                monthlyIncomeRows.stream().map(row -> {
                    IncomeReport.MonthlyIncome dto = new IncomeReport.MonthlyIncome();
                    // Updated to handle Integer year and month instead of String
                    Integer year = (Integer) row[0];
                    Integer month = (Integer) row[1];
                    BigDecimal amount = (BigDecimal) row[2];

                    // Format as YYYY-MM for consistency
                    dto.setMonth(String.format("%04d-%02d", year, month));
                    dto.setAmount(amount.doubleValue());
                    return dto;
                }).toList()
        );
        return income;
    }

    // ================== EXPENSES ==================
    private ExpensesReport buildExpenses(String email) {
        BigDecimal totalExpenses = expensesRepository.getTotalExpenses(email);
        List<Object[]> expensesByCategory = expensesRepository.getExpensesByCategory(email);

        ExpensesReport expenses = new ExpensesReport();
        expenses.setTotal(totalExpenses.doubleValue());

        List<ExpensesReport.CategoryExpense> categoryExpenses = expensesByCategory.stream().map(row -> {
            String category = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            ExpensesReport.CategoryExpense ce = new ExpensesReport.CategoryExpense();
            ce.setCategory(category);
            ce.setAmount(amount.doubleValue());
            if (totalExpenses.doubleValue() > 0) {
                ce.setPercentage((amount.doubleValue() / totalExpenses.doubleValue()) * 100);
            } else {
                ce.setPercentage(0.0);
            }
            return ce;
        }).toList();

        expenses.setCategories(categoryExpenses);

        // Use the new repository method for most used category
        String mostUsedCategory = expensesRepository.getMostUsedCategory(email);
        expenses.setMostUsedCategory(mostUsedCategory);

        return expenses;
    }

    // ================== SAVINGS ==================
    private SavingsReport buildSavings(String email) {
        BigDecimal totalIncome = incomeRepository.getCurrentMonthIncome(email);
        BigDecimal totalExpenses = expensesRepository.getTotalExpenses(email);

        SavingsReport savings = new SavingsReport();
        double savingsValue = totalIncome.doubleValue() - totalExpenses.doubleValue();
        savings.setTotal(savingsValue);

        if (totalIncome.doubleValue() > 0) {
            savings.setPercentageOfIncome((savingsValue / totalIncome.doubleValue()) * 100);
        } else {
            savings.setPercentageOfIncome(0.0);
        }

        return savings;
    }
}