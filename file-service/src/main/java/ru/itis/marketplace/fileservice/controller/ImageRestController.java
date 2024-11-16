package ru.itis.marketplace.fileservice.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.marketplace.fileservice.service.ImageService;

@Validated
@RestController
@RequestMapping("api/v1/file-service/images")
@RequiredArgsConstructor
public class ImageRestController {

    private final ImageService imageService;

    @PostMapping(value = "/{bucketName}")
    public String uploadImage(@NotNull @PathVariable String bucketName,
                              @NotNull @RequestBody MultipartFile file,
                              @Positive @RequestParam Integer width,
                              @Positive @RequestParam Integer height) {
        return imageService.uploadImage(bucketName, file, width, height);
    }

    @GetMapping(value = "/{bucketName}/{objectKey}")
    public byte[] loadImage(@NotNull @PathVariable String bucketName,
                            @NotNull @PathVariable String objectKey) {
        return imageService.loadImage(bucketName, objectKey);
    }

    @DeleteMapping(value = "/{bucketName}/{objectKey}")
    public ResponseEntity<Void> deleteImage(@NotNull @PathVariable String bucketName,
                                            @NotNull @PathVariable String objectKey) {
        imageService.deleteImage(bucketName, objectKey);
        return ResponseEntity.noContent().build();
    }
}
