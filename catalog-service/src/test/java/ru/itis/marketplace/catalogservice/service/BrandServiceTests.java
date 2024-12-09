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
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.impl.BrandServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ALL")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BrandServiceImpl.class})
@ActiveProfiles("test")
public class BrandServiceTests {

    @Autowired
    private BrandService brandService;

    @MockBean
    private BrandRepository brandRepository;
    @MockBean
    private ProductService productService;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findBrandById should return brand, because brandId is correct")
    public void findBrandByIdSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        Brand brand = new Brand("name", "desc", "link");
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        // Act
        Brand actualBrand = brandService.findBrandById(brandId);

        // Assert
        assertThat(actualBrand).isEqualTo(brand);
    }

    @Test
    @DisplayName("findBrandById should throw NotFoundException, because brandId is incorrect")
    public void findBrandByIdUnSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> brandService.findBrandById(brandId))
                .withMessage("Brand with ID: "  + brandId + " not found");
    }

    @Test
    @DisplayName("updateBrandById should update brand, because brandId is correct")
    public void updateBrandByIdSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        String name = "name";
        String description = "desc";
        String linkToLogo = "link";
        String status = "status";
        Brand oldBrand = new Brand(brandId, "oldName", "oldDesc",  "oldLink", "oldStatus", null, null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(oldBrand));
        when(brandRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        brandService.updateBrandById(brandId, name, description, linkToLogo, status);

        // Assert
        verify(brandRepository).save(oldBrand);
    }

    @Test
    @DisplayName("updateBrandById should throw BadRequestException, because brand with specified name already exist")
    public void updateBrandByIdUnSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        String name = "name";
        String description = "desc";
        String linkToLogo = "link";
        String status = "status";
        Brand oldBrand = new Brand(brandId, "oldName", "oldDesc",  "oldLink", "oldStatus", null, null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(oldBrand));
        when(brandRepository.findByName(name)).thenReturn(Optional.of(new Brand(name, "desc2", "link2")));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> brandService.updateBrandById(brandId, name, description, linkToLogo, status))
                .withMessage("Brand with name: " + name + " already exist");
    }

    @Test
    @DisplayName("updateBrandById should update brand, because brandId is correct and name is same")
    public void updateBrandByIdSuccessfulSameNameTest() {
        // Arrange
        Long brandId = 2L;
        String name = "name";
        String description = "desc";
        String linkToLogo = "link";
        String status = "status";
        Brand oldBrand = new Brand(brandId, name, "oldDesc",  "oldLink", "oldStatus", null, null);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(oldBrand));

        // Act
        brandService.updateBrandById(brandId, name, description, linkToLogo, status);

        // Assert
        verify(brandRepository).save(oldBrand);
    }

    @Test
    @DisplayName("deleteBrandById should delete brand and send messages in kafka, because brandId is correct")
    public void deleteBrandByIdSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        List<Product> productList = List.of(
                new Product(1L, null, null, null, null, null, brandId, null,
                List.of(new ProductSize(1L, null, 1L), new ProductSize(2L, null, 1L)), null, null),
                new Product(2L, null, null, null, null, null, brandId, null,
                        List.of(new ProductSize(3L, null, 2L), new ProductSize(4L, null, 2L)), null, null));
        when(productService.findAllProducts(null, null, null, null,
                null, null, null, brandId, null))
                .thenReturn(productList);

        // Act
        brandService.deleteBrandById(brandId);

        // Assert
        verify(kafkaProducer).sendSizeIds(List.of(1L, 2L, 3L, 4L));
        verify(kafkaProducer).sendBrandIds(List.of(brandId));
        verify(brandRepository).deleteById(brandId);
    }

    @Test
    @DisplayName("findAllProducts should call productRepository.findAll with pageable and sort, because page and size provided")
    public void findAllBrandsSuccessfulPageableTest() {
        // Arrange
        int page = 1;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(page, pageSize);
        when(brandRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // Act
        brandService.findAllBrands(null, pageSize, page, null);

        // Assert
        verify(brandRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findAllBrands should call brandRepository.findAll with pageable and sorting, because page and size and sort provided")
    public void findAllBrandsSuccessfulPageableAndSortableTest() {
        // Arrange
        int page = 1;
        int pageSize = 2;
        String sort = "name";
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sort));
        when(brandRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // Act
        brandService.findAllBrands(null, pageSize, page, sort);

        // Assert
        verify(brandRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findAllBrands should call brandRepository.findAll with sorting, because sort provided")
    public void findAllBrandsSuccessfulSortableTest() {
        // Arrange
        String sort = "name";
        Pageable pageable = Pageable.unpaged(Sort.by(sort));
        when(brandRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // Act
        brandService.findAllBrands(null, null, null, sort);

        // Assert
        verify(brandRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("findAllBrands should call brandRepository.findAll with out anything, because all parameters null")
    public void findAllBrandsSuccessfulTest() {
        // Arrange
        Pageable pageable = Pageable.unpaged(Sort.unsorted());
        when(brandRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // Act
        brandService.findAllBrands(null, null, null, null);

        // Assert
        verify(brandRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("createBrand should save brand, because provided name is free")
    public void createBrandSuccessfulTest() {
        // Arrange
        String name = "name";
        String description = "description";
        String linkToLogo = "link";
        Brand savedBrand = new Brand(1L, name, description, linkToLogo, null, null, null);
        when(brandRepository.findByName(name)).thenReturn(Optional.empty());
        when(brandRepository.save(any()))
                .thenReturn(savedBrand);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        Brand actualBrand = brandService.createBrand(name, description, linkToLogo);

        // Assert
        assertThat(actualBrand).isEqualTo(savedBrand);
    }

    @Test
    @DisplayName("createBrand should throw BadRequestException, because provided name is not already exist")
    public void createBrandUnSuccessfulTest() {
        // Arrange
        String name = "name";
        String description = "description";
        String linkToLogo = "link";
        Brand existedBrand = new Brand(1L, name, description, linkToLogo, null, null, null);
        when(brandRepository.findByName(name)).thenReturn(Optional.of(existedBrand));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> brandService.createBrand(name, description, linkToLogo))
                .withMessage("Brand with name: " + name + " already exist");
    }

    @Test
    @DisplayName("findAllBrandByIds should call brandRepository.findAllById")
    public void findAllBrandByIdsSuccessfulTest() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        // Act
        brandService.findAllBrandByIds(ids);

        // Assert
        verify(brandRepository).findAllById(ids);
    }

    @Test
    @DisplayName("findBrandsByNameLike should call brandRepository.findByNameLikeIgnoreCase")
    public void findBrandsByNameLikeSuccessfulTest() {
        // Arrange
        String name = "name";
        when(brandRepository.findByNameLikeIgnoreCase(name)).thenReturn(List.of());

        // Act
        brandService.findBrandsByNameLike(name);

        // Assert
        verify(brandRepository).findByNameLikeIgnoreCase(name);
    }

    @Test
    @DisplayName("updateBrandStatusById should set Request Status and save to brandRepository")
    public void updateBrandStatusByIdSuccessfulTest() {
        // Arrange
        Long brandId = 2L;
        String requestStatus = "status";
        Brand oldBrand = Mockito.mock();
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(oldBrand));

        // Act
        brandService.updateBrandStatusById(brandId, requestStatus);

        // Assert
        verify(oldBrand).setRequestStatus(requestStatus);
        verify(brandRepository).save(oldBrand);
    }
}
