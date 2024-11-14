package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.ProductSize;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
    List<ProductSize> findByProductId(Long productId);
    Optional<ProductSize> findByName(String name);
}
