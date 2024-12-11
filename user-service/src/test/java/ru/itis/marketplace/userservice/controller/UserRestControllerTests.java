package ru.itis.marketplace.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.userservice.config.SecurityBeans;
import ru.itis.marketplace.userservice.controller.payload.user.NewUserPayload;
import ru.itis.marketplace.userservice.controller.payload.user.UpdateUserPayload;
import ru.itis.marketplace.userservice.entity.Role;
import ru.itis.marketplace.userservice.entity.User;
import ru.itis.marketplace.userservice.service.UserService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserRestController.class})
@ActiveProfiles("test")
@Import(SecurityBeans.class)
class UserRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/by-username/{username} should return user by username")
    @WithMockUser(roles={"SERVICE"})
    void findUserByUsernameSuccessfulTest() throws Exception {
        // Arrange
        String username = "username";
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setId(1L);
        when(userService.findUserByUsername(username)).thenReturn(user);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-username/{username}", username))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User actualUser = mapper.readValue(response, User.class);
        assertThat(actualUser).isEqualTo(user);
        verify(userService).findUserByUsername(username);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/by-email/{email} should return user by email")
    @WithMockUser(roles={"SERVICE"})
    void findUserByEmailSuccessfulTest() throws Exception {
        // Arrange
        String email = "email";
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setId(1L);
        when(userService.findUserByEmail(email)).thenReturn(user);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-email/{email}", email))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User actualUser = mapper.readValue(response, User.class);
        assertThat(actualUser).isEqualTo(user);
        verify(userService).findUserByEmail(email);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/by-phone-number/{phoneNumber} should return user by phone-number")
    @WithMockUser(roles={"SERVICE"})
    void findUserByPhoneNumberSuccessfulTest() throws Exception {
        // Arrange
        String phoneNumber = "phoneNumber";
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setId(1L);
        when(userService.findUserByPhoneNumber(phoneNumber)).thenReturn(user);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-phone-number/{phoneNumber}", phoneNumber))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User actualUser = mapper.readValue(response, User.class);
        assertThat(actualUser).isEqualTo(user);
        verify(userService).findUserByPhoneNumber(phoneNumber);
    }

    @Test
    @DisplayName("Method: GET Endpoint: api/v1/user-service/users/{userId} should return user by id")
    @WithMockUser(roles={"SERVICE"})
    void findUserByIdSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 5L;
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        user.setId(userId);
        when(userService.findUserById(userId)).thenReturn(user);

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        User actualUser = mapper.readValue(response, User.class);
        assertThat(actualUser).isEqualTo(user);
        verify(userService).findUserById(userId);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users should create user")
    @WithMockUser(roles={"SERVICE"})
    void createUserSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 5L;
        ObjectMapper mapper = new ObjectMapper();
        String email = "email@mail.ru";
        String phoneNumber = "+79619091067";
        String firstName = "firstName";
        String lastName = "lastName";
        String username = "username";
        String password = "Password123";
        Set<String> roles = Set.of("CUSTOMER");
        User user = new User(email, phoneNumber, firstName, lastName, username, password, Set.of(new Role(12L, "CUSTOMER", null)));
        user.setId(userId);
        when(userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles)).thenReturn(user);

        // Act
        // Assert
        String response = mockMvc.perform(post("/api/v1/user-service/users")
                        .content(mapper.writeValueAsString(new NewUserPayload(email, phoneNumber, firstName, lastName, username, password, roles)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/user-service/users/5"))
                .andReturn().getResponse().getContentAsString();
        User actualUser = mapper.readValue(response, User.class);
        assertThat(actualUser).isEqualTo(user);
        verify(userService).createUser(email, phoneNumber, firstName, lastName, username, password, roles);
    }

    @Test
    @DisplayName("Method: POST Endpoint: api/v1/user-service/users should create user")
    @WithMockUser(roles={"SERVICE"})
    void createUserUnSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 5L;
        ObjectMapper mapper = new ObjectMapper();
        String email = "email@mail.ru";
        String phoneNumber = "89619091067";
        String firstName = "firstName";
        String lastName = "lastName";
        String username = "username";
        String password = "Password123";
        Set<String> roles = Set.of("CUSTOMER");
        User user = new User(email, phoneNumber, firstName, lastName, username, password, Set.of(new Role(12L, "CUSTOMER", null)));
        user.setId(userId);
        when(userService.createUser(email, phoneNumber, firstName, lastName, username, password, roles)).thenReturn(user);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/user-service/users")
                        .content(mapper.writeValueAsString(new NewUserPayload(email, phoneNumber, firstName, lastName, username, password, roles)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users should update user")
    @WithMockUser(roles={"SERVICE"})
    void updateUserByIdSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String email = "email@mail.ru";
        String phoneNumber = "+79619091067";
        String firstName = "firstName";
        String lastName = "lastName";
        String username = "username";
        String password = "Password123";
        Set<String> roles = Set.of("CUSTOMER");
        Long userId = 300L;


        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}", userId)
                        .content(mapper.writeValueAsString(new UpdateUserPayload(email, phoneNumber, firstName, lastName, username, password, roles)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(userService).updateUserById(userId, email, phoneNumber, firstName, lastName, username, password, roles);
    }

    @Test
    @DisplayName("Method: PUT Endpoint: api/v1/user-service/users should return 400, because payload is incorrect")
    @WithMockUser(roles={"SERVICE"})
    void updateUserByIdUnSuccessfulTest() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        String email = "email";
        String phoneNumber = "+79619091067";
        String firstName = "firstName";
        String lastName = "lastName";
        String username = "username";
        String password = "Password123";
        Set<String> roles = Set.of("CUSTOMER");
        Long userId = 300L;


        // Act
        // Assert
        mockMvc.perform(put("/api/v1/user-service/users/{userId}", userId)
                        .content(mapper.writeValueAsString(new UpdateUserPayload(email, phoneNumber, firstName, lastName, username, password, roles)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: api/v1/user-service/users should delete user")
    @WithMockUser(roles={"SERVICE"})
    void deleteUserByIdSuccessfulTest() throws Exception {
        // Arrange
        Long userId = 300L;


        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/user-service/users/{userId}", userId))
                .andExpect(status().isNoContent());
        verify(userService).deleteUserById(userId);
    }
}
