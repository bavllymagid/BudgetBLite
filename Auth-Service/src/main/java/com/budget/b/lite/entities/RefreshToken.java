package com.budget.b.lite.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private Timestamp expiryDate;

    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;


    public RefreshToken(User user, String token, Timestamp expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    @PrePersist
    private void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

}

