package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.brand_photo.NewBrandPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandPhotoRestController {

    private final BrandPhotoService brandPhotoService;

    @GetMapping("/{brandId:\\d+}/photos")
    public List<BrandPhoto> findBrandPhotos(@PathVariable Long brandId) {
        return brandPhotoService.findBrandPhotos(brandId);
    }

    @DeleteMapping("/{brandId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteAllBrandPhotosById(@PathVariable(name = "brandId") Long ignoredBrandId,
                                                         @PathVariable List<Long> photoIds) {
        brandPhotoService.deleteAllBrandPhotosById(photoIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{brandId:\\d+}/photos")
    public ResponseEntity<BrandPhoto> createBrandPhoto(@PathVariable Long brandId,
                                              @Valid @RequestBody NewBrandPhotoPayload payload,
                                              UriComponentsBuilder uriComponentsBuilder) {
        BrandPhoto brandPhoto = brandPhotoService.createBrandPhoto(brandId, payload.url(), payload.sequenceNumber());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/brands/{brandId}/photos/{photoId}")
                        .build( brandPhoto.getBrandId(), brandPhoto.getId()))
                .body(brandPhoto);
    }
}
