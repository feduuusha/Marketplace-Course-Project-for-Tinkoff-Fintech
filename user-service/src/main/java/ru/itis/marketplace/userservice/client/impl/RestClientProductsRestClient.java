package ru.itis.marketplace.userservice.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import ru.itis.marketplace.userservice.client.ProductsRestClient;
import ru.itis.marketplace.userservice.exception.UnavailableServiceException;
import ru.itis.marketplace.userservice.model.Product;

import java.util.List;

@RequiredArgsConstructor
public class RestClientProductsRestClient implements ProductsRestClient {

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    @Override
    public boolean productWithIdAndWithSizeIdExist(Long productId, Long sizeId) {
        try {
            restClient
                    .get()
                    .uri("/api/v1/catalog/products/{productId}/sizes/{sizeId}", productId, sizeId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest exception) {
            return false;
        } catch (HttpServerErrorException e) {
            throw new UnavailableServiceException("Catalog service is unavailable, because: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Product> findProductsByIds(List<Long> ids) {
        try {
            return restClient
                    .get()
                    .uri("/api/v1/catalog/products/by-ids/{productIds}", String.join(",", ids.stream().map(String::valueOf).toList()))
                    .retrieve()
                    .body(PRODUCTS_TYPE_REFERENCE);
        } catch (HttpServerErrorException exception) {
            throw new UnavailableServiceException("Catalog service is unavailable, because: " + exception.getMessage());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
