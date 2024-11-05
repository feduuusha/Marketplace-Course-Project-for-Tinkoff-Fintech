package ru.itis.marketplace.catalogservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;

import java.util.List;

public interface BrandPhotoRepository extends JpaRepository<BrandPhoto, Long> {
    List<BrandPhoto> findAllByBrandId(Long brandId, Sort sort);
}
