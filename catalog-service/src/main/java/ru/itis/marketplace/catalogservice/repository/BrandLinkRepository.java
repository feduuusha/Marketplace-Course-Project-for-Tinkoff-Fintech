package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.BrandLink;

import java.util.List;

public interface BrandLinkRepository extends JpaRepository<BrandLink, Long> {
    List<BrandLink> findByBrandId(Long brandId);
}
