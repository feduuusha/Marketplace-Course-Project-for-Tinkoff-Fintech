package ru.itis.marketplace.userservice.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.itis.marketplace.userservice.config.ClientBeans;
import ru.itis.marketplace.userservice.exception.UnavailableServiceException;
import ru.itis.marketplace.userservice.model.Product;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {ClientBeans.class})
@Testcontainers(disabledWithoutDocker = true)
class RestClientProductsRestClientTests {

    @Autowired
    private ProductsRestClient productsRestClient;

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(RestClientProductsRestClientTests.class,"mocks-config.json");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("marketplace.service.catalog.uri", wiremockServer::getBaseUrl);
        registry.add("marketplace.service.catalog.username", () -> "catalog_service_user");
        registry.add("marketplace.service.catalog.password", () -> "19022602");
    }

    @Test
    @DisplayName("Rest client should return true, because product with id and size with id exist in catalog (mocks-config.json)")
    void productWithIdAndWithSizeIdExistSuccessfulTest() {
        // Arrange
        Long productId = 200L;
        Long sizeId = 201L;

        // Act
        boolean productAndSizeAreExist = productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId);

        // Assert
        assertThat(productAndSizeAreExist).isTrue();
    }

    @Test
    @DisplayName("Rest client should return false, because product with id and size with id do not exist in catalog (mocks-config.json)")
    void brandWithIdExistUnSuccessfulNotFoundTest() {
        // Arrange
        Long productId = 404L;
        Long sizeId = 405L;

        // Act
        boolean productAndSizeAreExist = productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId);

        // Assert
        assertThat(productAndSizeAreExist).isFalse();
    }

    @Test
    @DisplayName("Rest client should return false, because server return 400 (mocks-config.json)")
    void brandWithIdExistUnSuccessfulBadRequestTest() {
        // Arrange
        Long productId = 400L;
        Long sizeId = 401L;

        // Act
        boolean productAndSizeAreExist = productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId);

        // Assert
        assertThat(productAndSizeAreExist).isFalse();
    }

    @Test
    @DisplayName("Rest client should throw UnavailableServiceException, because server return 500 (mocks-config.json")
    void brandWithIdExistUnSuccessfulInternalServerErrorTest() {
        // Arrange
        Long productId = 500L;
        Long sizeId = 501L;

        // Act
        // Assert
        assertThatExceptionOfType(UnavailableServiceException.class)
                .isThrownBy(() -> productsRestClient.productWithIdAndWithSizeIdExist(productId, sizeId))
                .withMessage("Catalog service is unavailable, because: 500 Internal Server Error: \"{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500,\"detail\":\"something wrong\",\"instance\":\"/api/v1/catalog/products/500/sizes/501\"}\"");
    }

    @Test
    @DisplayName("Rest client should return list of products, because products with ids exist in catalog (mocks-config.json)")
    void findProductsByIdsSuccessfulTest() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        // Act
        List<Product> products = productsRestClient.findProductsByIds(ids);

        // Assert
        assertThat(products.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Rest client should throw UnavailableServiceException, because rest client return 500 (mocks-config.json)")
    void findProductsByIdsUnSuccessfulTest() {
        // Arrange
        List<Long> ids = List.of(5L, 0L, 3L);

        // Act
        // Assert
        assertThatExceptionOfType(UnavailableServiceException.class)
                .isThrownBy(() -> productsRestClient.findProductsByIds(ids))
                .withMessage("Catalog service is unavailable, because: 500 Internal Server Error: \"{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500,\"detail\":\"something wrong\",\"instance\":\"/api/v1/catalog/products/by-ids/5%2C0%2C3\"}\"");
    }

}
