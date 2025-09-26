package com.budget.b.lite.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
public class Expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;

    // No @ManyToOne(User) because user_id is external (different microservice)
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_expenses_category"))
    private Category category;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted")
    private boolean isDeleted;

    @PrePersist
    private void setTime(){
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    private void setUpdatedAt(){
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Expenses(String userEmail, BigDecimal amount, LocalDate date, Category category) {
        this.userEmail = userEmail;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }
}
