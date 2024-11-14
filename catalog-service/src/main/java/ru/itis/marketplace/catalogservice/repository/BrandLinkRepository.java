package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.BrandLink;

import java.util.List;
import java.util.Optional;

public interface BrandLinkRepository extends JpaRepository<BrandLink, Long> {
    List<BrandLink> findByBrandId(Long brandId);
    Optional<BrandLink> findByName(String name);
}
