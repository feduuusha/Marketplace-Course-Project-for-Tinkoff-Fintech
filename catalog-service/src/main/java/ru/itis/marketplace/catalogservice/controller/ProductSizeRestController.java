package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product_size.NewProductSizePayload;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductSizeRestController {

    private final ProductSizeService productSizeService;

    @GetMapping("/{productId:\\d+}/sizes")
    public List<ProductSize> findAllProductSizes(@PathVariable Long productId) {
        return productSizeService.findAllProductSizes(productId);
    }

    @PostMapping("/{productId:\\d+}/sizes")
    public ResponseEntity<ProductSize> createProductSize(@PathVariable Long productId,
                                               @Valid @RequestBody NewProductSizePayload payload,
                                               UriComponentsBuilder uriComponentsBuilder) {
        ProductSize productSize = productSizeService.createProductSize(productId, payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/products/{productId}/sizes/{sizeId}")
                        .build(productSize.getProductId(),  productSize.getId()))
                .body(productSize);
    }

    @DeleteMapping("/{productId:\\d+}/sizes/{sizeIds}")
    public ResponseEntity<Void> deleteAllProductSizesById(@PathVariable Long productId,
                                                          @PathVariable List<Long> sizeIds) {
        productSizeService.deleteAllProductSizesById(productId, sizeIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId:\\d+}/sizes/{sizeId}")
    public ResponseEntity<ProductSize> findSizeByIdAndProductId(@PathVariable Long productId,
                                                                @PathVariable Long sizeId) {
        return ResponseEntity.ok(productSizeService.findSizeByIdAndProductId(productId, sizeId));
    }


}
