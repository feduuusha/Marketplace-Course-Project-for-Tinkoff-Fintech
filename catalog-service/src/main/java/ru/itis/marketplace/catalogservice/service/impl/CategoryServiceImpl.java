package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.service.CategoryService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateCategoryById(Long id, String name) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(name);
            categoryRepository.save(category);
        } else {
            throw new NoSuchElementException("Category with the specified ID was not found");
        }
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Category createCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    @Override
    public List<Category> findAllCategories() {
        return this.categoryRepository.findAll();
    }
}
