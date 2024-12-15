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
import ru.itis.marketplace.catalogservice.controller.payload.product.NewProductPayload;
import ru.itis.marketplace.catalogservice.controller.payload.product.UpdateProductPayload;
import ru.itis.marketplace.catalogservice.controller.payload.product.UpdateProductStatusPayload;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Product Rest Controller", description = "CRUD operations for product entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @Operation(
            summary = "Endpoint for getting product by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with product", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{productId:\\d+}")
    public Product findProductById(@PathVariable Long productId) {
        return productService.findProductById(productId);
    }

    @Operation(
            summary = "Endpoint for fully updating product by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, product updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or product with specified name already exist or product category not found or product brand not found", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/{productId:\\d+}")
    public ResponseEntity<Void> updateProductById(@PathVariable Long productId,
                                                  @Valid @RequestBody UpdateProductPayload payload) {
        productService.updateProductById(productId, payload.name(), payload.price(),
                payload.description(), payload.requestStatus(), payload.categoryId(), payload.brandId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for updating product status by product ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, product status updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PatchMapping("/{productId:\\d+}")
    public ResponseEntity<Void> updateProductStatusById(@PathVariable Long productId,
                                                        @Valid @RequestBody UpdateProductStatusPayload payload) {
        productService.updateProductStatusById(productId, payload.requestStatus());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for deleting product by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when product is deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping( "/{productId:\\d+}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for getting all products, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with products", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping
    public List<Product> findAllProducts(@RequestParam(required = false, name = "page-size") Integer pageSize,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false, name = "sort-by") String sortBy,
                                         @RequestParam(required = false) String direction,
                                         @RequestParam(required = false, name = "price-from") BigDecimal priceFrom,
                                         @RequestParam(required = false, name = "price-to") BigDecimal priceTo,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false, name = "brand-id") Long brandId,
                                         @RequestParam(required = false, name = "category-id") Long categoryId) {
        return productService.findAllProducts(pageSize, page, sortBy, direction,
                priceFrom, priceTo, status, brandId, categoryId);
    }

    @Operation(
            summary = "Endpoint for getting products by IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with products", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/by-ids/{productIds}")
    public List<Product> findProductsByIds(@PathVariable List<Long> productIds) {
        return productService.findProductsByIds(productIds);
    }

    @Operation(
            summary = "Endpoint for creating product, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created product", headers = @Header(name = "Location", description = "URL of the created Product"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or product with specified name already exist or product category not found or product brand not found", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody NewProductPayload payload,
                                                 UriComponentsBuilder uriComponentsBuilder) {
        Product product = productService.createProduct(payload.name(), payload.price(),
                payload.description(), payload.categoryId(), payload.brandId());
        return ResponseEntity
                .created(uriComponentsBuilder
                    .replacePath("api/v1/catalog/products/{productId}")
                    .build(product.getId()))
                .body(product);
    }

    @Operation(
            summary = "Endpoint for getting products by an inaccurate name match, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with products", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Product.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/search")
    public List<Product> findProductsByNameLike(@RequestParam String name) {
        return productService.findProductsByNameLike(name);
    }
}
