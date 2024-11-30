package ru.itis.marketplace.fileservice.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.fileservice.service.ImageResizerService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageResizerServiceImpl implements ImageResizerService {

    private final MeterRegistry meterRegistry;

    @Override
    public InputStream resizeImage(InputStream image, int width, int height, String formatName) {
        try {
            BufferedImage originalImage = ImageIO.read(image);

            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g2d = resizedImage.createGraphics();

            double xScale = (double) width / originalImage.getWidth();
            double yScale = (double) height / originalImage.getHeight();
            double scale = Math.max(xScale, yScale);

            g2d.drawImage(originalImage, 0, 0, (int) (originalImage.getWidth() * scale),
                    (int) (originalImage.getHeight() * scale), null);
            g2d.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, formatName, outputStream);
            byte[] bytes = outputStream.toByteArray();
            meterRegistry.counter("count of resized images").increment();
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Exception while resizing image: " + e.getMessage());
        }
    }
}
