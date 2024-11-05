package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.status.RequestStatus;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryIdAndRequestStatus(Long id, RequestStatus requestStatus, Pageable pageable);
    List<Product> findByCategoryIdAndRequestStatus(Long id, RequestStatus requestStatus);
    Page<Product> findByBrandIdAndRequestStatus(Long brandId, RequestStatus requestStatus, Pageable pageable);
    List<Product> findByBrandIdAndRequestStatus(Long brandId, RequestStatus requestStatus);
    List<Product> findByRequestStatus(RequestStatus status, Sort sort);
    Page<Product> findByRequestStatus(RequestStatus status, Pageable pageable);
}
