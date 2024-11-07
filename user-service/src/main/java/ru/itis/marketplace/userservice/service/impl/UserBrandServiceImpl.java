package ru.itis.marketplace.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.client.BrandsRestClient;
import ru.itis.marketplace.userservice.entity.MarketPlaceUser;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.repository.MarketPlaceUserRepository;
import ru.itis.marketplace.userservice.repository.UserBrandRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserBrandServiceImpl implements UserBrandService {

    private final MarketPlaceUserRepository userRepository;
    private final UserBrandRepository userBrandRepository;
    private final BrandsRestClient brandsRestClient;

    @Override
    @Transactional
    public UserBrand addBrandToUser(Long userId, Long brandId) {
        Optional<MarketPlaceUser> optionalUser = this.userRepository.findById(userId);
        boolean brandExist = this.brandsRestClient.brandWithIdExist(brandId);
        if (optionalUser.isPresent() && brandExist) {
            return this.userBrandRepository.save(new UserBrand(optionalUser.get(), brandId));
        } else if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with ID: " + userId + " do not exist");
        } else {
            throw new BadRequestException("Brand with ID: " + brandId + " do not exist");
        }
    }

    @Override
    public List<Long> findAllUserBrands(Long userId) {
        Optional<MarketPlaceUser> optionalUser = this.userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return this.userBrandRepository.findByUserId(userId)
                    .stream()
                    .map(UserBrand::getBrandId)
                    .toList();
        } else {
            throw new NoSuchElementException("User with ID: " + userId + " do not exist");
        }
    }

    @Override
    @Transactional
    public void deleteUserBrand(Long userId, Long brandId) {
        this.userBrandRepository.deleteByBrandId(brandId);
    }
}
