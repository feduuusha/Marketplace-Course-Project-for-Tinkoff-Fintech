package ru.itis.marketplace.fileservice.exception;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.itis.marketplace.fileservice.service.ImageService;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GlobalExceptionHandlerControllerAdviceTests {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService imageService;


    @Test
    @DisplayName("api/v1/file-service/images/bucket should throw NoSuchBucketException, because bucket name not found and exception handler should return 404")
    @WithMockUser(roles = {"SERVICE"})
    public void handleNotFoundExceptionTest() throws Exception {
        // Arrange
        String bucketName = "test";
        ObjectMapper objectMapper = new ObjectMapper();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String exceptionMessage = "something";
        when(imageService.uploadImage(bucketName, multipartFile, null, null)).thenThrow(NoSuchBucketException
                .builder()
                .message(exceptionMessage)
                .build());

        // Act
        // Assert
        String result = mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(404);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(exceptionMessage);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Resource Not Found");

        softly.assertAll();
    }


    @Test
    @DisplayName("api/v1/file-service/images/bucket should throw BadRequestException and exception handler should return 400")
    @WithMockUser(roles = {"SERVICE"})
    public void handleBadRequestException() throws Exception {
        // Arrange
        String bucketName = "test";
        ObjectMapper objectMapper = new ObjectMapper();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String exceptionMessage = "something";
        when(imageService.uploadImage(bucketName, multipartFile, null, null)).thenThrow(new BadRequestException(exceptionMessage));


        // Act
        // Assert
        String result = mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(400);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(exceptionMessage);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");

        softly.assertAll();
    }


    @Test
    @DisplayName("api/v1/file-service/images/bucket should throw IllegalStateException and exception handler should return 500")
    @WithMockUser(roles = {"SERVICE"})
    public void handleIllegalsException() throws Exception {
        // Arrange
        String bucketName = "test";
        ObjectMapper objectMapper = new ObjectMapper();
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String exceptionMessage = "something";
        when(imageService.uploadImage(bucketName, multipartFile, null, null)).thenThrow(new IllegalStateException(exceptionMessage));


        // Act
        // Assert
        String result = mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();
        ProblemDetail problemDetail = objectMapper.readValue(result, ProblemDetail.class);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(problemDetail.getStatus()).isEqualTo(500);
        softly.assertThat(problemDetail.getDetail()).isEqualTo(exceptionMessage);
        softly.assertThat(problemDetail.getType().toString()).isEqualTo("/swagger-ui/index.html");
        softly.assertThat(problemDetail.getTitle()).isEqualTo("Error on the server");

        softly.assertAll();
    }
}
