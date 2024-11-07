package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findByRequestStatus(String status);
}
