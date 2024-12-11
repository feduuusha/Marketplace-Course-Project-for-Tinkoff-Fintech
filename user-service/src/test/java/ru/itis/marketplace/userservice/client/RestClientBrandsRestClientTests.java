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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE, classes = {ClientBeans.class})
@Testcontainers(disabledWithoutDocker = true)
class RestClientBrandsRestClientTests {

    @Autowired
    private BrandsRestClient brandsRestClient;

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource(RestClientBrandsRestClientTests.class,"mocks-config.json");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("marketplace.service.catalog.uri", wiremockServer::getBaseUrl);
        registry.add("marketplace.service.catalog.username", () -> "catalog_service_user");
        registry.add("marketplace.service.catalog.password", () -> "19022602");
    }
    @Test
    @DisplayName("Rest client should return true, because brand with specified id exist in catalog (mocks-config.json)")
    void brandWithIdExistSuccessfulTest() {
        // Arrange
        Long id = 200L;

        // Act
        boolean productExist = brandsRestClient.brandWithIdExist(id);

        // Assert
        assertThat(productExist).isTrue();
    }

    @Test
    @DisplayName("Rest client should return false, because brand with specified id do not exist in catalog (mocks-config.json)")
    void brandWithIdExistUnSuccessfulNotFoundTest() {
        // Arrange
        Long id = 404L;

        // Act
        boolean productExist = brandsRestClient.brandWithIdExist(id);

        // Assert
        assertThat(productExist).isFalse();
    }

    @Test
    @DisplayName("Rest client should throw UnavailableServiceException, because server return 500 (mocks-config.json")
    void brandWithIdExistUnSuccessfulInternalServerErrorTest() {
        // Arrange
        Long id = 500L;

        // Act
        // Assert
        assertThatExceptionOfType(UnavailableServiceException.class)
                .isThrownBy(() -> brandsRestClient.brandWithIdExist(id))
                .withMessage("Catalog service is unavailable, because: 500 Internal Server Error: \"{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500,\"detail\":\"something wrong\",\"instance\":\"/api/v1/catalog/brands/500\"}\"");
    }
}
