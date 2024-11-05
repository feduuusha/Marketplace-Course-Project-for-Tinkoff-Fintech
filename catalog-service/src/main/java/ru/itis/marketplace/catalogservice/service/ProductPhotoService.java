package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.ProductPhoto;

import java.util.List;

public interface ProductPhotoService {
    List<ProductPhoto> findProductPhotos(Long productId);

    void deleteProductPhotosByIds(Long productId, List<Long> photosIds);

    ProductPhoto createProductPhoto(Long productId, String url, Long sequenceNumber);
}
