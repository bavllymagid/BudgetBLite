package com.budget.b.lite.repositories;

import com.budget.b.lite.entities.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Transactional
    @Modifying
    void deleteByName(String name);
    boolean existsByName(String name);
}
