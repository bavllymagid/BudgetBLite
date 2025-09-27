package com.budget.b.lite.services;

import com.budget.b.lite.payload.ReportResponse;
import com.budget.b.lite.payload.reports.ExpensesReport;
import com.budget.b.lite.payload.reports.IncomeReport;
import com.budget.b.lite.payload.reports.SavingsReport;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private final IncomeRepository incomeRepository;
    private final ExpensesRepository expensesRepository;

    public ReportService(IncomeRepository incomeRepository, ExpensesRepository expensesRepository) {
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
        BigDecimal totalIncome = incomeRepository.getTotalIncome(email);
        List<Object[]> monthlyIncomeRows = incomeRepository.getMonthlyIncome(email);

        IncomeReport income = new IncomeReport();
        income.setTotal(totalIncome.doubleValue());
        income.setMonthly(
                monthlyIncomeRows.stream().map(row -> {
                    IncomeReport.MonthlyIncome dto = new IncomeReport.MonthlyIncome();
                    dto.setMonth((String) row[0]);
                    dto.setAmount(((Number) row[1]).doubleValue());
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
            double amount = ((Number) row[1]).doubleValue();

            ExpensesReport.CategoryExpense ce = new ExpensesReport.CategoryExpense();
            ce.setCategory(category);
            ce.setAmount(amount);
            if (totalExpenses.doubleValue() > 0) {
                ce.setPercentage((amount / totalExpenses.doubleValue()) * 100);
            } else {
                ce.setPercentage(0.0);
            }
            return ce;
        }).toList();

        expenses.setCategories(categoryExpenses);

        // most used category
        Optional<ExpensesReport.CategoryExpense> mostUsedCategory = categoryExpenses.stream()
                .max(Comparator.comparingDouble(ExpensesReport.CategoryExpense::getAmount));
        expenses.setMostUsedCategory(mostUsedCategory.map(ExpensesReport.CategoryExpense::getCategory).orElse(null));

        return expenses;
    }

    // ================== SAVINGS ==================
    private SavingsReport buildSavings(String email) {
        BigDecimal totalIncome = incomeRepository.getTotalIncome(email);
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

