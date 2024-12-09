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
import ru.itis.marketplace.catalogservice.controller.payload.product_size.NewProductSizePayload;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductSizeRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
public class ProductSizeRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductSizeService productSizeService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/sizes should return product sizes")
    @WithMockUser(roles={"SERVICE"})
    public void findAllProductSizesSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        List<ProductSize> sizes = List.of(
            new ProductSize(1L, "name", productId),
            new ProductSize(2L, "name2", productId),
            new ProductSize(3L, "name3", productId)
        );
        when(productSizeService.findAllProductSizes(productId)).thenReturn(sizes);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/{productId}/sizes", productId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<ProductSize> actualSizes = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualSizes).isEqualTo(sizes);
        verify(productSizeService).findAllProductSizes(productId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/sizes should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findAllProductSizesUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/{productId}/sizes", productId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/sizes should create product size")
    @WithMockUser(roles={"SERVICE"})
    public void createProductSizeSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 2L;
        String name = "name";
        ProductSize productSize = new ProductSize(2L, name, productId);
        when(productSizeService.createProductSize(productId, name)).thenReturn(productSize);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/products/{productId}/sizes", productId)
                        .content(mapper.writeValueAsString(new NewProductSizePayload(name)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        ProductSize actualSize = mapper.readValue(response, ProductSize.class);
        assertThat(actualSize).isEqualTo(productSize);
        verify(productSizeService).createProductSize(productId, name);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/sizes should return 400, because incorrect payload")
    @WithMockUser(roles={"SERVICE"})
    public void createProductSizeUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 2L;
        String name = "   ";
        ProductSize productSize = new ProductSize(2L, name, productId);
        when(productSizeService.createProductSize(productId, name)).thenReturn(productSize);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products/{productId}/sizes", productId)
                        .content(mapper.writeValueAsString(new NewProductSizePayload(name)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products/{productId}/sizes should return 401, because not auuth-ed")
    @WithAnonymousUser
    public void createProductSizeUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 2L;
        String name = "name";
        ProductSize productSize = new ProductSize(2L, name, productId);
        when(productSizeService.createProductSize(productId, name)).thenReturn(productSize);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products/{productId}/sizes", productId)
                        .content(mapper.writeValueAsString(new NewProductSizePayload(name)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId}/sizes/{sizeIds} should delete product sizes")
    @WithMockUser(roles={"SERVICE"})
    public void deleteAllProductSizesByIdSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        List<Long> sizeIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}/sizes/{sizeIds}",
                        productId, String.join(",", sizeIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isNoContent());
        verify(productSizeService).deleteAllProductSizesById(sizeIds);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId}/sizes/{sizeIds} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void deleteAllProductSizesByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        List<Long> sizeIds = List.of(1L, 2L, 3L, 4L);

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}/sizes/{sizeIds}",
                        productId, String.join(",", sizeIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/sizes/{sizeId} should check what productSize.getProductId() == productId")
    @WithMockUser(roles={"SERVICE"})
    public void findSizeByIdAndProductIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        Long productId = 2L;
        Long sizeId = 3L;
        String name = "name";
        ProductSize productSize = new ProductSize(4L, name, productId);
        when(productSizeService.findSizeByIdAndProductId(productId, sizeId)).thenReturn(productSize);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/{productId}/sizes/{sizeId}", productId, sizeId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ProductSize actualProductSize = mapper.readValue(response, ProductSize.class);
        assertThat(actualProductSize).isEqualTo(productSize);
        verify(productSizeService).findSizeByIdAndProductId(productId, sizeId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId}/sizes/{sizeId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findSizeByIdAndProductIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        Long sizeId = 3L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/{productId}/sizes/{sizeId}", productId, sizeId))
                .andExpect(status().isUnauthorized());
    }
}
