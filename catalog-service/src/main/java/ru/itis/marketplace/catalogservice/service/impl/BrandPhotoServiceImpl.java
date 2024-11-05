package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.repository.BrandPhotoRepository;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandPhotoServiceImpl implements BrandPhotoService {

    private final BrandPhotoRepository brandPhotoRepository;
    private final BrandRepository brandRepository;

    @Override
    public List<BrandPhoto> findBrandPhotos(Long brandId) {
        return this.brandPhotoRepository.findAllByBrandId(brandId, Sort.by(Sort.Direction.ASC, "sequenceNumber"));
    }

    @Override
    @Transactional
    public void deleteAllBrandPhotosById(Long brandId, List<Long> photoIds) {
        this.brandPhotoRepository.deleteAllByIdInBatch(photoIds);
    }

    @Override
    @Transactional
    public BrandPhoto createBrandPhoto(Long brandId, String url, Long sequenceNumber) {
        Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            Brand brand = optionalBrand.get();
            return this.brandPhotoRepository.save(new BrandPhoto(url, sequenceNumber, brand));
        } else {
            throw new NoSuchElementException("Brand with the specified ID was not found");
        }
    }
}
