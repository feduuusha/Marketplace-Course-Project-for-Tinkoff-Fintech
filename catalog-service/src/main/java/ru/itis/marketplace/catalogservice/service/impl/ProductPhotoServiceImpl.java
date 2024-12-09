package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.ProductPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPhotoServiceImpl implements ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;
    private final ProductRepository productRepository;
    private final MeterRegistry meterRegistry;

    @Override
    public List<ProductPhoto> findProductPhotos(Long productId) {
        productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID: " + productId + " not found"));
        return productPhotoRepository
                .findByProductId(productId, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
    }

    @Override
    public void deleteProductPhotosByIds(List<Long> photosIds) {
        productPhotoRepository.deleteAllByIdInBatch(photosIds);
    }

    @Override
    @Transactional
    public ProductPhoto createProductPhoto(Long productId, String url, Long sequenceNumber) {
        productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID: " + productId + " not found"));
        var productPhoto = productPhotoRepository.save(new ProductPhoto(url, sequenceNumber, productId));
        meterRegistry.counter("count of created product photos").increment();
        return productPhoto;
    }
}
