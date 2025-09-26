package com.budget.b.lite.services;

import com.budget.b.lite.entities.Category;
import com.budget.b.lite.repositories.CategoryRepository;
import com.budget.b.lite.utils.exceptions.custom_exceptions.CategoryExistsException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    // Add category
    public Category addCategory(String name) {
        name = name.toUpperCase();
        if (repository.existsByName(name)) {
            throw new CategoryExistsException("Category already exists: " + name);
        }
        return repository.save(new Category(name));
    }

    public Category getCategoryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + id));
    }


    @Transactional
    public void deleteCategory(Long id) {
        if (!repository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found: " + id);
        }
        repository.deleteById(id);
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

}
