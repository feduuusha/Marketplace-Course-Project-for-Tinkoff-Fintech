package ru.itis.marketplace.userservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.userservice.client.BrandsRestClient;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.repository.UserBrandRepository;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.service.impl.UserBrandServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {UserBrandServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserBrandServiceTests {

    @Autowired
    private UserBrandService userBrandService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserBrandRepository userBrandRepository;
    @MockBean
    private BrandsRestClient brandsRestClient;

    @Test
    @DisplayName("declareBrandOwner should create userBrand entity")
    void declareBrandOwnerSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        Long brandId = 3L;
        UserBrand userBrand = new UserBrand(1L, userId, brandId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(brandsRestClient.brandWithIdExist(brandId)).thenReturn(true);
        when(userBrandRepository.findByBrandId(brandId)).thenReturn(Optional.empty());
        when(userBrandRepository.save(any())).thenReturn(userBrand);

        // Act
        UserBrand actualUserBrand = userBrandService.declareBrandOwner(userId, brandId);

        // Assert
        assertThat(actualUserBrand).isEqualTo(userBrand);
        verify(userBrandRepository).save(any());
    }

    @Test
    @DisplayName("declareBrandOwner should throw NotFoundException, because user not found")
    void declareBrandOwnerUnSuccessfulUserNotFoundTest() {
        // Arrange
        Long userId = 2L;
        Long brandId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userBrandService.declareBrandOwner(userId, brandId))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("declareBrandOwner should throw BadRequestException, because brand not found")
    void declareBrandOwnerUnSuccessfulBrandNotFoundTest() {
        // Arrange
        Long userId = 2L;
        Long brandId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(brandsRestClient.brandWithIdExist(brandId)).thenReturn(false);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userBrandService.declareBrandOwner(userId, brandId))
                .withMessage("Brand with ID: " + brandId + " does not exist");
    }

    @Test
    @DisplayName("declareBrandOwner should throw BadRequestException, because brand already property of user")
    void declareBrandOwnerUnSuccessfulBrandAlreadyPropertyOfUserTest() {
        // Arrange
        Long userId = 2L;
        Long brandId = 3L;
        UserBrand userBrand = new UserBrand(1L, 3L, brandId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(brandsRestClient.brandWithIdExist(brandId)).thenReturn(true);
        when(userBrandRepository.findByBrandId(brandId)).thenReturn(Optional.of(userBrand));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userBrandService.declareBrandOwner(userId, brandId))
                .withMessage("Brand with ID: " + brandId + " is already the property of the user with ID: " + userBrand.getUserId());
    }

    @Test
    @DisplayName("findAllUserBrands should return all userBrand ids")
    void findAllUserBrandsSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        List<UserBrand> userBrands = List.of(
                new UserBrand(1L, userId, 2L),
                new UserBrand(2L, userId, 4L),
                new UserBrand(3L, userId, 3L)
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userBrandRepository.findByUserId(userId)).thenReturn(userBrands);

        // Act
        List<Long> actualUserBrandsIds = userBrandService.findAllUserBrands(userId);

        // Assert
        assertThat(actualUserBrandsIds).isEqualTo(userBrands.stream().map(UserBrand::getBrandId).toList());
    }

    @Test
    @DisplayName("findAllUserBrands should throw NotFoundException, because user id incorrect")
    void findAllUserBrandsUnSuccessfulUserNotFoundTest() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userBrandService.findAllUserBrands(userId))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("findAllUserBrands should return empty list, because user do not have brands")
    void findAllUserBrandsSuccessfulEmptyListTest() {
        // Arrange
        Long userId = 2L;
        List<UserBrand> userBrands = List.of();
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userBrandRepository.findByUserId(userId)).thenReturn(userBrands);

        // Act
        List<Long> actualUserBrandsIds = userBrandService.findAllUserBrands(userId);

        // Assert
        assertThat(actualUserBrandsIds).isEqualTo(userBrands.stream().map(UserBrand::getBrandId).toList());
    }

    @Test
    @DisplayName("deleteUserBrand should call userBrandRepository.deleteByBrandId ")
    void deleteUserBrandSuccessfulTest() {
        // Arrange
        Long brandId = 3L;

        // Act
        userBrandService.deleteUserBrand(brandId);

        // Assert
        verify(userBrandRepository).deleteByBrandId(brandId);
    }

    @Test
    @DisplayName("findUserBrandByBrandId should return userBrand, because id is correct")
    void findUserBrandByBrandIdSuccessfulTest() {
        // Arrange
        Long brandId = 3L;
        UserBrand userBrand = new UserBrand(1L, 2L, brandId);
        when(userBrandRepository.findByBrandId(brandId)).thenReturn(Optional.of(userBrand));

        // Act
        UserBrand actualUserBrand = userBrandService.findUserBrandByBrandId(brandId);

        // Assert
        assertThat(actualUserBrand).isEqualTo(userBrand);
        verify(userBrandRepository).findByBrandId(brandId);
    }

    @Test
    @DisplayName("findUserBrandByBrandId should throw NotFoundException, because id is incorrect")
    void findUserBrandByBrandIdUnSuccessfulTest() {
        // Arrange
        Long brandId = 3L;
        when(userBrandRepository.findByBrandId(brandId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userBrandService.findUserBrandByBrandId(brandId))
                .withMessage("Brand with ID: " + brandId + " does not have owner");
    }
}
