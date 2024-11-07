package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product_size.NewProductSizePayload;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductSizeRestController {

    private final ProductSizeService productSizeService;

    @GetMapping("/{productId:\\d+}/sizes")
    public List<ProductSize> findAllProductSizes(@PathVariable Long productId) {
        return this.productSizeService.findAllProductSizes(productId);
    }

    @PostMapping("/{productId:\\d+}/sizes")
    public ResponseEntity<?> createProductSize(@PathVariable Long productId,
                                               @Valid @RequestBody NewProductSizePayload payload,
                                               BindingResult bindingResult,
                                               UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            ProductSize productSize = this.productSizeService.createProductSize(productId, payload.name());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/v1/catalog/products/{productId}/sizes/{sizeId}")
                            .build(Map.of("productId", productSize.getProduct().getId(), "sizeId", productSize.getId())))
                    .body(productSize);
        }
    }

    @DeleteMapping("/{productId:\\d+}/sizes/{sizeIds}")
    public ResponseEntity<Void> deleteAllProductSizesById(@PathVariable Long productId,
                                                          @PathVariable List<Long> sizeIds) {
        this.productSizeService.deleteAllProductSizesById(productId, sizeIds);
        return ResponseEntity.noContent().build();
    }


}
