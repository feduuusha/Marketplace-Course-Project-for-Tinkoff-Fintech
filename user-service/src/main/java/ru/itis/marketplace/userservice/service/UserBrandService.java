package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.UserBrand;

import java.util.List;

public interface UserBrandService {
    UserBrand addBrandToUser(Long userId, Long brandId);

    List<Long> findAllUserBrands(Long userId);

    void deleteUserBrand(Long userId, Long brandId);
}
