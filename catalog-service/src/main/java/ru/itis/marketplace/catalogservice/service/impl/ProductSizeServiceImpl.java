package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ProductSize> findAllProductSizes(Long productId) {
        productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID: " + productId + " not found"));
        return productSizeRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ProductSize createProductSize(Long productId, String name) {
        productRepository
                .findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID: " + productId + " not found"));
        if (productSizeRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Product size with name: " + name + " already exist");
        }
        return productSizeRepository.save(new ProductSize(name, productId));
    }

    @Override
    public void deleteAllProductSizesById(Long brandId, List<Long> sizeIds) {
        productSizeRepository.deleteAllByIdInBatch(sizeIds);
    }
}
