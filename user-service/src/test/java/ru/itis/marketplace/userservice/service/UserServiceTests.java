package ru.itis.marketplace.userservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.userservice.entity.Role;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.exception.BadRequestException;
import ru.itis.marketplace.userservice.exception.NotFoundException;
import ru.itis.marketplace.userservice.repository.RoleRepository;
import ru.itis.marketplace.userservice.repository.UserRepository;
import ru.itis.marketplace.userservice.service.impl.UserServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UserServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private PasswordEncoder bCryptPasswordEncoder;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("findUserByUsername should return user, because username is correct")
    void findUserByUsernameSuccessfulTest() {
        // Arrange
        String username = "username";
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        User actualUser = userService.findUserByUsername(username);

        // Assert
        assertThat(actualUser).isEqualTo(user);
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("findUserByUsername should return NotFoundException, because username is incorrect")
    void findUserByUsernameUnSuccessfulTest() {
        // Arrange
        String username = "doNotExist";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.findUserByUsername(username))
                .withMessage("User with username: " + username + " not found");
    }

    @Test
    @DisplayName("findUserByEmail should return user, because email is correct")
    void findUserByEmailSuccessfulTest() {
        // Arrange
        String email = "email";
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User actualUser = userService.findUserByEmail(email);

        // Assert
        assertThat(actualUser).isEqualTo(user);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("findUserByEmail should return NotFoundException, because email is incorrect")
    void findUserByEmailUnSuccessfulTest() {
        // Arrange
        String email = "doNotExist";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.findUserByEmail(email))
                .withMessage("User with email: " + email + " not found");
    }

    @Test
    @DisplayName("findUserByPhoneNumber should return user, because phone number is correct")
    void findUserByPhoneNumberSuccessfulTest() {
        // Arrange
        String phoneNumber = "+79999999999";
        User user = new User();
        user.setId(1L);
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        // Act
        User actualUser = userService.findUserByPhoneNumber(phoneNumber);

        // Assert
        assertThat(actualUser).isEqualTo(user);
        verify(userRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    @DisplayName("findUserByPhone should return NotFoundException, because phone number is incorrect")
    void findUserByPhoneUnSuccessfulTest() {
        // Arrange
        String phoneNumber = "+79999999999";
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.findUserByPhoneNumber(phoneNumber))
                .withMessage("User with phone-number: " + phoneNumber + " not found");
    }

    @Test
    @DisplayName("createUser should create user, because payload is correct")
    void createUserSuccessfulTest() {
        // Arrange
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Role role1 = new Role(1L, role, null);
        Set<String> roles = Set.of(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
        when(roleRepository.findByName(role)).thenReturn(Optional.of(role1));
        when(meterRegistry.counter(any())).thenReturn(counter);
        User user = new User(email, phoneNumber, firstName, lastName, username, password, Set.of(role1));
        user.setId(1L);
        when(userRepository.save(any())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(password)).thenReturn(password);

        // Act
        User actualUser = userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles);

        // Assert
        assertThat(actualUser).isEqualTo(user);
        verify(userRepository).findByEmail(email);
        verify(userRepository).findByUsername(username);
        verify(userRepository).findByPhoneNumber(phoneNumber);
        verify(roleRepository).findByName(role);
        verify(userRepository).save(any());
        verify(bCryptPasswordEncoder).encode(password);
    }

    @Test
    @DisplayName("createUser should throw BadRequestException, because payload is incorrect same email")
    void createUserUnSuccessfulSameEmailTest() {
        // Arrange
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with email: " + email + " already exist");
    }

    @Test
    @DisplayName("createUser should throw BadRequestException, because payload is incorrect same username")
    void createUserUnSuccessfulSameUsernameTest() {
        // Arrange
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with username: " + username + " already exist");
    }

    @Test
    @DisplayName("createUser should throw BadRequestException, because payload is incorrect same phone-number")
    void createUserUnSuccessfulSamePhoneNumberTest() {
        // Arrange
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new User()));

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with phone-number: " + phoneNumber + " already exist");
    }

    @Test
    @DisplayName("createUser should throw BadRequestException, because payload is incorrect role does not exist")
    void createUserUnSuccessfulRoleDoesNotExistTest() {
        // Arrange
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
        when(roleRepository.findByName(role)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("Role with name: " + role + " does not exist");
    }

    @Test
    @DisplayName("findUserById should return user, because user id is correct")
    void findUserByIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User actualUser = userService.findUserById(userId);

        // Assert
        assertThat(actualUser).isEqualTo(user);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("findUserById should throw NotFoundException, because user with specified id not found")
    void findUserByIdUnSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.findUserById(userId))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("updateUserById should update user, because payload is correct")
    void updateUserByIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Role role1 = new Role(1L, role, null);
        Set<String> roles = Set.of(role);
        User user = new User("email2", "phone2", "fname2", "lname2", "username2", "password2", Set.of());
        user = spy(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
        when(roleRepository.findByName(role)).thenReturn(Optional.of(role1));
        when(meterRegistry.counter(any())).thenReturn(counter);
        user.setId(1L);
        when(userRepository.save(any())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(password)).thenReturn(password);

        // Act
        userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles);

        // Assert
        verify(userRepository).findByEmail(email);
        verify(userRepository).findByUsername(username);
        verify(userRepository).findByPhoneNumber(phoneNumber);
        verify(roleRepository).findByName(role);
        verify(userRepository).save(any());
        verify(bCryptPasswordEncoder).encode(password);
        verify(user).setEmail(email);
        verify(user).setUsername(username);
        verify(user).setPhoneNumber(phoneNumber);
        verify(user).setPassword(password);
        verify(user).setFirstName(firstName);
        verify(user).setLastName(lastName);
        verify(user).setRoles(Set.of(role1));
    }

    @Test
    @DisplayName("updateUserById should throw NotFoundException user, because user is not found")
    void updateUserByIdUnSuccessfulUserNotFoundTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with ID: " + userId + " not found");
    }

    @Test
    @DisplayName("updateUserById should throw BadRequestException, because email already exist")
    void updateUserByIdUnSuccessfulAlreadyExistEmailTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        User user = new User("email2", "phone2", "fname2", "lname2", "username2", "password2", Set.of());
        user = spy(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));


        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with email: " + email + " already exist");
    }

    @Test
    @DisplayName("updateUserById should throw BadRequestException, because username already exist")
    void updateUserByIdUnSuccessfulAlreadyExistUsernameTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        User user = new User("email2", "phone2", "fname2", "lname2", "username2", "password2", Set.of());
        user = spy(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));


        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with username: " + username + " already exist");
    }

    @Test
    @DisplayName("updateUserById should throw BadRequestException, because phone-number already exist")
    void updateUserByIdUnSuccessfulAlreadyExistPhoneNumberTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        User user = new User("email2", "phone2", "fname2", "lname2", "username2", "password2", Set.of());
        user = spy(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(new User()));


        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("User with phone-number: " + phoneNumber + " already exist");
    }

    @Test
    @DisplayName("updateUserById should throw BadRequestException, because role with specified name not found")
    void updateUserByIdUnSuccessfulRoleNotFoundTest() {
        // Arrange
        Long userId = 2L;
        String email = "email";
        String phoneNumber = "phone";
        String firstName = "fName";
        String lastName = "sName";
        String username = "username";
        String password = "password";
        String role = "CUSTOMER";
        Set<String> roles = Set.of(role);
        User user = new User("email2", "phone2", "fname2", "lname2", "username2", "password2", Set.of());
        user = spy(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn(password);
        when(roleRepository.findByName(role)).thenReturn(Optional.empty());


        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles))
                .withMessage("Role with name: " + role + " does not exist");
    }

    @Test
    @DisplayName("deleteUserById should call userRepository.deleteById")
    void deleteUserByIdSuccessfulTest() {
        // Arrange
        Long userId = 2L;

        // Act
        userService.deleteUserById(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }
}

