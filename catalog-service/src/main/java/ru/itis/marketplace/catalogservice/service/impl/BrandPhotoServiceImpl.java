package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.repository.BrandPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandPhotoServiceImpl implements BrandPhotoService {

    private final BrandPhotoRepository brandPhotoRepository;
    private final BrandRepository brandRepository;
    private final MeterRegistry meterRegistry;

    @Override
    public List<BrandPhoto> findBrandPhotos(Long brandId) {
        brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException("Brand with ID: " + brandId + " not found"));
        return brandPhotoRepository.findByBrandId(brandId, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
    }

    @Override
    public void deleteAllBrandPhotosById(List<Long> photoIds) {
        brandPhotoRepository.deleteAllByIdInBatch(photoIds);
    }

    @Override
    @Transactional
    public BrandPhoto createBrandPhoto(Long brandId, String url, Long sequenceNumber) {
        brandRepository.findById(brandId).orElseThrow(() -> new NotFoundException("Brand with ID: " + brandId + " not found"));
        var brandPhoto = brandPhotoRepository.save(new BrandPhoto(url, sequenceNumber, brandId));
        meterRegistry.counter("count of created brand photos").increment();
        return brandPhoto;
    }
}
