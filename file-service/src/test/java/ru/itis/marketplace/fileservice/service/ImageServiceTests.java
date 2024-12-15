package ru.itis.marketplace.fileservice.service;

import io.awspring.cloud.s3.Location;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import ru.itis.marketplace.fileservice.exception.BadRequestException;
import ru.itis.marketplace.fileservice.service.impl.ImageServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ImageServiceImpl.class})
@ActiveProfiles("test")
public class ImageServiceTests {

    @Autowired
    private ImageService imageService;
    @MockBean
    private S3Template s3Template;
    @MockBean
    private ImageResizerService resizerService;
    @MockBean
    private S3Resource s3Resource;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;
    @MockBean
    private MultipartFile file;


    @Test
    @DisplayName("Method loadImage should return correct byte array, because input is correct")
    void loadImageSuccessfulTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";
        byte[] mockImage = new byte[]{1, 2, 3, 4, 5};
        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(s3Template.download(bucketName, objectKey)).thenReturn(s3Resource);
        when(s3Resource.getInputStream()).thenReturn(new ByteArrayInputStream(mockImage));

        // Act
        byte[] image = imageService.loadImage(bucketName, objectKey);

        // Assert
        assertThat(image).isEqualTo(mockImage);
        verify(counter).increment();
    }

    @Test
    @DisplayName("Method deleteImage should throw IllegalStateException, because s3Template throw IOException")
    void loadImageUnSuccessfulTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";
        String exceptionMessage = "message";
        when(s3Template.download(bucketName, objectKey)).thenReturn(s3Resource);
        when(s3Resource.getInputStream()).thenThrow(new IOException(exceptionMessage));

        // Act
        // Assert
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> imageService.loadImage(bucketName, objectKey))
                .withMessage("It is impossible to load a image, because: " + exceptionMessage);

    }

    @Test
    @DisplayName("Method deleteImage should call s3Template.deleteObject")
    void deleteImageSuccessfulTest() {
        // Arrange
        String bucketName = "bucket";
        String objectKey = "key";
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        imageService.deleteImage(bucketName, objectKey);

        // Assert
        verify(s3Template).deleteObject(bucketName, objectKey);
        verify(counter).increment();
    }

    @Test
    @DisplayName("Method uploadImage should return call s3Template.upload, because input is correct")
    void uploadImageSuccessfulTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;
        String fileName = "name.png";
        String extension = "png";
        String key = "key";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});

        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);
        when(resizerService.resizeImage(inputStream, width, height, extension)).thenReturn(inputStream);
        when(s3Template.upload(eq(bucketName),
                anyString(), eq(inputStream))).thenReturn(s3Resource);
        when(s3Resource.getLocation()).thenReturn(Location.of(bucketName, key));

        // Act
        String url = imageService.uploadImage(bucketName, file, width, height);

        // Assert
        assertThat(url).isEqualTo(bucketName + "/" + key);
        verify(resizerService).resizeImage(inputStream, width, height, extension);
        verify(counter).increment();
    }

    @Test
    @DisplayName("Method uploadImage should throw UnsupportedOperationException, because file type is incorrect")
    void uploadImageUnSuccessfulFilTypeTest() {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;


        when(file.getContentType()).thenReturn("something");


        // Act
        // Assert
        assertThatExceptionOfType(UnsupportedMediaTypeStatusException.class)
                .isThrownBy(() -> imageService.uploadImage(bucketName, file, width, height))
                .withMessage("415 UNSUPPORTED_MEDIA_TYPE \"Type " + file.getContentType() + " is unsupported\"");
    }

    @Test
    @DisplayName("Method uploadImage should throw BadRequestException, because file name is null")
    void uploadImageUnSuccessfulFileNameIsNullTest() {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;

        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(null);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> imageService.uploadImage(bucketName, file, width, height))
                .withMessage("File name is null...");
    }

    @Test
    @DisplayName("Method uploadImage should throw BadRequestException, because file do not have extension")
    void uploadImageUnSuccessfulFileDoNotHaveExtensionTest() {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;
        String fileName = "name";

        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(fileName);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> imageService.uploadImage(bucketName, file, width, height))
                .withMessage("File should have extension");
    }

    @Test
    @DisplayName("Method uploadImage should throw BadRequestException, because file have extension .")
    void uploadImageUnSuccessfulFileHaveIncorrectExtensionTest() {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;
        String fileName = "name.";

        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(fileName);

        // Act
        // Assert
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> imageService.uploadImage(bucketName, file, width, height))
                .withMessage("File should have extension");
    }

    @Test
    @DisplayName("Method uploadImage should do not resize image if width or height is null")
    void uploadImageResizeTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        Integer width = null;
        Integer height = null;
        String fileName = "name.png";
        String key = "key";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});

        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);
        when(s3Template.upload(eq(bucketName),
                anyString(), eq(inputStream))).thenReturn(s3Resource);
        when(s3Resource.getLocation()).thenReturn(Location.of(bucketName, key));

        // Act
        imageService.uploadImage(bucketName, file, width, height);

        // Assert
        verifyNoInteractions(resizerService);
    }

    @Test
    @DisplayName("Method uploadImage should do not resize image file type is svg")
    void uploadImageSvgResizeTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        Integer width = null;
        Integer height = null;
        String fileName = "name.svg";
        String key = "key";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});

        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(file.getContentType()).thenReturn("image/svg+xml");
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);
        when(s3Template.upload(eq(bucketName),
                anyString(), eq(inputStream))).thenReturn(s3Resource);
        when(s3Resource.getLocation()).thenReturn(Location.of(bucketName, key));

        // Act
        imageService.uploadImage(bucketName, file, width, height);

        // Assert
        verifyNoInteractions(resizerService);
    }

    @Test
    @DisplayName("Method uploadImage should throw IllegalStateException, because file input stream is incorrect")
    void uploadImageUnSuccessfulFileInputStreamIsIncorrectTest() throws IOException {
        // Arrange
        String bucketName = "bucket";
        int width = 100;
        int height = 100;
        String fileName = "name.png";
        String exceptionMessage = "message";

        when(meterRegistry.counter(anyString())).thenReturn(counter);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenThrow(new IOException(exceptionMessage));

        // Act
        // Assert
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> imageService.uploadImage(bucketName, file, width, height))
                .withMessage("It is impossible to upload a image, because: " + exceptionMessage);
    }
}
