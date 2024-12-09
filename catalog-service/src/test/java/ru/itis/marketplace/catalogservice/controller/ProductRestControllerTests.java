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
import ru.itis.marketplace.catalogservice.controller.payload.product.NewProductPayload;
import ru.itis.marketplace.catalogservice.controller.payload.product.UpdateProductPayload;
import ru.itis.marketplace.catalogservice.controller.payload.product.UpdateProductStatusPayload;
import ru.itis.marketplace.catalogservice.entity.Product;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductRestController.class})
@Import(SecurityBeans.class)
@ActiveProfiles("test")
public class ProductRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId} should return product")
    @WithMockUser(roles={"SERVICE"})
    public void findProductByIdSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        ObjectMapper mapper = new ObjectMapper();
        Product product = new Product(productId, null, null, null, null, null, null, null, null, null, null);
        when(productService.findProductById(productId)).thenReturn(product);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Product actualProduct = mapper.readValue(response, Product.class);
        assertThat(actualProduct).isEqualTo(product);
        verify(productService).findProductById(productId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/{productId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findProductByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/products/{productId} should update product")
    @WithMockUser(roles={"SERVICE"})
    public void updateProductByIdSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 3L;
        Long brandId = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductPayload(name, price, description, categoryId, brandId, status))))
                .andExpect(status().isNoContent());
        verify(productService).updateProductById(productId, name, price, description, status, categoryId, brandId);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/products/{productId} should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void updateProductByIdUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(-100);
        String description = "desc";
        String status = "status";
        Long categoryId = 3L;
        Long brandId = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductPayload(name, price, description, categoryId, brandId, status))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/catalog/products/{productId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void updateProductByIdUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 3L;
        Long brandId = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductPayload(name, price, description, categoryId, brandId, status))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/catalog/products/{productId} should update product status")
    @WithMockUser(roles={"SERVICE"})
    public void updateProductStatusByIdSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String status = "status";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(patch("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductStatusPayload(status))))
                .andExpect(status().isNoContent());
        verify(productService).updateProductStatusById(productId, status);
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/catalog/products/{productId} should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void updateProductStatusByIdUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String status = "   ";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductStatusPayload(status))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PATCH Endpoint: api/v1/catalog/products/{productId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void updateProductStatusByIdUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String status = "status";
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(put("/api/v1/catalog/products/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UpdateProductStatusPayload(status))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId} should delete product")
    @WithMockUser(roles={"SERVICE"})
    public void deleteProductByIdSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isNoContent());
        verify(productService).deleteProductById(productId);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/catalog/products/{productId} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void deleteProductByIdUnSuccessfulTest() throws Exception {
        // Arrange
        Long productId = 2L;

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products should return all products")
    @WithMockUser(roles={"SERVICE"})
    public void findAllProductsSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        List<Product> products = List.of(
                new Product(1L, null, null, null, null, null, null, null, null, null, null),
                new Product(2L, null, null, null, null, null, null, null, null, null, null),
                new Product(3L, null, null, null, null, null, null, null, null, null, null)
        );
        when(productService.findAllProducts(null, null, null, null, null, null, null, null, null))
                .thenReturn(products);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Product> actualProducts = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualProducts).isEqualTo(products);
        verify(productService).findAllProducts(null, null, null, null, null, null, null, null, null);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findAllProductsUnSuccessfulTest() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products should return not all products, because all parameters is provided")
    @WithMockUser(roles={"SERVICE"})
    public void findAllProductsSuccessfulAllParametersTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        List<Product> products = List.of(
                new Product(1L, null, null, null, null, null, null, null, null, null, null),
                new Product(2L, null, null, null, null, null, null, null, null, null, null),
                new Product(3L, null, null, null, null, null, null, null, null, null, null)
        );
        Integer pageSize = 2;
        Integer page = 3;
        String sortBy = "name";
        String direction = "asc";
        BigDecimal priceFrom = BigDecimal.valueOf(100);
        BigDecimal priceTo = BigDecimal.valueOf(200);
        String status = "status";
        Long brandId = 5L;
        Long categoryId = 6L;
        when(productService.findAllProducts(pageSize, page, sortBy, direction, priceFrom, priceTo, status, brandId, categoryId))
                .thenReturn(products);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products" +
                        "?page-size={pageSize}&page={page}&sort-by={sortBy}&direction={direction}" +
                        "&price-from={priceFrom}&price-to={priceTo}&status={status}" +
                        "&brand-id={brandId}&category-id={categoryId}",
                        pageSize, page, sortBy, direction, priceFrom, priceTo, status, brandId, categoryId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Product> actualProducts = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualProducts).isEqualTo(products);
        verify(productService).findAllProducts(pageSize, page, sortBy, direction, priceFrom, priceTo, status, brandId, categoryId);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/by-ids/{productIds} should products with specified ids")
    @WithMockUser(roles={"SERVICE"})
    public void findProductsByIdsSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        List<Long> productIds = List.of(1L, 2L, 3L);
        List<Product> products = List.of(
                new Product(1L, null, null, null, null, null, null, null, null, null, null),
                new Product(2L, null, null, null, null, null, null, null, null, null, null),
                new Product(3L, null, null, null, null, null, null, null, null, null, null)
        );
        when(productService.findProductsByIds(productIds)).thenReturn(products);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/by-ids/{productIds}",
                        String.join(",", productIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Product> actualProducts = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualProducts).isEqualTo(products);
        verify(productService).findProductsByIds(productIds);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/by-ids/{productIds} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findProductsByIdsUnSuccessfulTest() throws Exception {
        // Arrange
        List<Long> productIds = List.of(1L, 2L, 3L);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/by-ids/{productIds}",
                        String.join(",", productIds.stream().map(String::valueOf).toList())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products should create product")
    @WithMockUser(roles={"SERVICE"})
    public void createCategorySuccessfulTest() throws Exception {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(100);
        String description = "desc";
        String status = "status";
        Long categoryId = 3L;
        Long brandId = 4L;
        Product product = new Product(1L, name, price, description, status, categoryId, brandId, null, null, null, null);
        when(productService.createProduct(name, price, description, categoryId, brandId)).thenReturn(product);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewProductPayload(name, price, description, categoryId, brandId))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/catalog/products/" + product.getId()))
                .andReturn().getResponse().getContentAsString();
        Product actualProduct = mapper.readValue(response, Product.class);
        assertThat(actualProduct).isEqualTo(product);
        verify(productService).createProduct(name, price, description, categoryId, brandId);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    public void createCategoryUnSuccessfulIncorrectPayloadTest() throws Exception {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(-100);
        String description = "desc";
        Long categoryId = 3L;
        Long brandId = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewProductPayload(name, price, description, categoryId, brandId))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/catalog/products should return 401, because not auth-ed")
    @WithAnonymousUser
    public void createCategoryUnSuccessfulNotAuthenticatedTest() throws Exception {
        // Arrange
        String name = "name";
        BigDecimal price = BigDecimal.valueOf(-100);
        String description = "desc";
        Long categoryId = 3L;
        Long brandId = 4L;
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewProductPayload(name, price, description, categoryId, brandId))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/search should return products by name")
    @WithMockUser(roles = {"SERVICE"})
    public void findProductsByNameLikeSuccessfulTest() throws Exception {
        // Arrange
        List<Product> products = List.of(
                new Product(1L, "name", null, null, null, null, null, null, null, null, null),
                new Product(2L, "name", null, null, null, null, null, null, null, null, null),
                new Product(3L, "name", null, null, null, null, null, null, null, null, null)
        );
        String name = "name";
        when(productService.findProductsByNameLike(name)).thenReturn(products);
        ObjectMapper mapper = new ObjectMapper();

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/catalog/products/search?name={name}", name))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Product> actualProducts = mapper.readValue(response, new TypeReference<>() {});
        assertThat(actualProducts).isEqualTo(products);
        verify(productService).findProductsByNameLike(name);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/catalog/products/search should return 401, because not auth-ed")
    @WithAnonymousUser
    public void findProductsByNameLikeUnSuccessfulTest() throws Exception {
        // Arrange
        String name = "name";

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/catalog/products/search?name={name}", name))
                .andExpect(status().isUnauthorized());
    }
}
