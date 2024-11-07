package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findProductById(Long id);
    void updateProductById(Long productId, String name, BigDecimal price, String description, String status, Long categoryId, Long brandId);
    void deleteProductById(Long id);
    List<Product> findAllProducts(int size, int page, String sortBy, String direction, String status);
    Product createProduct(String name, BigDecimal price, String description, Long categoryId, Long brandId);
    List<Product> findProductsByCategory(Long categoryId, int size, int page, String sortBy, String direction, String status);
    List<Product> findProductsByBrand(Long brandId, int size, int page, String sortBy, String direction, String status);
    List<Product> findProductsByIds(List<Long> productIds);
}
