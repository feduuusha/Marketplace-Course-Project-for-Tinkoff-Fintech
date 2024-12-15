package ru.itis.marketplace.catalogservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.ProductPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.service.impl.ProductPhotoServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductPhotoServiceImpl.class})
@ActiveProfiles("test")
class ProductPhotoServiceTests {

    @Autowired
    private ProductPhotoServiceImpl productPhotoService;

    @MockBean
    private ProductPhotoRepository productPhotoRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findProductPhotos should return list of product photos, because productId is correct")
    void findProductPhotosSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        Product product = new Product("name", BigDecimal.valueOf(100), "desc", 1L, 1L);
        List<ProductPhoto> expectedProductPhotos = List.of(
                new ProductPhoto("url1", 1L, productId),
                new ProductPhoto("url2", 2L, productId),
                new ProductPhoto("url3", 3L, productId)
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Sort sort = Sort.by(Sort.Direction.ASC, "sequenceNumber");
        when(productPhotoRepository.findByProductId(productId, sort)).thenReturn(expectedProductPhotos);

        // Act
        List<ProductPhoto> productPhotos = productPhotoService.findProductPhotos(productId);

        // Assert
        assertThat(productPhotos).isEqualTo(expectedProductPhotos);
        verify(productPhotoRepository).findByProductId(productId, sort);
    }

    @Test
    @DisplayName("findProductPhotos should throw NotFoundException, because productId is incorrect")
    void findProductPhotosUnSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productPhotoService.findProductPhotos(productId))
                .withMessage("Product with ID: " + productId + " not found");
    }

    @Test
    @DisplayName("deleteProductPhotosByIds should call productPhotoRepository.deleteAllByIdInBatch(productPhotosIds)")
    void deleteProductPhotosByIdsSuccessfulTest() {
        // Arrange
        List<Long> productPhotosIds = List.of(1L, 2L, 3L, 4L, 5L);

        // Act
        productPhotoService.deleteProductPhotosByIds(productPhotosIds);

        // Assert
        verify(productPhotoRepository).deleteAllByIdInBatch(productPhotosIds);
    }

    @Test
    @DisplayName("createProductPhoto should create productPhoto, because parameters is correct")
    void createProductPhotoSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        String url = "url";
        Long sequenceNumber = 2L;
        Product product = new Product("name", BigDecimal.valueOf(100), "desc", 1L, 1L);
        ProductPhoto savedProductPhoto = new ProductPhoto("url1", 1L, productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productPhotoRepository.save(any())).thenReturn(savedProductPhoto);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        ProductPhoto actualProductPhoto = productPhotoService.createProductPhoto(productId, url, sequenceNumber);

        // Assert
        assertThat(actualProductPhoto).isEqualTo(savedProductPhoto);
    }

    @Test
    @DisplayName("createProductPhoto should throw NotFoundException, because productId is incorrect")
    void createProductPhotoUnSuccessfulIncorrectProductIdTest() {
        // Arrange
        Long productId = 1L;
        String url = "url";
        Long sequenceNumber = 2L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productPhotoService.createProductPhoto(productId, url, sequenceNumber))
                .withMessage("Product with ID: "  + productId + " not found");
    }
}
