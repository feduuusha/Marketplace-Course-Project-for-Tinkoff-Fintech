package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @Override
    public Optional<Product> findProductById(Long id) {
        return this.productRepository.findById(id);
    }

    @Override
    @Transactional
    public void updateProductById(Long productId, String name, BigDecimal price, String description,
                                               String status, Long categoryId, Long brandId) {
        Optional<Product> optionalProduct = this.productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Optional<Category> optionalCategory = this.categoryRepository.findById(categoryId);
            Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
            if (optionalCategory.isPresent() && optionalBrand.isPresent()) {
                Product product = optionalProduct.get();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setRequestStatus(status.toLowerCase());
                product.setCategory(optionalCategory.get());
                product.setBrand(optionalBrand.get());
                this.productRepository.save(product);
            } else {
                if (optionalCategory.isEmpty() && optionalBrand.isEmpty()) {
                    throw new EntityNotFoundException("Brand with the specified ID was not found and Category with the specified ID was not found");
                } else if (optionalBrand.isEmpty()) {
                    throw new EntityNotFoundException("Brand with the specified ID was not found");
                } else {
                    throw new EntityNotFoundException("Category with the specified ID was not found");
                }
            }
        } else {
            throw new NoSuchElementException("Product with the specified ID was not found");
        }
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        this.productRepository.deleteById(id);
    }

    @Override
    public List<Product> findAllProducts(int size, int page, String sortBy, String direction, String status) {
        if (size == 0 || page == -1) {
            return this.productRepository.findByRequestStatus(status.toLowerCase(), Sort.by(Sort.Direction.fromString(direction), sortBy));
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
            return this.productRepository.findByRequestStatus(status.toLowerCase(), pageable).toList();
        }
    }

    @Override
    @Transactional
    public Product createProduct(String name, BigDecimal price, String description, Long categoryId, Long brandId) {
        Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
        Optional<Category> optionalCategory = this.categoryRepository.findById(categoryId);
        if (optionalBrand.isPresent() && optionalCategory.isPresent()) {
            Brand brand = optionalBrand.get();
            Category category = optionalCategory.get();
            return this.productRepository.save(new Product(name, price, description, category, brand));
        } else {
            if (optionalCategory.isEmpty() && optionalBrand.isEmpty()) {
                throw new EntityNotFoundException("Brand with the specified ID was not found and Category with the specified ID was not found");
            } else if (optionalBrand.isEmpty()) {
                throw new EntityNotFoundException("Brand with the specified ID was not found");
            } else {
                throw new EntityNotFoundException("Category with the specified ID was not found");
            }
        }
    }

    @Override
    public List<Product> findProductsByCategory(Long categoryId, int size, int page, String sortBy, String direction, String status) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(categoryId);
        if (optionalCategory.isPresent()) {
            if (size != 0 && page != -1) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
                return this.productRepository.findByCategoryIdAndRequestStatus(optionalCategory.get().getId(),
                        status.toLowerCase(), pageable).toList();
            } else {
                return this.productRepository.findByCategoryIdAndRequestStatus(optionalCategory.get().getId(),
                        status.toLowerCase());
            }
        } else {
            throw new EntityNotFoundException("Category with the specified ID was not found");
        }
    }

    @Override
    public List<Product> findProductsByBrand(Long brandId, int size, int page, String sortBy, String direction, String status) {
        Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            if (size != 0 && page != -1) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
                return this.productRepository.findByBrandIdAndRequestStatus(optionalBrand.get().getId(),
                        status.toLowerCase(), pageable).toList();
            } else {
                return this.productRepository.findByBrandIdAndRequestStatus(optionalBrand.get().getId(),
                        status.toLowerCase());
            }
        } else {
            throw new EntityNotFoundException("Brand with the specified ID was not found");
        }
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return this.productRepository.findAllById(productIds);
    }

}
