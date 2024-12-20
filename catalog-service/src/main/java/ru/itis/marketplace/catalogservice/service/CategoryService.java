package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.Category;

import java.util.List;

public interface CategoryService {
    Category findCategoryById(Long id);
    void updateCategoryById(Long id,  String name);
    void deleteCategoryById(Long id);
    Category createCategory(String name);
    List<Category> findAllCategories();
    List<Category> findCategoryByNameLike(String name);
}
