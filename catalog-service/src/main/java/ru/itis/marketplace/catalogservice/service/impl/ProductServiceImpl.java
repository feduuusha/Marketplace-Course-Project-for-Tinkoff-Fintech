package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final KafkaProducer kafkaProducer;
    private final MeterRegistry meterRegistry;

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with ID: " + id + " not found"));
    }

    @Override
    @Transactional
    public void updateProductById(Long productId, String name, BigDecimal price, String description,
                                               String status, Long categoryId, Long brandId) {
        Product product = findProductById(productId);
        categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category with ID: " + categoryId + " not found"));
        brandRepository.findById(brandId).orElseThrow(() -> new BadRequestException("Brand with ID: " + brandId + " not found"));
        if (!product.getName().equals(name) && productRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Product with name: " + name + " already exist");
        }
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setRequestStatus(status.toLowerCase());
        if (!Objects.equals(product.getBrandId(), brandId)) {
            product.setBrandId(brandId);
            kafkaProducer.sendProductUpdateMessage(productId, brandId);
        }
        product.setCategoryId(categoryId);
        productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        var sizeIds = productSizeRepository.findByProductId(id).stream().map(ProductSize::getId).toList();
        kafkaProducer.sendSizeIds(sizeIds);
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> findAllProducts(Integer pageSize, Integer page, String sortBy, String direction, BigDecimal priceFrom, BigDecimal priceTo, String status, Long brandId, Long categoryId) {
        var dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = sortBy == null ? Sort.unsorted() : Sort.by(dir, sortBy);
        Pageable pageable = Pageable.unpaged(sort);
        if (pageSize != null && page != null) {
            pageable = PageRequest.of(page, pageSize, sort);
        }
        var specification = ProductRepository.buildProductSpecification(priceFrom, priceTo, status, brandId, categoryId);
        var products = productRepository.findAll(specification, pageable).toList();
        var productIds = products.stream().map(Product::getId).toList();
        productRepository.joinPhotosToProductWithIds(productIds);
        productRepository.joinSizesToBrandWithIds(productIds);
        return products;
    }

    @Override
    @Transactional
    public Product createProduct(String name, BigDecimal price, String description, Long categoryId, Long brandId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category with ID: " + categoryId + " not found"));
        brandRepository.findById(brandId).orElseThrow(() -> new BadRequestException("Brand with ID: " + brandId + " not found"));
        if (productRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Product with name: " + name + " already exist");
        }
        var product = productRepository.save(new Product(name, price, description, categoryId, brandId));
        meterRegistry.counter("count of created products").increment();
        return product;
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    @Transactional
    public List<Product> findProductsByNameLike(String name) {
        var products = productRepository.findByNameLikeIgnoreCase(name);
        var productIds = products.stream().map(Product::getId).toList();
        productRepository.joinPhotosToProductWithIds(productIds);
        productRepository.joinSizesToBrandWithIds(productIds);
        return products;
    }

    @Override
    public void updateProductStatusById(Long productId, String requestStatus) {
        Product product = findProductById(productId);
        product.setRequestStatus(requestStatus);
        productRepository.save(product);
    }

}
