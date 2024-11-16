package ru.itis.marketplace.userservice.client;

import ru.itis.marketplace.userservice.model.Product;

import java.util.List;

public interface ProductsRestClient {
    boolean productWithIdAndWithSizeIdExist(Long productId, Long sizeId);
    List<Product> findProductsByIds(List<Long> ids);
}
