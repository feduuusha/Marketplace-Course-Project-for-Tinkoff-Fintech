package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final ProductRepository productRepository;
    private final KafkaProducer kafkaProducer;
    private final MeterRegistry meterRegistry;

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
        var sizes = productSizeRepository.findByProductId(productId);
        if (sizes.stream().anyMatch(productSize -> productSize.getName().equals(name))) {
            throw new BadRequestException("Product size with name: " + name + " already exist");
        }
        var productSize = productSizeRepository.save(new ProductSize(name, productId));
        meterRegistry.counter("count of created product sizes").increment();
        return productSize;
    }

    @Override
    public void deleteAllProductSizesById(Long brandId, List<Long> sizeIds) {
        kafkaProducer.sendSizeIds(sizeIds);
        productSizeRepository.deleteAllByIdInBatch(sizeIds);
    }

    @Override
    public ProductSize findSizeByIdAndProductId(Long productId, Long sizeId) {
        ProductSize size = productSizeRepository
                .findById(sizeId)
                .orElseThrow(() -> new NotFoundException("Size with ID: " + sizeId + " not found"));
        if (!productId.equals(size.getProductId())) {
            throw new BadRequestException("Size with ID: " + sizeId + " belongs to Product with ID: " + size.getProductId());
        }
        return size;
    }
}
