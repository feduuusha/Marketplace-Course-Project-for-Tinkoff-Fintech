package ru.itis.marketplace.catalogservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.BrandLinkRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.impl.BrandLinkServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BrandLinkServiceImpl.class})
@ActiveProfiles("test")
public class BrandLinkServiceTests {

    @Autowired
    private BrandLinkService brandLinkService;

    @MockBean
    private BrandLinkRepository brandLinkRepository;
    @MockBean
    private BrandRepository brandRepository;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findAllBrandLinks should return list of brand links, because brandId is correct")
    public void findAllBrandLinksSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        Brand brand = new Brand("name", "desc", "link");
        List<BrandLink> expectedBrandLinks = List.of(new BrandLink("url", "name", 1L), new BrandLink("url2", "name2", 1L));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandLinkRepository.findByBrandId(brandId)).thenReturn(expectedBrandLinks);

        // Act
        List<BrandLink> brandLink = brandLinkService.findAllBrandLinks(brandId);

        // Assert
        assertThat(brandLink).isEqualTo(expectedBrandLinks);
    }

    @Test
    @DisplayName("findAllBrandLinks should throw NotFoundException, because brandId is incorrect")
    public void findAllBrandLinksUnSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> brandLinkService.findAllBrandLinks(brandId))
                .withMessage("Brand with ID: "  + brandId + " not found");
    }

    @Test
    @DisplayName("createBrandLink should create brandLink, because parameters is correct")
    public void createBrandLinkSuccessfulTest() {
        // Arrange
        Long brandId = 1L;
        String url = "url";
        String name = "name";
        Brand brand = new Brand("name", "desc", "link");
        List<BrandLink> brandLinks = List.of(new BrandLink("url2", "name2", brandId));
        BrandLink savedBrandLink = new BrandLink(1L, url, name, brandId);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandLinkRepository.findByBrandId(brandId)).thenReturn(brandLinks);
        when(brandLinkRepository.save(any())).thenReturn(savedBrandLink);
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        BrandLink actualBrandLink = brandLinkService.createBrandLink(brandId, url, name);

        // Assert
        assertThat(actualBrandLink).isEqualTo(savedBrandLink);
    }

    @Test
    @DisplayName("createBrandLink should throw NotFoundException, because brandId is incorrect")
    public void createBrandLinkUnSuccessfulIncorrectBrandIdTest() {
        // Arrange
        Long brandId = 1L;
        String url = "url";
        String name = "name";
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> brandLinkService.createBrandLink(brandId, url, name))
                .withMessage("Brand with ID: "  + brandId + " not found");
    }

    @Test
    @DisplayName("createBrandLink should throw BadRequestException, because brand link with specified name already exist")
    public void createBrandLinkUnSuccessfulAlreadyExistNameTest() {
        // Arrange
        Long brandId = 1L;
        String url = "url";
        String name = "name";
        Brand brand = new Brand("name2", "desc", "link");
        List<BrandLink> brandLinks = List.of(new BrandLink("url2", name, brandId));
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandLinkRepository.findByBrandId(brandId)).thenReturn(brandLinks);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> brandLinkService.createBrandLink(brandId, url, name))
                .withMessage("Brand link with name: " + name + " and brand ID: " + brandId + " already exist");
    }

    @Test
    @DisplayName("deleteAllBrandLinkById should call brandLinkRepository.deleteAllByIdInBatch(linkIds)")
    public void deleteAllBrandLinkByIdSuccessfulTest() {
        // Arrange
        List<Long> brandLinkIds = List.of(1L, 2L, 3L, 4L, 5L);

        // Act
        brandLinkService.deleteAllBrandLinkById(brandLinkIds);

        // Assert
        verify(brandLinkRepository).deleteAllByIdInBatch(brandLinkIds);
    }
}
