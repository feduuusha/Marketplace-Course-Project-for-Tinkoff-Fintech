package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.repository.ProductPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductPhotoServiceImpl implements ProductPhotoService {

    private final ProductPhotoRepository productPhotoRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ProductPhoto> findProductPhotos(Long productId) {
        return this.productPhotoRepository.findByProductId(productId,
                Sort.by(Sort.Direction.ASC, "sequenceNumber"));
    }

    @Override
    @Transactional
    public void deleteProductPhotosByIds(Long productId, List<Long> photosIds) {
        this.productPhotoRepository.deleteAllByIdInBatch(photosIds);
    }

    @Override
    @Transactional
    public ProductPhoto createProductPhoto(Long productId, String url, Long sequenceNumber) {
        Optional<Product> optionalProduct = this.productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return this.productPhotoRepository.save(new ProductPhoto(url, sequenceNumber, product));
        } else {
            throw new NoSuchElementException("Product with the ID=" + productId + " was not found");
        }
    }
}
