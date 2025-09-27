package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    // Get current month's income
    @Query("SELECT COALESCE(i.amount, 0) FROM Income i WHERE i.userEmail = :email " +
            "AND YEAR(i.date) = YEAR(CURRENT_DATE) AND MONTH(i.date) = MONTH(CURRENT_DATE)")
    BigDecimal getCurrentMonthIncome(@Param("email") String email);

    // Get income for a specific month
    @Query("SELECT COALESCE(i.amount, 0) FROM Income i WHERE i.userEmail = :email " +
            "AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    BigDecimal getIncomeForMonth(@Param("email") String email,
                                 @Param("year") int year,
                                 @Param("month") int month);

    // 2. Get monthly incomes (since only one record per month per user)
    @Query("SELECT YEAR(i.date), MONTH(i.date), i.amount " +
            "FROM Income i WHERE i.userEmail = :email ORDER BY i.date")
    List<Object[]> getMonthlyIncome(@Param("email") String email);

    // Get the full Income entity for a specific month (if needed elsewhere)
    @Query("SELECT i FROM Income i WHERE i.userEmail = :email " +
            "AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    Optional<Income> getIncomeEntityForMonth(@Param("email") String email,
                                             @Param("year") int year,
                                             @Param("month") int month);
}
