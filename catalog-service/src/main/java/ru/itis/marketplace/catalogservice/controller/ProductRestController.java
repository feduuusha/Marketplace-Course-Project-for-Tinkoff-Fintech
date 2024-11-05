package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product.NewProductPayload;
import ru.itis.marketplace.catalogservice.controller.payload.product.UpdateProductPayload;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController()
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;

    @GetMapping("/{productId:\\d+}")
    public Product findProductById(@PathVariable Long productId) {
        return productService.findProductById(productId).orElseThrow(() -> new NoSuchElementException("Product with the specified ID was not found"));
    }

    @PutMapping("/{productId:\\d+}")
    public ResponseEntity<Void> updateProductById(@PathVariable Long productId,
                                               @Valid @RequestBody UpdateProductPayload payload,
                                               BindingResult bindingResult) throws BindException{
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            productService.updateProductById(productId, payload.name(), payload.price(),
                    payload.description(), payload.requestStatus(), payload.categoryId(), payload.brandId());
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping( "/{productId:\\d+}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long productId) {
        this.productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Product> findProducts(@RequestParam(required = false, defaultValue = "0") final int size,
                                      @RequestParam(required = false, defaultValue = "-1") final int page,
                                      @RequestParam(required = false, defaultValue = "name") final String sort,
                                      @RequestParam(required = false, defaultValue = "asc") final String direction,
                                      @RequestParam final String status) {
        return this.productService.findAllProducts(size, page, sort, direction, status);
    }

    @GetMapping("/by-ids/{productIds}")
    public List<Product> findProductsByIds(@PathVariable List<Long> productIds) {
        return this.productService.findProductsByIds(productIds);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody NewProductPayload payload,
                                                 BindingResult bindingResult,
                                                 UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            Product product = this.productService.createProduct(payload.name(), payload.price(),
                    payload.description(), payload.categoryId(), payload.brandId());
            return ResponseEntity
                    .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/products/{productId}")
                        .build(Map.of("productId", product.getId())))
                    .body(product);
        }
    }

    @GetMapping( "/by-category/{categoryId:\\d+}")
    public List<Product> findProductsByCategory(@PathVariable Long categoryId,
                                                    @RequestParam(required = false, defaultValue = "0") final int size,
                                                    @RequestParam(required = false, defaultValue = "-1") final int page,
                                                    @RequestParam(required = false, defaultValue = "name") final String sort,
                                                    @RequestParam(required = false, defaultValue = "asc") final String direction,
                                                    @RequestParam final String status) {
        return this.productService.findProductsByCategory(categoryId, size, page, sort, direction, status);
    }

    @GetMapping( "/by-brand/{brandId:\\d+}")
    public List<Product> findProductsByBrand(@PathVariable Long brandId,
                                                @RequestParam(required = false, defaultValue = "0") final int size,
                                                @RequestParam(required = false, defaultValue = "-1") final int page,
                                                @RequestParam(required = false, defaultValue = "name") final String sort,
                                                @RequestParam(required = false, defaultValue = "asc") final String direction,
                                                @RequestParam final String status) {
        return this.productService.findProductsByBrand(brandId, size, page, sort, direction, status);
    }
}
