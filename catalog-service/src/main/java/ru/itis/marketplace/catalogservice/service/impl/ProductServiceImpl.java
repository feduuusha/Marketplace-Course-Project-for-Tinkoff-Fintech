package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

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
        product.setCategoryId(categoryId);
        product.setBrandId(brandId);
        productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
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
        return productRepository.findAll(specification, pageable).toList();
    }

    @Override
    @Transactional
    public Product createProduct(String name, BigDecimal price, String description, Long categoryId, Long brandId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new BadRequestException("Category with ID: " + categoryId + " not found"));
        brandRepository.findById(brandId).orElseThrow(() -> new BadRequestException("Brand with ID: " + brandId + " not found"));
        if (productRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Product with name: " + name + " already exist");
        }
        return productRepository.save(new Product(name, price, description, categoryId, brandId));
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    public List<Product> findProductsByNameLike(String name) {
        return productRepository.findByNameLikeIgnoreCase(name);
    }

    @Override
    public void updateProductStatusById(Long productId, String requestStatus) {
        Product product = findProductById(productId);
        product.setRequestStatus(requestStatus);
        productRepository.save(product);
    }

}
