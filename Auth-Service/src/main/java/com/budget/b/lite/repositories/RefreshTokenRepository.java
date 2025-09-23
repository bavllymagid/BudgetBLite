package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.RefreshToken;
import com.budget.b.lite.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);

    @Transactional
    @Query("select rt from RefreshToken rt join fetch rt.user u where u.email = :email")
    void deleteByUserEmail(String email);
}
