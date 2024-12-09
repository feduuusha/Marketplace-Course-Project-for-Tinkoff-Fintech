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
import ru.itis.marketplace.catalogservice.controller.payload.product_photo.NewProductPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductPhotoRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
public class ProductPhotoRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductPhotoService productPhotoService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/photos should return product photos")
    @WithMockUser(roles={"SERVICE"})
    public void findProductPhotosSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        List<ProductPhoto> productPhotos = List.of(
            new ProductPhoto(1L, "url", 1L, productId),
            new ProductPhoto(2L, "ur2", 2L, productId)
        );
        when(productPhotoService.findProductPhotos(productId)).thenReturn(productPhotos);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/{productId}/photos", productId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ProductPhoto> actualProductPhoto = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualProductPhoto).isEqualTo(productPhotos);
        verify(productPhotoService).findProductPhotos(productId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/photos should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findProductPhotosUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/{productId}/photos", productId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId}/photos/{photoIds} should delete product photos by specified ids")
    @WithMockUser(roles={"SERVICE"})
    public void deleteProductPhotosByIdsSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        List<Long> photoIds = List.of(1L, 2L, 3L);

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}/photos/{photoIds}",
                        productId, String.join(",", photoIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isNoContent());

        verify(productPhotoService).deleteProductPhotosByIds(photoIds);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId}/photos/{photoIds} should delete product 401, because not auth-ed")
    @WithAnonymousUser
    public void deleteProductPhotosByIdsUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        List<Long> photoIds = List.of(1L, 2L, 3L);

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}/photos/{photoIds}",
                productId, String.join(",", photoIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/photos should create product photo")
    @WithMockUser(roles={"SERVICE"})
    public void createProductPhotoSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String url = "url";
        Long sequenceNumber = 4L;
        ProductPhoto productPhoto = new ProductPhoto(1L, url, sequenceNumber, productId);
        ObjectMapper mapper = new ObjectMapper();
        when(productPhotoService.createProductPhoto(productId, url, sequenceNumber)).thenReturn(productPhoto);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/products/{productId}/photos",
                        productId)
                        .content(mapper.writeValueAsString(new NewProductPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/product/2/photos/" + productPhoto.getId()))
                .andReturn().getResponse().getContentAsString();
        ProductPhoto actualProductPhoto = mapper.readValue(response, ProductPhoto.class);
        assertThat(actualProductPhoto).isEqualTo(productPhoto);
        verify(productPhotoService).createProductPhoto(productId, url, sequenceNumber);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/photos should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void createProductPhotoUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String url = "url";
        Long sequenceNumber = -4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products/{productId}/photos",
                        productId)
                        .content(mapper.writeValueAsString(new NewProductPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/photos should return 401, because not auth-ed")
    @WithAnonymousUser
    public void createProductPhotoUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String url = "url";
        Long sequenceNumber = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products/{productId}/photos",
                        productId)
                        .content(mapper.writeValueAsString(new NewProductPhotoPayload(url, sequenceNumber)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
