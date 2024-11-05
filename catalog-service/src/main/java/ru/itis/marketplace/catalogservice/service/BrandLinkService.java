package ru.itis.marketplace.catalogservice.service;

import ru.itis.marketplace.catalogservice.entity.BrandLink;

import java.util.List;

public interface BrandLinkService {
    List<BrandLink> findAllBrandLinks(Long brandId);

    BrandLink createBrandLink(Long brandId, String url, String name);

    void deleteAllBrandLinkById(Long brandId, List<Long> linkIds);
}
