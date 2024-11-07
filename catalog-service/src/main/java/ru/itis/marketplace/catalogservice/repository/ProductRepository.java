package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryIdAndRequestStatus(Long id, String requestStatus, Pageable pageable);
    List<Product> findByCategoryIdAndRequestStatus(Long id, String requestStatus);
    Page<Product> findByBrandIdAndRequestStatus(Long brandId, String requestStatus, Pageable pageable);
    List<Product> findByBrandIdAndRequestStatus(Long brandId, String requestStatus);
    List<Product> findByRequestStatus(String status, Sort sort);
    Page<Product> findByRequestStatus(String status, Pageable pageable);
}
