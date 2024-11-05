package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.product_photo.NewProductPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandPhotoRestController {

    private final BrandPhotoService brandPhotoService;

    @GetMapping("/{brandId:\\d+}/photos")
    public List<BrandPhoto> findBrandPhotos(@PathVariable Long brandId) {
        return this.brandPhotoService.findBrandPhotos(brandId);
    }

    @DeleteMapping("/{brandId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteAllBrandPhotosById(@PathVariable Long brandId,
                                                         @PathVariable List<Long> photoIds) {
        this.brandPhotoService.deleteAllBrandPhotosById(brandId, photoIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{brandId:\\d+}/photos")
    public ResponseEntity<?> createBrandPhoto(@PathVariable Long brandId,
                                              @Valid @RequestBody NewProductPhotoPayload payload,
                                              BindingResult bindingResult,
                                              UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            BrandPhoto brandPhoto = this.brandPhotoService.createBrandPhoto(
                    brandId, payload.url(), payload.sequenceNumber());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/v1/catalog/brand/{brandId}/photos/{photoId}")
                            .build(Map.of("brandId", brandPhoto.getBrand().getId(), "photoId", brandPhoto.getId())))
                    .body(brandPhoto);
        }
    }
}
