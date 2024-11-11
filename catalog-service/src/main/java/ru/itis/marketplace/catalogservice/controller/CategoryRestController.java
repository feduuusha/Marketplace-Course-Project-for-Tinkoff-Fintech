package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.category.NewCategoryPayload;
import ru.itis.marketplace.catalogservice.controller.payload.category.UpdateCategoryPayload;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.service.CategoryService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping(path = "/{categoryId:\\d+}")
    public Category findCategoryById(@PathVariable Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }

    @PutMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Long categoryId,
                                                @Valid @RequestBody UpdateCategoryPayload payload) {
        categoryService.updateCategoryById(categoryId, payload.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody NewCategoryPayload payload,
                                            UriComponentsBuilder uriComponentsBuilder) {
        Category category = categoryService.createCategory(payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/categories/{categoryId}")
                        .build(category.getId()))
                .body(category);
    }

    @GetMapping
    public List<Category> findCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/search")
    public List<Category> findCategoriesByNameLike(@RequestParam String name) {
        return categoryService.findCategoryByNameLike(name);
    }
}
