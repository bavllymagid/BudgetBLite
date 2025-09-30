package com.budget.b.lite.services;

import com.budget.b.lite.entities.Income;
import com.budget.b.lite.exceptions.custom_exceptions.NoReportFoundException;
import com.budget.b.lite.payload.ReportResponse;
import com.budget.b.lite.payload.reports.ExpensesReport;
import com.budget.b.lite.payload.reports.IncomeReport;
import com.budget.b.lite.payload.reports.SavingsReport;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // Entry point that assembles the full report with caching
    @Cacheable(value = "reports", key = "#email + ':' + #date.toString()")
    public ReportResponse generateReport(String email, LocalDate date) {
        ReportResponse report = new ReportResponse();
        report.setUserEmail(email);
        report.setReportGeneratedAt(LocalDateTime.now());

        if(date == null) date = LocalDate.now();

        report.setIncome(buildIncome(email, date));
        report.setExpenses(buildExpenses(email, date));
        report.setSavings(buildSavings(email, date));

        return report;
    }

    // ================== CURRENT MONTH INCOME ==================
    public IncomeReport buildIncome(String email, LocalDate date) {
        BigDecimal currentMonthIncome = incomeRepository.getIncomeForMonth(email, date.getYear(), date.getMonthValue())
                .orElseThrow(()->new NoReportFoundException("No report found for current month please insert your income"));
        List<Income> monthlyIncomeRows = incomeRepository.getMonthlyIncomes(email);

        IncomeReport income = new IncomeReport();
        income.setTotal(currentMonthIncome.doubleValue());
        income.setMonthly(monthlyIncomeRows.stream().map(in->{
            IncomeReport.MonthlyIncome monthlyIncome =  new IncomeReport.MonthlyIncome();
            monthlyIncome.setAmount(in.getAmount());
            monthlyIncome.setMonth(in.getDate());
            return monthlyIncome;
        }).toList());
        return income;
    }

    // ================== CURRENT MONTH EXPENSES ==================
    public ExpensesReport buildExpenses(String email, LocalDate date) {
        BigDecimal totalExpenses = expensesRepository.getTotalExpenses(email, date.getYear(), date.getMonthValue());
        List<Object[]> expensesByCategory = expensesRepository.getExpensesByCategory(email, date.getYear(), date.getMonthValue());

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
        String mostUsedCategory = expensesRepository.getMostUsedCategory(email, date.getYear(), date.getMonthValue());
        expenses.setMostUsedCategory(mostUsedCategory);

        return expenses;
    }

    // ================== CURRENT MONTH SAVINGS ==================
    public SavingsReport buildSavings(String email, LocalDate date) {
        BigDecimal currentMonthIncome = incomeRepository.getIncomeForMonth(email, date.getYear(), date.getMonthValue())
                .orElseThrow(()->new NoReportFoundException("No report found for current month please insert your income"));

        BigDecimal totalExpenses = expensesRepository.getTotalExpenses(email, date.getYear(), date.getMonthValue());

        SavingsReport savings = new SavingsReport();
        double savingsValue = currentMonthIncome.doubleValue() - totalExpenses.doubleValue();
        savings.setTotal(savingsValue);

        if (currentMonthIncome.doubleValue() > 0) {
            savings.setPercentageOfIncome((savingsValue / currentMonthIncome.doubleValue()) * 100);
        } else {
            savings.setPercentageOfIncome(0.0);
        }

        return savings;
    }
}