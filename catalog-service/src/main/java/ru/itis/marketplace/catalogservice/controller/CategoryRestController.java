package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.category.NewCategoryPayload;
import ru.itis.marketplace.catalogservice.controller.payload.category.UpdateCategoryPayload;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.service.CategoryService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @GetMapping(path = "/{categoryId:\\d+}")
    public Category findCategoryById(@PathVariable Long categoryId) {
        return this.categoryService.findCategoryById(categoryId).orElseThrow(() -> new NoSuchElementException("Category with the specified ID was not found"));
    }

    @PutMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<?> updateCategoryById(@PathVariable Long categoryId,
                                                @RequestBody UpdateCategoryPayload payload,
                                                BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.categoryService.updateCategoryById(categoryId, payload.name());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        this.categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody NewCategoryPayload payload,
                                            BindingResult bindingResult,
                                            UriComponentsBuilder uriComponentsBuilder) throws BindException{
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Category category = this.categoryService.createCategory(payload.name());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/v1/catalog/categories/{categoryId}")
                            .build(Map.of("categoryId", category.getId())))
                    .body(category);
        }
    }

    @GetMapping
    public List<Category> findCategories() {
        return this.categoryService.findAllCategories();
    }
}
