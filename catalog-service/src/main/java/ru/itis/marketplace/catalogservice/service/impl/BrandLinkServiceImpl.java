package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.repository.BrandLinkRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandLinkService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandLinkServiceImpl implements BrandLinkService {

    private final BrandLinkRepository brandLinkRepository;
    private final BrandRepository brandRepository;
    @Override
    public List<BrandLink> findAllBrandLinks(Long brandId) {
        return this.brandLinkRepository.findByBrandId(brandId);
    }

    @Override
    @Transactional
    public BrandLink createBrandLink(Long brandId, String url, String name) {
        Optional<Brand> optionalBrand = brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            Brand brand = optionalBrand.get();
            return this.brandLinkRepository.save(new BrandLink(url, name, brand));
        } else {
            throw new NoSuchElementException("Brand with the specified ID was not found");
        }
    }

    @Override
    @Transactional
    public void deleteAllBrandLinkById(Long brandId, List<Long> linkIds) {
        this.brandLinkRepository.deleteAllByIdInBatch(linkIds);
    }
}
