package ru.itis.marketplace.fileservice.service.impl;

import io.awspring.cloud.s3.Location;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.itis.marketplace.fileservice.service.FileService;
import ru.itis.marketplace.fileservice.service.PhotoResizerService;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3Template s3Template;
    private final PhotoResizerService photoResizerService;

    @Value("${image.width:200}")
    private int imageWidth;
    @Value("${image.height:200}")
    private int imageHeight;

    @Override
    public String createFile(String bucketName, InputStream inputStream) {
        inputStream = photoResizerService.resizeImage(inputStream, imageWidth, imageHeight);
        S3Resource resource = s3Template.upload(bucketName, (UUID.randomUUID() + ".jpeg"), inputStream);
        Location location = resource.getLocation();
        return location.getBucket() + "/" + location.getObject();
    }

    @Override
    public byte[] findFile(String bucketName, String objectKey) {
        try {
            return IoUtils.toByteArray(s3Template.download(bucketName, objectKey).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String objectKey) {
        s3Template.deleteObject(bucketName, objectKey);
    }
}
