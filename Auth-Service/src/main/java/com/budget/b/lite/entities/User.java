package com.budget.b.lite.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "jwt_secret", length = 512)
    private String jwtSecret;

    @Column(nullable = false, name = "created_at")
    private Timestamp createdAt;

    @Column(nullable = false, name = "updated_at")
    private Timestamp updatedAt;


    public User(String username, String email, String password, String jwtSecret) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.jwtSecret = jwtSecret;
    }

    @PrePersist
    private void setDates(){
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

}
