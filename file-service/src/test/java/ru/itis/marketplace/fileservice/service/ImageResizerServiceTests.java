package ru.itis.marketplace.fileservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import ru.itis.marketplace.fileservice.service.impl.ImageResizerServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ImageResizerServiceImpl.class})
@ActiveProfiles("test")
public class ImageResizerServiceTests {

    @Autowired
    private ImageResizerService resizerService;
    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private Counter counter;

    @Test
    @DisplayName("Method resizeImage should resize image, because image is correct")
    public void resizeImageSuccessfulTest() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("cat.jpeg");
        InputStream file = resource.getInputStream();
        int width = 100;
        int height = 100;
        String formatName = "jpeg";
        when(meterRegistry.counter(anyString())).thenReturn(counter);

        // Act
        InputStream image = resizerService.resizeImage(file, width, height, formatName);

        // Assert
        BufferedImage resizedImage = ImageIO.read(image);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(resizedImage.getHeight()).isEqualTo(height);
        softly.assertThat(resizedImage.getWidth()).isEqualTo(width);

        softly.assertAll();
    }

    @Test
    @DisplayName("Method resizeImage should throw IllegalStateException, because file is incorrect")
    public void resizeImageUnSuccessfulTest() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("file.jpeg");
        InputStream file = resource.getInputStream();
        int width = 100;
        int height = 100;
        String formatName = "jpeg";

        // Act
        // Assert
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> resizerService.resizeImage(file, width, height, formatName))
                .withMessage("Exception while resizing image: Exception while read image");
    }
}
