package com.budget.b.lite.services;

import com.budget.b.lite.dto.ExpenseChange;
import com.budget.b.lite.dto.FinanceEvent;
import com.budget.b.lite.dto.constants.EntityType;
import com.budget.b.lite.dto.constants.EventAction;
import com.budget.b.lite.utils.dto.ExpensesDTO;
import com.budget.b.lite.utils.dto.ExpensesRequest;
import com.budget.b.lite.utils.dto.IncomeRequest;
import com.budget.b.lite.entities.Expenses;
import com.budget.b.lite.entities.Income;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import com.budget.b.lite.utils.exceptions.custom_exceptions.CategoryNotFoundException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.ExpenseAlreadyDeletedException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.NoExpenseFoundException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.NoIncomeFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BudgetService {

    private final ExpensesRepository expensesRepository;
    private final IncomeRepository incomeRepository;
    private final CategoryService categoryService;
    private final EventProducerService producerService;

    @Value("${host.topic}")
    private String topicName;

    public BudgetService(ExpensesRepository expensesRepository,
                         IncomeRepository incomeRepository,
                         CategoryService categoryService,
                         EventProducerService producerService) {
        this.expensesRepository = expensesRepository;
        this.incomeRepository = incomeRepository;
        this.categoryService = categoryService;
        this.producerService = producerService;
    }

    public Income addIncome(IncomeRequest request) {
        Income income = incomeRepository
                .findRecentIncomeByUserEmail(request.userEmail(), LocalDate.now().minusDays(30))
                .orElse(new Income(request.userEmail(), LocalDate.now()));

        income.setAmount(request.amount());
        Income saved = incomeRepository.save(income);

        sendFinanceEvent(saved.getUserEmail(), EntityType.INCOME, EventAction.UPDATED, null, null);

        return saved;
    }

    public ExpensesDTO addExpenses(ExpensesRequest request) {
        var category = categoryService.getCategoryById(request.categoryId());
        if (category == null) {
            throw new CategoryNotFoundException("Category not found with id: " + request.categoryId());
        }

        Expenses saved = expensesRepository.save(
                new Expenses(request.userEmail(), request.amount(), LocalDate.now(), category)
        );

        sendFinanceEvent(saved.getUserEmail(), EntityType.EXPENSES, EventAction.CREATED, null, null);

        return mapToDTO(saved);
    }

    public Page<ExpensesDTO> getUserExpenses(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expensesRepository.findByUserEmailOrderByDateDesc(email, pageable)
                .map(this::mapToDTO);
    }

    public Income getUserIncome(String email) {
        return incomeRepository.findByUserEmail(email)
                .orElseThrow(() -> new NoIncomeFoundException("No Income found for email: " + email));
    }

    public ExpensesDTO updateExpense(Long expenseId, ExpensesRequest request) {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new NoExpenseFoundException("Expense not found with id: " + expenseId));
        ExpenseChange change = new ExpenseChange();
        if (request.userEmail() != null && !request.userEmail().isEmpty()) {
            expense.setUserEmail(request.userEmail());
        }
        if (request.amount() != null) {
            change.setOldAmount(expense.getAmount());
            expense.setAmount(request.amount());
            change.setNewAmount(request.amount());
        }
        if (request.categoryId() != null) {
            var category = categoryService.getCategoryById(request.categoryId());
            if (category == null) {
                throw new CategoryNotFoundException("Category not found with id: " + request.categoryId());
            }
            change.setOldCategory(expense.getCategory().getId());
            expense.setCategory(category);
            change.setNewCategory(request.categoryId());
        }
        expense.setDate(LocalDate.now());

        Expenses saved = expensesRepository.save(expense);

        sendFinanceEvent(saved.getUserEmail(), EntityType.EXPENSES, EventAction.UPDATED, change, expenseId);

        return mapToDTO(saved);
    }

    public void deleteExpenseById(Long expenseId) {
        Expenses expense = expensesRepository.findById(expenseId)
                .orElseThrow(() -> new NoExpenseFoundException("Expense not found with id: " + expenseId));

        if (expense.isDeleted()) {
            throw new ExpenseAlreadyDeletedException("Expense already deleted with id: " + expenseId);
        }

        expense.setDeleted(true);
        expensesRepository.save(expense);

        sendFinanceEvent(expense.getUserEmail(), EntityType.EXPENSES, EventAction.DELETED,null, expenseId);
    }

    private void sendFinanceEvent(String userEmail, EntityType type, EventAction action, ExpenseChange change, Long expenseId) {
        FinanceEvent event = new FinanceEvent(
                userEmail,
                type,
                action,
                LocalDateTime.now()
        );
        event.setExpenseChange(change);
        event.setExpenseId(expenseId);
        producerService.sendEvent(topicName, event);
    }

    private ExpensesDTO mapToDTO(Expenses expense) {
        return new ExpensesDTO(
                expense.getId(),
                expense.getUserEmail(),
                expense.getAmount(),
                expense.getDate(),
                expense.getCategory() != null ? expense.getCategory().getName() : null
        );
    }
}
