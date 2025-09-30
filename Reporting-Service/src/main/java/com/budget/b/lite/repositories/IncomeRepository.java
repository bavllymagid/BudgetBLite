package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    // Get income for a specific month
    @Query("SELECT COALESCE(i.amount, 0) FROM Income i WHERE i.userEmail = :email " +
            "AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    Optional<BigDecimal> getIncomeForMonth(@Param("email") String email,
                                 @Param("year") int year,
                                 @Param("month") int month);

    //Get monthly incomes (since only one record per month per user)
    @Query("FROM Income i WHERE i.userEmail = :email ORDER BY i.date desc limit 10")
    List<Income> getMonthlyIncomes(@Param("email") String email);

}
