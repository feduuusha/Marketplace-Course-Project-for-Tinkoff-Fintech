package ru.itis.marketplace.fileservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public interface FileService {
    String createFile(String bucketName, InputStream inputStream);

    byte[] findFile(String bucketName, String objectKey);
    void deleteFile(String bucketName, String objectKey);
}
