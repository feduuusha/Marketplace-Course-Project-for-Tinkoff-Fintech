package ru.itis.marketplace.catalogservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.impl.ProductSizeServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductSizeServiceImpl.class})
@ActiveProfiles("test")
public class ProductSizeServiceTests {

    @Autowired
    private ProductSizeServiceImpl productSizeService;

    @MockBean
    private ProductSizeRepository productSizeRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findAllProductSizes should return list of product sizes, because productId is correct")
    public void findAllProductSizesSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        List<ProductSize> expectedProductSizes = List.of(
                new ProductSize(1L, "name", productId),
                new ProductSize(2L, "name2", productId),
                new ProductSize(3L, "name3", productId)
        );
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeRepository.findByProductId(productId)).thenReturn(expectedProductSizes);

        // Act
        List<ProductSize> productSizes = productSizeService.findAllProductSizes(productId);

        // Assert
        assertThat(productSizes).isEqualTo(expectedProductSizes);
    }

    @Test
    @DisplayName("findAllProductSizes should throw NotFoundException, because productId is incorrect")
    public void findAllProductSizesUnSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productSizeService.findAllProductSizes(productId))
                .withMessage("Product with ID: "  + productId + " not found");
    }

    @Test
    @DisplayName("createProductSize should create productSize, because parameters is correct")
    public void createProductSizeSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        Product product = new Product();
        List<ProductSize> productSizes = List.of(new ProductSize("name1",productId));
        ProductSize savedProductSize = new ProductSize(name, productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeRepository.findByProductId(productId)).thenReturn(productSizes);
        when(productSizeRepository.save(any())).thenReturn(savedProductSize);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        ProductSize actualProductSize = productSizeService.createProductSize(productId, name);

        // Assert
        assertThat(actualProductSize).isEqualTo(savedProductSize);
    }

    @Test
    @DisplayName("createProductSize should throw NotFoundException, because productId is incorrect")
    public void createProductSizeUnSuccessfulIncorrectProductIdTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productSizeService.createProductSize(productId, name))
                .withMessage("Product with ID: "  + productId + " not found");
    }

    @Test
    @DisplayName("createProductSize should throw BadRequestException, because product size with specified name and product id already exist")
    public void createProductSizeUnSuccessfulAlreadyExistNameTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        Product product = new Product();
        List<ProductSize> productSizes = List.of(new ProductSize(name,productId));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeRepository.findByProductId(productId)).thenReturn(productSizes);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productSizeService.createProductSize(productId, name))
                .withMessage("Product size with name: " + name + " and product ID: " + productId + " already exist");
    }

    @Test
    @DisplayName("deleteAllProductSizesById should call productSizeRepository.deleteAllByIdInBatch(sizeIds) and send message to kafka")
    public void deleteAllProductSizesByIdSuccessfulTest() {
        // Arrange
        List<Long> productSizeIds = List.of(1L, 2L, 3L, 4L, 5L);

        // Act
        productSizeService.deleteAllProductSizesById(productSizeIds);

        // Assert
        verify(productSizeRepository).deleteAllByIdInBatch(productSizeIds);
        verify(kafkaProducer).sendSizeIds(productSizeIds);
    }

    @Test
    @DisplayName("findSizeByIdAndProductId should return product size, because parameters is correct")
    public void findSizeByIdAndProductIdSuccessfulTest() {
        // Arrange
        Long productId = 2L;
        Long productSizeId = 4L;
        ProductSize expectedSize = new ProductSize("name", productId);
        when(productSizeRepository.findById(productSizeId))
                .thenReturn(Optional.of(expectedSize));

        // Act
        ProductSize size = productSizeService.findSizeByIdAndProductId(productId, productSizeId);

        // Assert
        assertThat(size).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("findSizeByIdAndProductId should throw NotFoundException, because product size is incorrect")
    public void findSizeByIdAndProductIdUnSuccessfulProductSizeIsIncorrectTest() {
        // Arrange
        Long productId = 2L;
        Long productSizeId = 4L;
        when(productSizeRepository.findById(productSizeId))
                .thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productSizeService.findSizeByIdAndProductId(productId, productSizeId))
                .withMessage("Size with ID: " + productSizeId + " not found");
    }

    @Test
    @DisplayName("findSizeByIdAndProductId should throw BadRequestException, because product size have different product id")
    public void findSizeByIdAndProductIdUnSuccessfulDifferentProductIdTest() {
        // Arrange
        Long productId = 2L;
        Long productSizeId = 4L;
        Long trueProductId = 100L;
        ProductSize expectedSize = new ProductSize("name", trueProductId);
        when(productSizeRepository.findById(productSizeId))
                .thenReturn(Optional.of(expectedSize));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productSizeService.findSizeByIdAndProductId(productId, productSizeId))
                .withMessage("Size with ID: " + productSizeId + " belongs to Product with ID: " + trueProductId);
    }
}
