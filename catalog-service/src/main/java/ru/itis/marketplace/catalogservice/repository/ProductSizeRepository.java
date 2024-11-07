package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.ProductSize;

import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductId(Long productId);
}
