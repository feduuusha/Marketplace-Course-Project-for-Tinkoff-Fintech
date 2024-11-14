package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.ProductSize;

import java.util.List;

public interface ProductSizeService {
    List<ProductSize> findAllProductSizes(Long productId);
    ProductSize createProductSize(Long productId, String name);
    void deleteAllProductSizesById(Long brandId, List<Long> sizeIds);
}
