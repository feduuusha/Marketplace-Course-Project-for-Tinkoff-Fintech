package ru.itis.marketplace.userservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.userservice.config.SecurityBeans;
import ru.itis.marketplace.userservice.controller.UserRestController;
import ru.itis.marketplace.userservice.service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserRestController.class})
@ActiveProfiles("test")
@Import({SecurityBeans.class, GlobalExceptionHandlerControllerAdvice.class})
class GlobalExceptionHandlerControllerAdviceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("user service should throw BadRequestException and Exception handler should return Problem Detail with 400")
    @WithMockUser(roles={"SERVICE"})
    void handleBadRequestExceptionTest() throws Exception {
        // Arrange
        String username = "username";
        ObjectMapper mapper = new ObjectMapper();
        String message = "message";
        when(userService.findUserByUsername(username)).thenThrow(new BadRequestException(message));

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-username/{username}", username))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = mapper.readValue(response, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(400);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");

        softly.assertAll();
    }

    @Test
    @DisplayName("user service should throw NotFoundException and Exception handler should return Problem Detail with 404")
    @WithMockUser(roles={"SERVICE"})
    void handleNotFoundExceptionTest() throws Exception {
        // Arrange
        String username = "username";
        ObjectMapper mapper = new ObjectMapper();
        String message = "message";
        when(userService.findUserByUsername(username)).thenThrow(new NotFoundException(message));

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-username/{username}", username))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = mapper.readValue(response, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(404);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");

        softly.assertAll();
    }

    @Test
    @DisplayName("user service should throw UnavailableServiceException and Exception handler should return Problem Detail with 503")
    @WithMockUser(roles={"SERVICE"})
    void handleUnavailableServiceExceptionTest() throws Exception {
        // Arrange
        String username = "username";
        ObjectMapper mapper = new ObjectMapper();
        String message = "message";
        when(userService.findUserByUsername(username)).thenThrow(new UnavailableServiceException(message));

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-username/{username}", username))
                .andExpect(status().is(503))
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = mapper.readValue(response, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(503);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("External Service Unavailable");

        softly.assertAll();
    }

    @Test
    @DisplayName("user service should throw IllegalStateException and Exception handler should return Problem Detail with 500")
    @WithMockUser(roles={"SERVICE"})
    void handleIllegalsExceptionExceptionTest() throws Exception {
        // Arrange
        String username = "username";
        ObjectMapper mapper = new ObjectMapper();
        String message = "message";
        when(userService.findUserByUsername(username)).thenThrow(new IllegalStateException(message));

        // Act
        // Assert
        String response = mockMvc.perform(get("/api/v1/user-service/users/by-username/{username}", username))
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = mapper.readValue(response, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(500);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Error on the server");

        softly.assertAll();
    }
}
