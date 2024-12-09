package ru.itis.marketplace.catalogservice.exception;

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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.itis.marketplace.catalogservice.config.SecurityBeans;
import ru.itis.marketplace.catalogservice.controller.ProductRestController;
import ru.itis.marketplace.catalogservice.service.ProductService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductRestController.class)
@ActiveProfiles("test")
@Import({SecurityBeans.class, GlobalExceptionHandlerControllerAdvice.class})
public class GlobalExceptionHandlerControllerAdviceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("api/v1/catalog/products/{productId} should throw NotFoundException and exception handler should return 404")
    @WithMockUser(roles = {"SERVICE"})
    public void handleNotFoundExceptionTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String message = "message";
        ObjectMapper objectMapper = new ObjectMapper();
        when(productService.findProductById(productId)).thenThrow(new NotFoundException(message));

        // Act
        // Assert
        String result = mockMvc.perform(get("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(404);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");

        softly.assertAll();
    }

    @Test
    @DisplayName("api/v1/catalog/products/{productId} should throw BadRequestException and exception handler should return 400")
    @WithMockUser(roles = {"SERVICE"})
    public void handleBadRequestExceptionTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String message = "message";
        ObjectMapper objectMapper = new ObjectMapper();
        when(productService.findProductById(productId)).thenThrow(new BadRequestException(message));

        // Act
        // Assert
        String result = mockMvc.perform(get("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(400);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");

        softly.assertAll();
    }

    @Test
    @DisplayName("api/v1/catalog/products/{productId} should throw IllegalStateException and exception handler should return 500")
    @WithMockUser(roles = {"SERVICE"})
    public void handleIllegalStateExceptionTest() throws Exception {
        // Arrange
        Long productId = 2L;
        String message = "message";
        ObjectMapper objectMapper = new ObjectMapper();
        when(productService.findProductById(productId)).thenThrow(new IllegalStateException(message));

        // Act
        // Assert
        String result = mockMvc.perform(get("/api/v1/catalog/products/{productId}", productId))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(500);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(message);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Error on the server");

        softly.assertAll();
    }
}
