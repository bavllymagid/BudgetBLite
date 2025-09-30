package com.budget.b.lite.services;

import com.budget.b.lite.dto.ExpenseChange;
import com.budget.b.lite.dto.FinanceEvent;
import com.budget.b.lite.dto.constants.EventAction;
import com.budget.b.lite.entities.Category;
import com.budget.b.lite.entities.Expenses;
import com.budget.b.lite.exceptions.custom_exceptions.CategoryNotFoundException;
import com.budget.b.lite.exceptions.custom_exceptions.ExpenseChangeEmptyException;
import com.budget.b.lite.exceptions.custom_exceptions.NoExpenseFoundException;
import com.budget.b.lite.payload.ReportResponse;
import com.budget.b.lite.payload.reports.ExpensesReport;
import com.budget.b.lite.payload.reports.IncomeReport;
import com.budget.b.lite.payload.reports.SavingsReport;
import com.budget.b.lite.repositories.CategoryRepository;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
public class ReportService {

    private final DBRetrieveReportService retrieveReportService;
    private final CacheService cacheService;
    private final ExpensesRepository expensesRepository;

    public ReportService(DBRetrieveReportService retrieveReportService,
                         CacheService cacheService,
                         ExpensesRepository expensesRepository) {
        this.retrieveReportService = retrieveReportService;
        this.cacheService = cacheService;
        this.expensesRepository = expensesRepository;
    }

    public ReportResponse getReport(String email){
        ReportResponse cache = cacheService.getCache(email);
        if(cache == null){
            cache = retrieveReportService.generateReport(email);
        }
        return cache;
    }


    @KafkaListener(topics = "${host.topic}",
            groupId = "${spring.kafka.consumer.group-id:budget-report-service}")
    @Transactional
    public void handleFinanceEvent(FinanceEvent event) {
        log.info("Received finance event: {}", event);

        try {
            ReportResponse cache = cacheService.getCache(event.getUserId());
            if(cache == null){
                retrieveReportService.generateReport(event.getUserId());
                return;
            }
            switch (event.getEntityType()) {
                case INCOME -> handleIncomeEvent(event, cache);
                case EXPENSES -> handleExpenseEvent(event, cache);
                default -> log.warn("Unknown entity type: {}", event.getEntityType());
            }

        } catch (Exception e) {
            log.error("Error processing finance event: {}", event, e);
            throw e;
        }
    }

    private void handleIncomeEvent(FinanceEvent event, ReportResponse cache) {
        IncomeReport report = retrieveReportService.buildIncome(event.getUserId());
        SavingsReport savingsReport = retrieveReportService.buildSavings(event.getUserId());
        cache.setIncome(report);
        cache.setSavings(savingsReport);
        cacheService.AddCache(cache);
    }

    private void handleExpenseEvent(FinanceEvent event, ReportResponse cache) {
        if(event.getAction().equals(EventAction.DELETED)){
            expensesRepository.deleteAllMarkedAsDeleted();
        }
        ExpensesReport report = retrieveReportService.buildExpenses(event.getUserId());
        SavingsReport savingsReport = retrieveReportService.buildSavings(event.getUserId());
        cache.setExpenses(report);
        cache.setSavings(savingsReport);
        cacheService.AddCache(cache);
    }
}