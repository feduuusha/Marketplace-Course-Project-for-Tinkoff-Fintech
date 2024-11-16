package ru.itis.marketplace.userservice.service;

import ru.itis.marketplace.userservice.entity.User;

import java.util.Set;

public interface UserService {
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByPhoneNumber(String phoneNumber);
    User createUser(String email, String phoneNumber, String firstName, String lastName,
                    String username, String password, Set<String> roles);
    User findUserById(Long userId);
    void updateUserById(Long userId, String email, String phoneNumber, String firstName,
                        String lastName, String username, String password, Set<String> roles);
    void deleteUserById(Long userId);
}
