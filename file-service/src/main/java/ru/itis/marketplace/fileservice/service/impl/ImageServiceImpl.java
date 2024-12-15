package ru.itis.marketplace.fileservice.service.impl;

import io.awspring.cloud.s3.Location;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import ru.itis.marketplace.fileservice.exception.BadRequestException;
import ru.itis.marketplace.fileservice.service.ImageService;
import ru.itis.marketplace.fileservice.service.ImageResizerService;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Template s3Template;
    private final ImageResizerService imageResizerService;
    private final MeterRegistry meterRegistry;
    @Value("${supported-types}")
    private List<String> supportedContentTypes;

    @Override
    public byte[] loadImage(String bucketName, String objectKey) {
        try {
            var image = IoUtils.toByteArray(s3Template.download(bucketName, objectKey).getInputStream());
            meterRegistry.counter("count of loaded images").increment();
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException("It is impossible to load a image, because: " + exception.getMessage());
        }
    }

    @Override
    public void deleteImage(String bucketName, String objectKey) {
        s3Template.deleteObject(bucketName, objectKey);
        meterRegistry.counter("count of deleted images").increment();
    }

    @Override
    public String uploadImage(String bucketName, MultipartFile file, Integer width, Integer height) {
        try {
            var inputStream = file.getInputStream();
            if (!supportedContentTypes.contains(file.getContentType())) {
                throw new UnsupportedMediaTypeStatusException("Type " + file.getContentType() + " is unsupported");
            }
            var filename = file.getOriginalFilename();
            if (filename == null) {
                throw new BadRequestException("File name is null...");
            }
            int lastIndexOfDot = filename.lastIndexOf('.');
            if (lastIndexOfDot == -1 || lastIndexOfDot == filename.length() - 1) {
                throw new BadRequestException("File should have extension");
            }
            if (width != null && height != null && !"image/svg+xml".equals(file.getContentType())) {
                inputStream = imageResizerService.resizeImage(inputStream, width, height,
                        filename.substring(lastIndexOfDot + 1));
            }
            S3Resource resource = s3Template.upload(bucketName,
                    (UUID.randomUUID() + filename.substring(lastIndexOfDot)), inputStream);
            Location location = resource.getLocation();
            meterRegistry.counter("count of uploaded images").increment();
            return location.getBucket() + "/" + location.getObject();
        } catch (IOException exception) {
            throw new IllegalStateException("It is impossible to upload a image, because: " + exception.getMessage());
        }
    }
}
