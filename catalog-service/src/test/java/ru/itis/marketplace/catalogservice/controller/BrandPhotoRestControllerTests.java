package ru.itis.marketplace.catalogservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.catalogservice.config.SecurityBeans;
import ru.itis.marketplace.catalogservice.controller.payload.brand_photo.NewBrandPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BrandPhotoRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
class BrandPhotoRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandPhotoService brandPhotoService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/{brandId}/photos should return brand photos")
    @WithMockUser(roles={"SERVICE"})
    void findBrandPhotosSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        List<BrandPhoto> brandPhotos = List.of(
            new BrandPhoto(1L, "url1", 1L, brandId),
            new BrandPhoto(2L, "url2", 2L, brandId),
            new BrandPhoto(3L, "url3", 3L, brandId)
        );
        when(brandPhotoService.findBrandPhotos(brandId)).thenReturn(brandPhotos);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/brands/{brandId}/photos", brandId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<BrandPhoto> actualBrandPhotos = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualBrandPhotos).isEqualTo(brandPhotos);
        verify(brandPhotoService).findBrandPhotos(brandId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/brands/{brandId}/photos should return 401, because not auth-ed")
    @WithAnonymousUser
    void findAllBrandLinksUnSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/brands/{brandId}/links", brandId))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId}/photos/{photoIds} should delete photo with specified ids")
    @WithMockUser(roles={"SERVICE"})
    void deleteAllBrandPhotosByIdSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        List<Long> photoIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(
                        delete("/api/v1/catalog/brands/{brandId}/photos/{photoIds}",
                                brandId, String.join(",", photoIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isNoContent());
        verify(brandPhotoService).deleteAllBrandPhotosById(photoIds);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/brands/{brandId}/photos/{photoIds} should return 401, because not auth-ed")
    @WithAnonymousUser()
    void deleteAllBrandPhotosByIdUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        List<Long> photoIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(
                        delete("/api/v1/catalog/brands/{brandId}/photos/{photoIds}",
                                brandId, String.join(",", photoIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/photos should create brand photo")
    @WithMockUser(roles={"SERVICE"})
    void createBrandPhotoSuccessfulTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String url = "url";
        Long sequenceNumber = 100L;
        ObjectMapper mapper = new ObjectMapper();
        BrandPhoto brandPhoto = new BrandPhoto(100L, url, sequenceNumber, brandId);
        when(brandPhotoService.createBrandPhoto(brandId, url, sequenceNumber)).thenReturn(brandPhoto);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/photos", brandId)
                        .content(mapper.writeValueAsBytes(new NewBrandPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/brands/2/photos/" + brandPhoto.getId()))
                .andReturn().getResponse().getContentAsString();
        BrandPhoto actualBrandPhoto = mapper.readValue(response, BrandPhoto.class);
        assertThat(actualBrandPhoto).isEqualTo(brandPhoto);
        verify(brandPhotoService).createBrandPhoto(brandId, url, sequenceNumber);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/photos should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void createBrandPhotoUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String url = null;
        ObjectMapper mapper = new ObjectMapper();
        Long sequenceNumber = 100L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/photos", brandId)
                        .content(mapper.writeValueAsBytes(new NewBrandPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/brands/{brandId}/photos should return 401, because not auth-ed")
    @WithAnonymousUser
    void createBrandPhotoUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long brandId = 2L;
        String url = "url";
        ObjectMapper mapper = new ObjectMapper();
        Long sequenceNumber = 100L;

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/brands/{brandId}/photos", brandId)
                        .content(mapper.writeValueAsBytes(new NewBrandPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
