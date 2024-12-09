package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product_photo.NewProductPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/catalog/products")
public class ProductPhotoRestController {

    private final ProductPhotoService productPhotoService;

    @GetMapping("/{productId:\\d+}/photos")
    public List<ProductPhoto> findProductPhotos(@PathVariable Long productId) {
        return productPhotoService.findProductPhotos(productId);
    }

    @DeleteMapping("/{productId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteProductPhotosByIds(@PathVariable(name = "productId") Long ignoredProductId,
                                                         @PathVariable List<Long> photoIds) {
        productPhotoService.deleteProductPhotosByIds(photoIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId:\\d+}/photos")
    public ResponseEntity<ProductPhoto> createProductPhoto(@Valid @RequestBody NewProductPhotoPayload payload,
                                           @PathVariable Long productId,
                                           UriComponentsBuilder uriComponentsBuilder) {
        ProductPhoto productPhoto = productPhotoService
                .createProductPhoto(productId, payload.url(), payload.sequenceNumber());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/product/{productId}/photos/{photoId}")
                        .build(productPhoto.getProductId(), productPhoto.getId()))
                .body(productPhoto);
    }
}
