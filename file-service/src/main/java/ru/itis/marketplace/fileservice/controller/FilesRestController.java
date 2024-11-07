package ru.itis.marketplace.fileservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.marketplace.fileservice.service.FileService;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/file-service/files")
@RequiredArgsConstructor
public class FilesRestController {

    private final FileService fileService;
    @PostMapping(value = "/{bucketName}")
    public String createFile(@PathVariable String bucketName, @RequestBody MultipartFile file) {
        try {
            return this.fileService.createFile(bucketName, file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @GetMapping(value = "/{bucketName}/{objectKey}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] findFile(@PathVariable String bucketName,
                         @PathVariable String objectKey) {
        return this.fileService.findFile(bucketName, objectKey);
    }

    @DeleteMapping(value = "/{bucketName}/{objectKey}")
    public ResponseEntity<Void> deleteFile(@PathVariable String bucketName,
                                           @PathVariable String objectKey) {
        this.fileService.deleteFile(bucketName, objectKey);
        return ResponseEntity.noContent().build();
    }
}
