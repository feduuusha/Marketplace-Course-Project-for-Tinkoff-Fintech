package ru.itis.marketplace.catalogservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.exception.BadRequestException;
import ru.itis.marketplace.catalogservice.exception.NotFoundException;
import ru.itis.marketplace.catalogservice.kafka.KafkaProducer;
import ru.itis.marketplace.catalogservice.repository.BrandRepository;
import ru.itis.marketplace.catalogservice.service.BrandService;
import ru.itis.marketplace.catalogservice.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductService productService;
    private final KafkaProducer kafkaProducer;
    private final MeterRegistry meterRegistry;

    @Override
    public Brand findBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new NotFoundException("Brand with ID: " + id + " not found"));
    }

    @Override
    @Transactional
    public void updateBrandById(Long brandId, String name, String description, String linkToLogo, String status) {
        Brand brand = findBrandById(brandId);
        if (!brand.getName().equals(name) && brandRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Brand with name: " + name + " already exist");
        }
        brand.setName(name);
        brand.setDescription(description);
        brand.setLinkToLogo(linkToLogo);
        brand.setRequestStatus(status.toLowerCase());
        brandRepository.save(brand);
    }

    @Override
    public void deleteBrandById(Long id) {
        var products = productService.findAllProducts(null, null, null, null, null, null, null, id, null);
        var sizeIds = products.stream().flatMap(product -> product.getSizes().stream()).map(ProductSize::getId).toList();
        kafkaProducer.sendSizeIds(sizeIds);
        kafkaProducer.sendBrandIds(List.of(id));
        brandRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Brand> findAllBrands(String status, Integer pageSize, Integer page, String sortedBy) {
        Sort sort = sortedBy == null ? Sort.unsorted() : Sort.by(sortedBy);
        Pageable pageable = Pageable.unpaged(sort);
        if (pageSize != null && page != null) {
            pageable = PageRequest.of(page, pageSize, sort);
        }
        Specification<Brand> statusSpec = BrandRepository.buildFindAllSpecificationByStatus(status);
        var brands = brandRepository.findAll(statusSpec, pageable).toList();
        brandRepository.joinLinksToBrandsWithIds(brands.stream().map(Brand::getId).toList());
        brandRepository.joinPhotosToBrandsWithIds(brands.stream().map(Brand::getId).toList());
        return brands;
    }


    @Override
    public Brand createBrand(String name, String description, String linkToLogo) {
        if (brandRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Brand with name: " + name + " already exist");
        }
        var brand = brandRepository.save(new Brand(name, description, linkToLogo));
        meterRegistry.counter("count of created brands").increment();
        return brand;
    }

    @Override
    public List<Brand> findAllBrandByIds(List<Long> brandIds) {
        return brandRepository.findAllById(brandIds);
    }

    @Override
    @Transactional
    public List<Brand> findBrandsByNameLike(String name) {
        var brands = brandRepository.findByNameLikeIgnoreCase(name);
        brandRepository.joinLinksToBrandsWithIds(brands.stream().map(Brand::getId).toList());
        brandRepository.joinPhotosToBrandsWithIds(brands.stream().map(Brand::getId).toList());
        return brands;
    }

    @Override
    @Transactional
    public void updateBrandStatusById(Long brandId, String requestStatus) {
        Brand brand = findBrandById(brandId);
        brand.setRequestStatus(requestStatus);
        brandRepository.save(brand);
    }

}
