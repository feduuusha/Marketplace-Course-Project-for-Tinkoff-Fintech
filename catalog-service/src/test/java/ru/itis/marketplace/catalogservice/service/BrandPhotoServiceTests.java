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
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.BrandPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.impl.BrandPhotoServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BrandPhotoServiceImpl.class})
@ActiveProfiles("test")
class BrandPhotoServiceTests {

    @Autowired
    private BrandPhotoService brandPhotoService;

    @MockBean
    private BrandPhotoRepository brandPhotoRepository;
    @MockBean
    private BrandRepository brandRepository;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;


    @Test
    @DisplayName("findBrandPhotos should return list of brand photos, because brandId is correct")
    void findBrandPhotosSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        Brand brand = new Brand("name", "desc", "link");
        List<BrandPhoto> expectedBrandPhotos = List.of(
                new BrandPhoto("url", 1L, 1L),
                new BrandPhoto("url2", 2L, 1L)
        );
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        Sort sort = Sort.by(Sort.Direction.ASC, "sequenceNumber");
        when(brandPhotoRepository.findByBrandId(brandId, sort)).thenReturn(expectedBrandPhotos);

        // Act
        List<BrandPhoto> brandPhotos = brandPhotoService.findBrandPhotos(brandId);

        // Assert
        assertThat(brandPhotos).isEqualTo(expectedBrandPhotos);
        verify(brandPhotoRepository).findByBrandId(brandId, sort);
    }

    @Test
    @DisplayName("findBrandPhotos should throw NotFoundException, because brandId is incorrect")
    void findBrandPhotosUnSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> brandPhotoService.findBrandPhotos(brandId))
                .withMessage("Brand with ID: " + brandId + " not found");
    }


    @Test
    @DisplayName("deleteAllBrandPhotosById should call brandLinkRepository.deleteAllByIdInBatch(brandPhotosIds)")
    void deleteAllBrandLinkByIdSuccessfulTest() {
        // Arrange
        List<Long> brandPhotosIds = List.of(1L, 2L, 3L, 4L, 5L);

        // Act
        brandPhotoService.deleteAllBrandPhotosById(brandPhotosIds);

        // Assert
        verify(brandPhotoRepository).deleteAllByIdInBatch(brandPhotosIds);
    }

    @Test
    @DisplayName("createBrandPhoto should create brandPhoto, because parameters is correct")
    void createBrandPhotoSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        String url = "url";
        Long sequenceNumber = 2L;
        Brand brand = new Brand("name", "desc", "link");
        BrandPhoto savedBrandphoto = new BrandPhoto(1L, url, sequenceNumber, brandId);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandPhotoRepository.save(any())).thenReturn(savedBrandphoto);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        BrandPhoto actualBrandPhoto = brandPhotoService.createBrandPhoto(brandId, url, sequenceNumber);

        // Assert
        assertThat(actualBrandPhoto).isEqualTo(savedBrandphoto);
    }

    @Test
    @DisplayName("createBrandPhoto should throw NotFoundException, because brandId is incorrect")
    void createBrandPhotoUnSuccessfulIncorrectBrandIdTest() {
        // Arrange
        Long brandId = 1L;
        String url = "url";
        Long sequenceNumber = 2L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> brandPhotoService.createBrandPhoto(brandId, url, sequenceNumber))
                .withMessage("Brand with ID: "  + brandId + " not found");
    }
}
