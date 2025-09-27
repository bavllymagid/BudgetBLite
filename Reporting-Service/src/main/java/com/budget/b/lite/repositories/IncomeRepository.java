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
    // Get Total Income For a specific user
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.userEmail = :email")
    BigDecimal getTotalIncome(@Param("email") String email);

    //Get monthly incomes (since only one record per month per user)
    @Query("SELECT YEAR(i.date), MONTH(i.date), i.amount " +
            "FROM Income i WHERE i.userEmail = :email ORDER BY i.date")
    List<Object[]> getMonthlyIncome(@Param("email") String email);

    //Get income for a specific month
    @Query("SELECT i FROM Income i WHERE i.userEmail = :email " +
            "AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    Optional<Income> getIncomeForMonth(@Param("email") String email,
                                       @Param("year") int year,
                                       @Param("month") int month);
}
