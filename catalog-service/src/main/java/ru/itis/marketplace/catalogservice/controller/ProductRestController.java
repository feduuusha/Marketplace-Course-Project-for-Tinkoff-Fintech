package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@Validated
@RestController
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @GetMapping("/{productId:\\d+}")
    public Product findProductById(@PathVariable Long productId) {
        return productService.findProductById(productId);
    }

    @PutMapping("/{productId:\\d+}")
    public ResponseEntity<Void> updateProductById(@PathVariable Long productId,
                                                  @Valid @RequestBody UpdateProductPayload payload) {
        productService.updateProductById(productId, payload.name(), payload.price(),
                payload.description(), payload.requestStatus(), payload.categoryId(), payload.brandId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId:\\d+}")
    public ResponseEntity<Void> updateProductStatusById(@PathVariable Long productId,
                                                        @Valid @RequestBody UpdateProductStatusPayload payload) {
        productService.updateProductStatusById(productId, payload.requestStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping( "/{productId:\\d+}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Product> findAllProducts(@RequestParam(required = false, name = "page-size") Integer pageSize,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false, name = "sort-by") String sortBy,
                                         @RequestParam(required = false) String direction,
                                         @RequestParam(required = false, name = "price-from") BigDecimal priceFrom,
                                         @RequestParam(required = false, name = "price-to") BigDecimal priceTo,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) Long brandId,
                                         @RequestParam(required = false) Long categoryId) {
        return productService.findAllProducts(pageSize, page, sortBy, direction,
                priceFrom, priceTo, status, brandId, categoryId);
    }

    @GetMapping("/by-ids/{productIds}")
    public List<Product> findProductsByIds(@PathVariable List<Long> productIds) {
        return productService.findProductsByIds(productIds);
    }

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

    @GetMapping("/search")
    public List<Product> findProductsByNameLike(@RequestParam String name) {
        return productService.findProductsByNameLike(name);
    }
}
