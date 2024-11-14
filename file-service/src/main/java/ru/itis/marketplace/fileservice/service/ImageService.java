package ru.itis.marketplace.fileservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    byte[] loadImage(String bucketName, String objectKey);
    void deleteImage(String bucketName, String objectKey);
    String uploadImage(String bucketName, MultipartFile file, Integer width, Integer height);
}
