package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    Brand findBrandById(Long id);
    void updateBrandById(Long brandId, String name, String description, String linkToLogo, String status);
    void deleteBrandById(Long id);
    List<Brand> findAllBrands(String status);
    Brand createBrand(String name, String description, String linkToLogo);
    List<Brand> findAllBrandByIds(List<Long> brandIds);
}
