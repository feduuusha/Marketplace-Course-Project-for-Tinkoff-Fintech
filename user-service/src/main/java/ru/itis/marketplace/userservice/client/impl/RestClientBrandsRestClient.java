package ru.itis.marketplace.userservice.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.itis.marketplace.userservice.exception.UnavailableServiceException;
import ru.itis.marketplace.userservice.client.BrandsRestClient;

@RequiredArgsConstructor
public class RestClientBrandsRestClient implements BrandsRestClient {

    private final RestClient restClient;

    @Override
    public boolean brandWithIdExist(Long brandId) {
        try {
            this.restClient
                    .get()
                    .uri("/api/v1/catalog/brands/{brandId}", brandId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new UnavailableServiceException("Catalog service is unavailable, because: " + e.getMessage());
        }
    }
}
