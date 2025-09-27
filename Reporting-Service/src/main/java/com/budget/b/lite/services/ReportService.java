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

    private final IncomeRepository incomeRepository;
    private final ExpensesRepository expensesRepository;
    private final CategoryRepository categoryRepository;
    private final CacheService cacheService;

    public ReportService(IncomeRepository incomeRepository,
                         ExpensesRepository expensesRepository,
                         CategoryRepository categoryRepository,
                         CacheService cacheService) {
        this.incomeRepository = incomeRepository;
        this.expensesRepository = expensesRepository;
        this.categoryRepository = categoryRepository;
        this.cacheService = cacheService;
    }

    @KafkaListener(topics = "${host.topic}",
            groupId = "${spring.kafka.consumer.group-id:budget-report-service}")
    @Transactional
    public void handleFinanceEvent(FinanceEvent event) {
        log.info("Received finance event: {}", event);

        try {
            switch (event.getEntityType()) {
                case INCOME -> handleIncomeEvent(event);
                case EXPENSES -> handleExpenseEvent(event);
                default -> log.warn("Unknown entity type: {}", event.getEntityType());
            }

        } catch (Exception e) {
            log.error("Error processing finance event: {}", event, e);
            throw e;
        }
    }

    private void handleIncomeEvent(FinanceEvent event) {

    }

    private void handleExpenseEvent(FinanceEvent event) {
        switch (event.getAction()) {
            case CREATED -> handleExpenseCreated(event);
            case UPDATED -> handleExpenseUpdated(event);
            case DELETED -> handleExpenseDeleted(event);
            default -> log.warn("Unknown action for expense: {}", event.getAction());
        }
    }

    private void handleExpenseCreated(FinanceEvent event) {


    }

    private void handleExpenseUpdated(FinanceEvent event) {


    }

    private void handleExpenseDeleted(FinanceEvent event) {

    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("No category found with id: " + id));
    }
}