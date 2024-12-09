package ru.itis.marketplace.catalogservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.Category;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.repository.CategoryRepository;
import ru.itis.marketplace.catalogservice.repository.ProductRepository;
import ru.itis.marketplace.catalogservice.repository.ProductSizeRepository;
import ru.itis.marketplace.catalogservice.service.impl.ProductServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ALL")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {ProductServiceImpl.class})
@ActiveProfiles("test")
public class ProductServiceTests {

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private ProductSizeRepository productSizeRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private BrandRepository brandRepository;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findProductById should return product, because productId is correct")
    public void findProductByIdSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Product actualProduct = productService.findProductById(productId);

        // Assert
        assertThat(actualProduct).isEqualTo(product);
    }

    @Test
    @DisplayName("findProductById should throw NotFoundException, because productId is incorrect")
    public void findProductByIdUnSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productService.findProductById(productId))
                .withMessage("Product with ID: "  + productId + " not found");
    }

    @Test
    @DisplayName("updateProductById should update product, because productId, categoryId, brandId is correct and name is free")
    public void updateProductByIdSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = Mockito.mock();
        Category category = new Category(categoryId, "nameCat");
        Brand brand = new Brand(brandId, "nameBr", "descBr", "linkBr", "statusBr", null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(product.getName()).thenReturn("anotherName");
        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        productService.updateProductById(productId, name, price, description, status, categoryId, brandId);

        // Assert
        verify(product).setBrandId(brandId);
        verify(product).setName(name);
        verify(product).setDescription(description);
        verify(product).setPrice(price);
        verify(product).setRequestStatus(status.toLowerCase());
        verify(product).setCategoryId(categoryId);
        verify(kafkaProducer).sendProductUpdateMessage(productId, brandId);
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("updateProductById should throw NotFoundException, because productId is incorrect")
    public void updateProductByIdUnSuccessfulProductNotFoundTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> productService.updateProductById(productId, name, price, description, status, categoryId, brandId))
                .withMessage("Product with ID: " + productId  + " not found");
    }

    @Test
    @DisplayName("updateProductById should throw BadRequestException, because categoryId is incorrect")
    public void updateProductByIdUnSuccessfulCategoryNotFoundTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.updateProductById(productId, name, price, description, status, categoryId, brandId))
                .withMessage("Category with ID: " + categoryId  + " not found");
    }

    @Test
    @DisplayName("updateProductById should throw BadRequestException, because brandId is incorrect")
    public void updateProductByIdUnSuccessfulBrandNotFoundTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = new Product();
        Category category = new Category();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.updateProductById(productId, name, price, description, status, categoryId, brandId))
                .withMessage("Brand with ID: " + brandId  + " not found");
    }

    @Test
    @DisplayName("updateProductById should update product, because productId, categoryId, brandId is correct and name is same")
    public void updateProductByIdSuccessfulSameNameTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = Mockito.mock();
        Category category = new Category(categoryId, "nameCat");
        Brand brand = new Brand(brandId, "nameBr", "descBr", "linkBr", "statusBr", null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(product.getName()).thenReturn(name);
        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        productService.updateProductById(productId, name, price, description, status, categoryId, brandId);

        // Assert
        verify(product).setBrandId(brandId);
        verify(product).setName(name);
        verify(product).setDescription(description);
        verify(product).setPrice(price);
        verify(product).setRequestStatus(status.toLowerCase());
        verify(product).setCategoryId(categoryId);
        verify(kafkaProducer).sendProductUpdateMessage(productId, brandId);
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("updateProductById should throw BadRequestException, because provided name already exist")
    public void updateProductByIdUnSuccessfulTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = Mockito.mock();
        Category category = new Category(categoryId, "nameCat");
        Brand brand = new Brand(brandId, "nameBr", "descBr", "linkBr", "statusBr", null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(product.getName()).thenReturn("anotherName");
        when(productRepository.findByName(name)).thenReturn(Optional.of(new Product()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.updateProductById(productId, name, price, description, status, categoryId, brandId))
                .withMessage("Product with name: " + name + " already exist");
    }

    @Test
    @DisplayName("updateProductById should update product and dont send message in kafka, because productId, categoryId, brandId is correct and name is same and brandId is same")
    public void updateProductByIdSuccessfulSameBrandTest() {
        // Arrange
        Long productId = 1L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 2L;
        Long brandId = 3L;
        Product product = Mockito.mock();
        Category category = new Category(categoryId, "nameCat");
        Brand brand = new Brand(brandId, "nameBr", "descBr", "linkBr", "statusBr", null, null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(product.getBrandId()).thenReturn(brandId);
        when(product.getName()).thenReturn(name);
        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        productService.updateProductById(productId, name, price, description, status, categoryId, brandId);

        // Assert
        verify(product).setName(name);
        verify(product).setDescription(description);
        verify(product).setPrice(price);
        verify(product).setRequestStatus(status.toLowerCase());
        verify(product).setCategoryId(categoryId);
        verifyNoInteractions(kafkaProducer);
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("deleteProductById should call productRepository.deleteById(id) and send message to kafka")
    public void deleteAllProductSizesByIdSuccessfulTest() {
        // Arrange
        Long productId = 2L;
        List<ProductSize> productSizes = List.of(
                new ProductSize(1L, "name1", productId),
                new ProductSize(2L, "name2", productId),
                new ProductSize(3L, "name3", productId)
        );
        when(productSizeRepository.findByProductId(productId)).thenReturn(productSizes);

        // Act
        productService.deleteProductById(productId);

        // Assert
        verify(productRepository).deleteById(productId);
        verify(kafkaProducer).sendSizeIds(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("findAllProducts should call productRepository.findAll with pageable, sort and direction, because page, size, sort and direction provided")
    public void findAllProductsSuccessfulWithAllTest() {
        // Arrange
        int page = 1;
        int pageSize = 2;
        String sortBy = "name";
        String direction = "desc";
        var dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());


        // Act
        productService.findAllProducts(pageSize, page, sortBy, direction, null, null, null, null, null);

        // Assert
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findAllProducts should call productRepository.findAll with out anything, because nothing provided")
    public void findAllProductsSuccessfulWithOutAnythingTest() {
        // Arrange
        Pageable pageable = Pageable.unpaged(Sort.unsorted());
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());


        // Act
        productService.findAllProducts(null, null, null, null, null, null, null, null, null);

        // Assert
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("createProduct should save product, because provided name is free and brandId categoryId are correct")
    public void createProductSuccessfulTest() {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        Long categoryId = 2L;
        Long brandId = 3L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(new Brand()));
        Product savedProduct = new Product();
        when(productRepository.findByName(name)).thenReturn(Optional.empty());
        when(productRepository.save(any()))
                .thenReturn(savedProduct);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        Product actualProduct = productService.createProduct(name, price, description, categoryId, brandId);

        // Assert
        assertThat(actualProduct).isEqualTo(savedProduct);
    }

    @Test
    @DisplayName("createProduct should throw BadRequestException, because provided categoryId is incorrect")
    public void createProductUnSuccessfulCategoryIdNotFoundTest() {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        Long categoryId = 2L;
        Long brandId = 3L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.createProduct(name, price, description, categoryId, brandId))
                .withMessage("Category with ID: " + categoryId + " not found");
    }

    @Test
    @DisplayName("createProduct should throw BadRequestException, because provided brandId is incorrect")
    public void createProductUnSuccessfulBrandIdNotFoundTest() {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        Long categoryId = 2L;
        Long brandId = 3L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.createProduct(name, price, description, categoryId, brandId))
                .withMessage("Brand with ID: " + brandId + " not found");
    }

    @Test
    @DisplayName("createProduct should throw BadRequestException, because provided name is already exist")
    public void createProductUnSuccessfulNameAlreadyExistTest() {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        Long categoryId = 2L;
        Long brandId = 3L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(new Brand()));
        when(productRepository.findByName(name)).thenReturn(Optional.of(new Product()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> productService.createProduct(name, price, description, categoryId, brandId))
                .withMessage("Product with name: " + name + " already exist");
    }

    @Test
    @DisplayName("findProductsByIds should call productRepository.findAllById")
    public void findProductsByIdsSuccessfulTest() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);
        // Act
        productService.findProductsByIds(ids);

        // Assert
        verify(productRepository).findAllById(ids);
    }

    @Test
    @DisplayName("findProductsByNameLike should call productRepository.findByNameLikeIgnoreCase")
    public void findProductsByNameLikeSuccessfulTest() {
        // Arrange
        String name = "name";
        List<Product> products = List.of(
                new Product(name, null, null, null, null),
                new Product(name, null, null, null, null),
                new Product(name, null, null, null, null)
        );
        Random random = new Random();
        products = products.stream().peek(product -> product.setId(random.nextLong())).toList();
        when(productRepository.findByNameLikeIgnoreCase(name)).thenReturn(products);

        // Act
        List<Product> actualProducts = productService.findProductsByNameLike(name);

        // Assert
        verify(productRepository).findByNameLikeIgnoreCase(name);
        assertThat(actualProducts).isEqualTo(products);
    }

    @Test
    @DisplayName("updateProductStatusById should call productRepository.findByNameLikeIgnoreCase")
    public void updateProductStatusByIdSuccessfulTest() {
        // Arrange
        Long productId = 2L;
        String requestStatus = "status";
        Product product = Mockito.mock();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        productService.updateProductStatusById(productId, requestStatus);

        // Assert
        verify(product).setRequestStatus(requestStatus);
        verify(productRepository).save(product);
    }
}
