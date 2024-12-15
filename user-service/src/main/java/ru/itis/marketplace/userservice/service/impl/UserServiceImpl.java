package ru.itis.marketplace.userservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.entity.Role;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.repository.RoleRepository;
import ru.itis.marketplace.userservice.service.UserService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final MeterRegistry meterRegistry;

    @Override
    public User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username: " + username + " not found"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " not found"));
    }

    @Override
    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("User with phone-number: " + phoneNumber + " not found"));
    }

    @Override
    @Transactional
    public User createUser(String email, String phoneNumber, String firstName, String lastName, String username, String password, Set<String> roles) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("User with email: " + email + " already exist");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with username: " + username + " already exist");
        }
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with phone-number: " + phoneNumber + " already exist");
        }
        Set<Role> userRoles = roles.stream()
                .map((roleName) -> roleRepository
                        .findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role with name: " + roleName + " does not exist")))
                .collect(Collectors.toSet());
        var user = userRepository.save(new User(email, phoneNumber, firstName, lastName,
                username, bCryptPasswordEncoder.encode(password), userRoles));
        meterRegistry.counter("count of new users").increment();
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
    }

    @Override
    @Transactional
    public void updateUserById(Long userId, String email, String phoneNumber, String firstName, String lastName, String username, String password, Set<String> roles) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID: " + userId + " not found"));
        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("User with email: " + email + " already exist");
        }
        user.setEmail(email);
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("User with username: " + username + " already exist");
        }
        user.setUsername(username);
        if (!user.getPhoneNumber().equals(phoneNumber) && userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BadRequestException("User with phone-number: " + phoneNumber + " already exist");
        }
        user.setPhoneNumber(phoneNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRoles(roles.stream()
                .map((roleName) -> roleRepository
                        .findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role with name: " + roleName + " does not exist")))
                .collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

}
