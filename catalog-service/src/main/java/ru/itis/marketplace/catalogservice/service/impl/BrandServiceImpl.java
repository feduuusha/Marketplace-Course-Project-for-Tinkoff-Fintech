package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.status.RequestStatus;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public Brand findBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Brand with ID: " + id + " not found"));
    }

    @Override
    @Transactional
    public void updateBrandById(Long brandId, String name, String description, String linkToLogo, String status) {
        Optional<Brand> optionalBrand = this.brandRepository.findById(brandId);
        if (optionalBrand.isPresent()) {
            if (RequestStatus.statusIsValid(status)) {
                Brand brand = optionalBrand.get();
                brand.setName(name);
                brand.setDescription(description);
                brand.setLinkToLogo(linkToLogo);
                brand.setRequestStatus(RequestStatus.valueOf(status.toUpperCase()));
                this.brandRepository.save(brand);
            } else {
                throw new IllegalArgumentException(status.toUpperCase() + " is not valid request status");
            }
        } else {
            throw new NoSuchElementException("Brand with the specified ID was not found");
        }
    }

    @Override
    @Transactional
    public void deleteBrandById(Long id) {
        this.brandRepository.deleteById(id);
    }

    @Override
    public List<Brand> findAllBrands(String status) {
        if (status == null) {
            return this.brandRepository.findAll();
        }
        if (RequestStatus.statusIsValid(status)) {
            return this.brandRepository.findByRequestStatus(RequestStatus.valueOf(status.toUpperCase()));
        } else {
            throw new IllegalArgumentException(status.toUpperCase() + " is not valid request status");
        }
    }

    @Override
    @Transactional
    public Brand createBrand(String name, String description, String linkToLogo) {
        return this.brandRepository.save(new Brand(name, description, linkToLogo));
    }

    @Override
    public List<Brand> findAllBrandByIds(List<Long> brandIds) {
        return this.brandRepository.findAllById(brandIds);
    }

}
