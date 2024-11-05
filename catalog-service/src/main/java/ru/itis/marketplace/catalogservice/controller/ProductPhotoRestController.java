package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product_photo.NewProductPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/catalog/products")
public class ProductPhotoRestController {
    private final ProductPhotoService productPhotoService;


    @GetMapping("/{productId:\\d+}/photos")
    public List<ProductPhoto> findProductPhotos(@PathVariable Long productId) {
        return this.productPhotoService.findProductPhotos(productId);
    }

    @DeleteMapping("/{productId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteProductPhotosByIds(@PathVariable Long productId,
                                                         @PathVariable List<Long> photoIds) {
        this.productPhotoService.deleteProductPhotosByIds(productId, photoIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId:\\d+}/photos")
    public ResponseEntity<?> createProductPhoto(@Valid @RequestBody NewProductPhotoPayload payload,
                                           @PathVariable Long productId,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            ProductPhoto productPhoto = this.productPhotoService.createProductPhoto(
                    productId, payload.url(), payload.sequenceNumber()
            );
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/v1/catalog/product/{productId}/photos/{photoId}")
                            .build(Map.of(
                                    "productId", productPhoto.getProduct().getId(),
                                    "photoId", productPhoto.getId()))
                    ).body(productPhoto);
        }
    }
}
