package ru.itis.marketplace.catalogservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.brand.NewBrandPayload;
import ru.itis.marketplace.catalogservice.controller.payload.brand.UpdateBrandPayload;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.service.BrandService;

import java.util.List;

@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandRestController {

    private final BrandService brandService;

    @GetMapping(path = "/{brandId:\\d+}")
    public Brand findBrandById(@PathVariable Long brandId) {
        return this.brandService.findBrandById(brandId);
    }

    @PutMapping(path = "/{brandId:\\d+}")
    public ResponseEntity<Void> updateBrandById(@PathVariable Long brandId,
                                             @Valid @RequestBody UpdateBrandPayload payload) {
        this.brandService.updateBrandById(brandId, payload.name(), payload.description(), payload.linkToLogo(), payload.requestStatus());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{brandId:\\d+}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable Long brandId) {
        this.brandService.deleteBrandById(brandId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Brand> findBrands(@RequestParam(required = false) String status,
                                  @RequestParam(required = false) Integer pageSize,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return this.brandService.findAllBrands(status, pageSize, page, sortedBy);
    }

    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody NewBrandPayload payload,
                                         UriComponentsBuilder uriComponentsBuilder) {
        Brand brand = this.brandService.createBrand(payload.name(), payload.description(), payload.linkToLogo());
        return ResponseEntity
                .created(uriComponentsBuilder
                    .replacePath("api/v1/catalog/brands/{brandId}")
                    .build(brand.getId()))
                .body(brand);
    }

    @GetMapping("/by-ids/{brandIds}")
    public List<Brand> findAllBrandByIds(@PathVariable List<Long> brandIds) {
        return this.brandService.findAllBrandByIds(brandIds);
    }

    @GetMapping("/search")
    public List<Brand> findBrandsByNameLike(@RequestParam String name) {
        return this.brandService.findBrandsByNameLike(name);
    }
}
