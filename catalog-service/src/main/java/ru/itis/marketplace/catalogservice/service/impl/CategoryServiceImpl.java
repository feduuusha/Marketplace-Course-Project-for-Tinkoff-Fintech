package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.service.CategoryService;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final KafkaProducer kafkaProducer;
    private final MeterRegistry meterRegistry;


    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with ID: " + id + " not found"));
    }

    @Override
    @Transactional
    public void updateCategoryById(Long id, String name) {
        Category category = findCategoryById(id);
        if (!category.getName().equals(name) && categoryRepository.findByName(name).isPresent())
            throw new BadRequestException("Category with name: " + name + " already exist");
        category.setName(name);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategoryById(Long id) {
        var products = productService.findAllProducts(null, null, null, null, null, null, null, null, id);
        var sizeIds = products.stream().flatMap(product -> product.getSizes().stream()).map(ProductSize::getId).toList();
        kafkaProducer.sendSizeIds(sizeIds);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Category createCategory(String name) {
        if (categoryRepository.findByName(name).isPresent())
            throw new BadRequestException("Category with name: " + name + " already exist");
        var category = categoryRepository.save(new Category(name));
        meterRegistry.counter("count of created categories").increment();
        return category;
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll(Sort.by("name"));
    }

    @Override
    public List<Category> findCategoryByNameLike(String name) {
        return categoryRepository.findByNameLikeIgnoreCase(name);
    }
}
