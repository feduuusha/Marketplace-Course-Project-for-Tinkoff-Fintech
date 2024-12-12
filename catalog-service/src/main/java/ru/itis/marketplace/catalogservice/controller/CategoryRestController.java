package ru.itis.marketplace.catalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.category.NewCategoryPayload;
import ru.itis.marketplace.catalogservice.controller.payload.category.UpdateCategoryPayload;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.service.CategoryService;

import java.util.List;

@Tag(name = "Category Rest Controller", description = "CRUD operations for category entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Endpoint for getting category by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with category", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                    @ApiResponse(description = "Category not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping(path = "/{categoryId:\\d+}")
    public Category findCategoryById(@PathVariable Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }

    @Operation(
            summary = "Endpoint for updating category by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, category updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or category with specified name already exist", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Category not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<Void> updateCategoryById(@PathVariable Long categoryId,
                                                @Valid @RequestBody UpdateCategoryPayload payload) {
        categoryService.updateCategoryById(categoryId, payload.name());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for deleting category by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when category is deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping(path = "/{categoryId:\\d+}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for creating category, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created category", headers = @Header(name = "Location", description = "URL of the created Category"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or category with specified name already exist", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody NewCategoryPayload payload,
                                            UriComponentsBuilder uriComponentsBuilder) {
        Category category = categoryService.createCategory(payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/categories/{categoryId}")
                        .build(category.getId()))
                .body(category);
    }

    @Operation(
            summary = "Endpoint for getting all categories, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with categories", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping
    public List<Category> findCategories() {
        return categoryService.findAllCategories();
    }

    @Operation(
            summary = "Endpoint for getting categories by an inaccurate name match, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with categories", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Category.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/search")
    public List<Category> findCategoriesByNameLike(@RequestParam String name) {
        return categoryService.findCategoryByNameLike(name);
    }
}
