package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.MarketPlaceUser;

public interface MarketPlaceUserService {
    MarketPlaceUser findUserByUsername(String username);
    MarketPlaceUser createUser(String email, String phoneNumber, String firstName, String lastName,
                               String username, String password, Long role);
    MarketPlaceUser findUserById(Long userId);
    void updateUserById(Long userId, String email, String phoneNumber, String firstName, String lastName, String username, String password, Long role);
    void deleteUserById(Long userId);
}
