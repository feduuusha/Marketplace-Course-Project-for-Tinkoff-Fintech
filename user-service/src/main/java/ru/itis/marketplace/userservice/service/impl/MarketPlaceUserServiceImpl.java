package ru.itis.marketplace.userservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.userservice.entity.MarketPlaceUser;
import ru.itis.marketplace.userservice.entity.Role;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.repository.MarketPlaceUserRepository;
import ru.itis.marketplace.userservice.repository.RoleRepository;
import ru.itis.marketplace.userservice.service.MarketPlaceUserService;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class MarketPlaceUserServiceImpl implements MarketPlaceUserService {
    private final MarketPlaceUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public MarketPlaceUser findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User with Username: " + username + " do not exist"));
    }

    @Override
    public MarketPlaceUser findUserById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " do not exist"));
    }

    @Override
    @Transactional
    public void updateUserById(Long userId, String email, String phoneNumber, String firstName, String lastName,
                               String username, String password, Long roleId) {
        Optional<MarketPlaceUser> userOptional = this.userRepository.findById(userId);
        if (userOptional.isPresent()) {
            MarketPlaceUser user = userOptional.get();
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            if (this.userRepository.findByUsername(username).isEmpty()) {
                user.setUsername(username);
                user.setPassword(password);
                Optional<Role> roleOptional = this.roleRepository.findById(roleId);
                if (roleOptional.isPresent()) {
                    Role role = roleOptional.get();
                    user.setRoles(Set.of(role));
                } else {
                    throw new BadRequestException("Role with ID: " + roleId + " do not exist");
                }
            } else {
                throw new BadRequestException("User with Username: " + username + " already exist");
            }
        } else {
            throw new NoSuchElementException("User with ID: " + userId + " do not exist");
        }
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        this.userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public MarketPlaceUser createUser(String email, String phoneNumber, String firstName, String lastName,
                                      String username, String password, Long roleId) {
        Optional<Role> optionalRole = this.roleRepository.findById(roleId);
        if (optionalRole.isPresent()) {
            if (this.userRepository.findByUsername(username).isEmpty()) {
                return this.userRepository.save(new MarketPlaceUser(email, phoneNumber, firstName, lastName,
                        username, passwordEncoder.encode(password), Set.of(optionalRole.get())));
            } else {
                throw new BadRequestException("User with username " + username + " already exist");
            }
        } else {
            throw new BadRequestException("Role with id " + roleId + " was not found");
        }
    }
}
