package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    @Query("FROM Income i WHERE i.userEmail = :userEmail " +
            "AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    Optional<Income> findRecentIncomeByUserEmail(
            @Param("userEmail") String userEmail,
            @Param("year") int year,
            @Param("month") int month
    );

    Optional<Income> findByUserEmail(String email);
}
