package ru.itis.marketplace.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.marketplace.userservice.entity.UserBrand;

import java.util.List;

public interface UserBrandRepository extends JpaRepository<UserBrand, Long> {
    List<UserBrand> findByUserId(Long userId);
    void deleteByBrandId(Long brandId);
}
