package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Expenses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    Page<Expenses> findByUserEmail(String email, Pageable pageable);
}
