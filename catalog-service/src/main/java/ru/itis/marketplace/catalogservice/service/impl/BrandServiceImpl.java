package ru.itis.marketplace.catalogservice.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandService;

import java.util.List;
import java.util.NoSuchElementException;

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
        Brand brand = findBrandById(brandId);
        if (!brand.getName().equals(name) && this.brandRepository.findByName(name).isPresent()) {
            throw new EntityExistsException("Brand with name: " + name + " already exist");
        }
        brand.setName(name);
        brand.setDescription(description);
        brand.setLinkToLogo(linkToLogo);
        brand.setRequestStatus(status.toLowerCase());
        this.brandRepository.save(brand);
    }

    @Override
    public void deleteBrandById(Long id) {
        this.brandRepository.deleteById(id);
    }

    @Override
    public List<Brand> findAllBrands(String status, Integer pageSize, Integer page, String sortedBy) {
        Sort sort = sortedBy == null ? Sort.unsorted() : Sort.by(sortedBy);
        Pageable pageable = Pageable.unpaged(sort);
        if (pageSize != null && page != null) {
            pageable = PageRequest.of(page, pageSize, sort);
        }
        Specification<Brand> statusSpec = BrandRepository.buildFindALlSpecificationByStatus(status);
        return brandRepository.findAll(statusSpec, pageable).toList();
    }


    @Override
    public Brand createBrand(String name, String description, String linkToLogo) {
        if (this.brandRepository.findByName(name).isPresent()) {
            throw new EntityExistsException("Brand with name: " + name + " already exist");
        }
        return this.brandRepository.save(new Brand(name, description, linkToLogo));
    }

    @Override
    public List<Brand> findAllBrandByIds(List<Long> brandIds) {
        return this.brandRepository.findAllById(brandIds);
    }

    @Override
    public List<Brand> findBrandsByNameLike(String name) {
        return brandRepository.findByNameLikeIgnoreCase(name);
    }

}
