package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;

import java.util.List;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {

    List<ProductPhoto> findByProductId(Long productId, Sort sort);
}
