package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ProductSize> findAllProductSizes(Long productId) {
        return this.productSizeRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public ProductSize createProductSize(Long productId, String name) {
        Optional<Product> optionalProduct = this.productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return this.productSizeRepository.save(new ProductSize(name, product));
        } else {
            throw new NoSuchElementException("Product with the specified ID was not found");
        }
    }

    @Override
    @Transactional
    public void deleteAllProductSizesById(Long brandId, List<Long> sizeIds) {
        this.productSizeRepository.deleteAllByIdInBatch(sizeIds);
    }
}
