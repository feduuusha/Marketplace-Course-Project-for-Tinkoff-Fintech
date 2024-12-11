package ru.itis.marketplace.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.client.BrandsRestClient;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.repository.UserBrandRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBrandServiceImpl implements UserBrandService {

    private final UserRepository userRepository;
    private final UserBrandRepository userBrandRepository;
    private final BrandsRestClient brandsRestClient;

    @Override
    @Transactional
    public UserBrand declareBrandOwner(Long userId, Long brandId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        boolean brandExist = brandsRestClient.brandWithIdExist(brandId);
        if (!brandExist) {
            throw new BadRequestException("Brand with ID: " + brandId + " does not exist");
        }
        var userBrand = userBrandRepository.findByBrandId(brandId);
        if (userBrand.isPresent()) {
            throw new BadRequestException("Brand with ID: " + brandId + " is already the property of the user with ID: " + userBrand.get().getUserId());
        }
        return userBrandRepository.save(new UserBrand(userId, brandId));
    }

    @Override
    public List<Long> findAllUserBrands(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        return userBrandRepository.findByUserId(userId)
                .stream()
                .map(UserBrand::getBrandId)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUserBrand(Long brandId) {
        userBrandRepository.deleteByBrandId(brandId);
    }

    @Override
    public UserBrand findUserBrandByBrandId(Long brandId) {
        return userBrandRepository
                .findByBrandId(brandId)
                .orElseThrow(() -> new NotFoundException("Brand with ID: " + brandId + " does not have owner"));
    }
}
