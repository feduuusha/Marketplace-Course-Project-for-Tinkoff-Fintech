package ru.itis.marketplace.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;
import ru.itis.marketplace.userservice.client.impl.RestClientBrandsRestClient;
import ru.itis.marketplace.userservice.client.impl.RestClientProductsRestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientProductsRestClient productsRestClient(
            @Value("${marketplace.service.catalog.uri:http://localhost:8081}") String catalogBaseUrl,
            @Value("${marketplace.service.catalog.username:}") String username,
            @Value("${marketplace.service.catalog.password:}") String password
    ) {
        return new RestClientProductsRestClient(
                RestClient
                        .builder()
                        .requestInterceptor(new BasicAuthenticationInterceptor(username, password))
                        .baseUrl(catalogBaseUrl)
                        .build()
        );
    }

    @Bean
    public RestClientBrandsRestClient brandsRestClient(
            @Value("${marketplace.service.catalog.uri:http://localhost:8081}") String catalogBaseUrl,
            @Value("${marketplace.service.catalog.username:}") String username,
            @Value("${marketplace.service.catalog.password:}") String password
    ) {
        return new RestClientBrandsRestClient(
                RestClient
                        .builder()
                        .requestInterceptor(new BasicAuthenticationInterceptor(username, password))
                        .baseUrl(catalogBaseUrl)
                        .build()
        );
    }
}
