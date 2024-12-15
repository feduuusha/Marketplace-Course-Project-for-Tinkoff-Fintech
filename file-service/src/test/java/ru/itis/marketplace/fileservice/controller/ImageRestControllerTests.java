package ru.itis.marketplace.fileservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.itis.marketplace.fileservice.service.ImageService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ImageRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    @DisplayName("Method: POST Endpoint: file-service/images/{bucketName} should call imageService.uploadImage(bucketName)")
    @WithMockUser(roles={"SERVICE"})
    public void uploadImageSuccessfulTest() throws Exception {
        // Arrange
        String bucketName = "test";
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String expectedUrl = "something";
        when(imageService.uploadImage(bucketName, multipartFile, null, null)).thenReturn(expectedUrl);

        // Act
        // Assert
        mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));
        verify(imageService).uploadImage(bucketName, multipartFile, null, null);
    }

    @Test
    @DisplayName("Method: POST Endpoint: file-service/images/{bucketName} should call imageService.uploadImage(bucketName) with width and height")
    @WithMockUser(roles={"SERVICE"})
    public void uploadImageSuccessfulWithWidthAndHeightTest() throws Exception {
        // Arrange
        String bucketName = "test";
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String expectedUrl = "something";
        int width = 100;
        int height = 120;
        when(imageService.uploadImage(bucketName, multipartFile, width, height)).thenReturn(expectedUrl);

        // Act
        // Assert
        mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile)
                        .param("width", String.valueOf(width))
                        .param("height", String.valueOf(height)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedUrl));
        verify(imageService).uploadImage(bucketName, multipartFile, width, height);
    }

    @Test
    @DisplayName("Method: GET Endpoint: file-service/images/{bucketName}/{objectKey} should call imageService.loadImage(bucketName, objectKey)")
    @WithAnonymousUser
    public void loadImageSuccessfulTest() throws Exception {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";
        byte[] expectedImage = new byte[]{1, 2, 3, 4, 5};
        when(imageService.loadImage(bucketName, objectKey)).thenReturn(expectedImage);

        // Act
        // Assert
        mockMvc.perform(get("/api/v1/file-service/images/{bucketName}/{objectKey}", bucketName, objectKey))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expectedImage));
        verify(imageService).loadImage(bucketName, objectKey);
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: file-service/images/{bucketName}/{objectKey} should call imageService.deleteImage(bucketName, objectKey)")
    @WithMockUser(roles={"SERVICE"})
    public void deleteImageSuccessfulTest() throws Exception {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/file-service/images/{bucketName}/{objectKey}", bucketName, objectKey))
                .andExpect(status().isNoContent());
        verify(imageService).deleteImage(bucketName, objectKey);
    }

    @Test
    @DisplayName("Method: POST Endpoint: file-service/images/{bucketName} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void uploadImageUnSuccessfulTest() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[]{1,2,3,4,5});
        String bucketName = "asdsad";
        // Act
        // Assert
        mockMvc.perform(multipart("/api/v1/file-service/images/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().is(401));
    }

    @Test
    @DisplayName("Method: DELETE Endpoint: file-service/images/{bucketName}/{objectKey} should return 401, because not auth-ed")
    @WithAnonymousUser
    public void deleteImageUnSuccessfulTest() throws Exception {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";

        // Act
        // Assert
        mockMvc.perform(delete("/api/v1/file-service/images/{bucketName}/{objectKey}", bucketName, objectKey))
                .andExpect(status().is(401));
    }
}