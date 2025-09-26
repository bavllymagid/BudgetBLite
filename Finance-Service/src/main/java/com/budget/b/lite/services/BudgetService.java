package com.budget.b.lite.services;

import com.budget.b.lite.utils.dto.ExpensesRequest;
import com.budget.b.lite.utils.dto.IncomeRequest;
import com.budget.b.lite.entities.Category;
import com.budget.b.lite.entities.Expenses;
import com.budget.b.lite.entities.Income;
import com.budget.b.lite.repositories.ExpensesRepository;
import com.budget.b.lite.repositories.IncomeRepository;
import com.budget.b.lite.utils.exceptions.NoExpenseFoundException;
import com.budget.b.lite.utils.exceptions.NoIncomeFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BudgetService {

    private final ExpensesRepository expensesRepository;
    private final IncomeRepository incomeRepository;

    public BudgetService(ExpensesRepository expensesRepository, IncomeRepository incomeRepository) {
        this.expensesRepository = expensesRepository;
        this.incomeRepository = incomeRepository;
    }

    public Income addIncome(IncomeRequest request){
        Income income = incomeRepository.findRecentIncomeByUserEmail(request.userEmail(), LocalDate.now().minusDays(30))
                .orElse(null);

        if(income == null){
            income = new Income(request.userEmail(), LocalDate.now());
        }

        income.setAmount(request.amount());
        return incomeRepository.save(income);
    }

    public Expenses addExpenses(ExpensesRequest request){
       return expensesRepository.save(new Expenses(request.userEmail(),
                request.amount(),
                LocalDate.now(),
                new Category(request.categoryName())));
    }

    public Page<Expenses> getUserExpenses(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expensesRepository.findByUserEmail(email, pageable);
    }

    public Income getUserIncome(String email){
        return incomeRepository.findByUserEmail(email).orElseThrow(()-> new NoIncomeFoundException("No Income found for email: " + email));
    }

    public Expenses updateExpense(Long expenseId, ExpensesRequest request){
        return expensesRepository.findById(expenseId)
                .map(expense -> {
                    if(request.userEmail() != null && !request.userEmail().isEmpty())
                        expense.setUserEmail(request.userEmail());
                    if(request.amount() != null)
                        expense.setAmount(request.amount());
                    if(request.categoryName() != null && !request.categoryName().isEmpty())
                        expense.setCategory(new Category(request.categoryName()));
                    expense.setDate(LocalDate.now());

                    return expensesRepository.save(expense);
                })
                .orElseThrow(() -> new NoExpenseFoundException("Expense not found with id: " + expenseId));
    }

    public void deleteExpenseById(Long expenseId){
        expensesRepository.deleteById(expenseId);
    }
}
