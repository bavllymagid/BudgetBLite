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
            "WHERE e.userEmail = :email AND e.isDeleted = false")
    BigDecimal getTotalExpenses(@Param("email") String email);

    //Expenses grouped by category
    @Query("SELECT c.name, COALESCE(SUM(e.amount), 0) " +
            "FROM Expenses e JOIN e.category c " +
            "WHERE e.userEmail = :email AND e.isDeleted = false " +
            "GROUP BY c.name ORDER BY SUM(e.amount) DESC")
    List<Object[]> getExpensesByCategory(@Param("email") String email);

    //Most used category (highest spending)
    @Query(value = "SELECT c.name " +
            "FROM expenses e JOIN categories c ON e.category_id = c.category_id " +
            "WHERE e.user_email = :email AND e.deleted = false " +
            "GROUP BY c.name ORDER BY SUM(e.amount) DESC LIMIT 1",
            nativeQuery = true)
    String getMostUsedCategory(@Param("email") String email);

    //Expenses grouped by month (for trend reporting)
    @Query("SELECT YEAR(e.date), MONTH(e.date), COALESCE(SUM(e.amount), 0) " +
            "FROM Expenses e WHERE e.userEmail = :email AND e.isDeleted = false " +
            "GROUP BY YEAR(e.date), MONTH(e.date) " +
            "ORDER BY YEAR(e.date), MONTH(e.date)")
    List<Object[]> getMonthlyExpenses(@Param("email") String email);

    //Expenses for a specific month
    @Query("SELECT COALESCE(SUM(e.amount), 0) " +
            "FROM Expenses e WHERE e.userEmail = :email AND e.isDeleted = false " +
            "AND YEAR(e.date) = :year AND MONTH(e.date) = :month")
    BigDecimal getMonthlyExpenses(@Param("email") String email,
                                  @Param("year") int year,
                                  @Param("month") int month);
}
