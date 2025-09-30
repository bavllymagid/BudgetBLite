package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    //Total expenses for a user
    @Query("SELECT COALESCE(SUM(e.amount), 0) " +
            "FROM Expenses e " +
            "WHERE e.userEmail = :email " +
            "AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    BigDecimal getTotalExpenses(@Param("email") String email,
                                @Param("year") int year,
                                @Param("month") int month);

    //Expenses grouped by category
    @Query(value = "SELECT c.name, COALESCE(SUM(e.amount), 0) " +
            "FROM Expenses e JOIN e.category c " +
            "WHERE e.userEmail = :email AND YEAR(e.date) = :year AND MONTH(e.date) = :month " +
            "GROUP BY c.name ORDER BY SUM(e.amount) DESC")
    List<Object[]> getExpensesByCategory(@Param("email") String email,
                                         @Param("year") int year,
                                         @Param("month") int month);

    //Most used category (highest spending)
    @Query("SELECT c.name " +
            "FROM Expenses e JOIN e.category c " +
            "WHERE e.userEmail = :email " +
            "AND YEAR(e.date) = :year AND MONTH(e.date) = :month " +
            "GROUP BY c.name " +
            "ORDER BY SUM(e.amount) DESC " +
            "LIMIT 1")
    String getMostUsedCategory(@Param("email") String email,
                               @Param("year") int year,
                               @Param("month") int month);

}
