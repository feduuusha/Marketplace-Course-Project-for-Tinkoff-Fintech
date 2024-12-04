package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.BrandLinkRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandLinkService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandLinkServiceImpl implements BrandLinkService {

    private final BrandLinkRepository brandLinkRepository;
    private final BrandRepository brandRepository;
    private final MeterRegistry meterRegistry;

    @Override
    public List<BrandLink> findAllBrandLinks(Long brandId) {
        brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException("Brand with ID: " + brandId + " not found"));
        return brandLinkRepository.findByBrandId(brandId);
    }

    @Override
    @Transactional
    public BrandLink createBrandLink(Long brandId, String url, String name) {
        brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException("Brand with ID: " + brandId + " not found"));
        if (brandLinkRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Brand link with name: " + name + " already exist");
        }
        var brandLink = brandLinkRepository.save(new BrandLink(url, name, brandId));
        meterRegistry.counter("count of created brand links").increment();
        return brandLink;
    }

    @Override
    public void deleteAllBrandLinkById(Long brandId, List<Long> linkIds) {
        brandLinkRepository.deleteAllByIdInBatch(linkIds);
    }
}
