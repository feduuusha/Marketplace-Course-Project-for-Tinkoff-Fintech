package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.brand_link.NewBrandLinkPayload;
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.service.BrandLinkService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandLinkRestController {

    private final BrandLinkService brandLinkService;

    @GetMapping("/{brandId:\\d+}/links")
    public List<BrandLink> findAllBrandLinks(@PathVariable Long brandId) {
        return brandLinkService.findAllBrandLinks(brandId);
    }

    @PostMapping("/{brandId:\\d+}/links")
    public ResponseEntity<BrandLink> createBrandLink(@PathVariable Long brandId,
                                                     @Valid @RequestBody NewBrandLinkPayload payload,
                                                     UriComponentsBuilder uriComponentsBuilder) {
        BrandLink brandLink = brandLinkService.createBrandLink(brandId, payload.url(), payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/brand/{brandId}/links/{linkId}")
                        .build(brandLink.getBrandId(), brandLink.getId()))
                .body(brandLink);
    }

    @DeleteMapping("/{brandId:\\d+}/links/{linkIds}")
    public ResponseEntity<Void> deleteAllBrandLinkById(@PathVariable Long brandId, @PathVariable List<Long> linkIds) {
        brandLinkService.deleteAllBrandLinkById(brandId, linkIds);
        return ResponseEntity.noContent().build();
    }
}
