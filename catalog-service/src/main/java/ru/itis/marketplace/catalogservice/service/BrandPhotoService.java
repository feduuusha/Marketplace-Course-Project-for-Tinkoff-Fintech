package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.BrandPhoto;

import java.util.List;

public interface BrandPhotoService {
    List<BrandPhoto> findBrandPhotos(Long brandId);

    void deleteAllBrandPhotosById(Long brandId, List<Long> photoIds);

    BrandPhoto createBrandPhoto(Long brandId, String url, Long sequenceNumber);
}
