package ru.itis.marketplace.userservice.repository;

import ru.itis.marketplace.userservice.entity.MarketPlaceUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MarketPlaceUserRepository extends JpaRepository<MarketPlaceUser, Long> {
    Optional<MarketPlaceUser> findByUsername(String username);
}
