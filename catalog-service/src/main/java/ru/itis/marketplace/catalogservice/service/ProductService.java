package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product findProductById(Long id);
    void updateProductById(Long productId, String name, BigDecimal price, String description, String status, Long categoryId, Long brandId);
    void deleteProductById(Long id);
    List<Product> findAllProducts(Integer pageSize, Integer page, String sortBy, String direction, BigDecimal priceFrom, BigDecimal priceTo, String status, Long brandId, Long categoryId);
    Product createProduct(String name, BigDecimal price, String description, Long categoryId, Long brandId);
    List<Product> findProductsByIds(List<Long> productIds);
    List<Product> findProductsByNameLike(String name);
    void updateProductStatusById(Long productId, String requestStatus);
}
