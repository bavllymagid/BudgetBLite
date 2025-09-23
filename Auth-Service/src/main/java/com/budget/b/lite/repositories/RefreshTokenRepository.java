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
    @Modifying
    @Query("delete from RefreshToken rt where rt.user.email = ?1")
    void deleteByUserEmail(String email);
}
