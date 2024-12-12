package ru.itis.marketplace.fileservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.marketplace.fileservice.service.ImageService;

import java.io.ByteArrayOutputStream;

@Tag(name = "Image Rest Controller", description = "Controller for interacting with the object storage, processes only images")
@Validated
@RestController
@RequestMapping("api/v1/file-service/images")
@RequiredArgsConstructor
public class ImageRestController {

    private final ImageService imageService;

    @Operation(
            summary = "Endpoint for upload images, available only for authorized users",
            description = "To upload, you need to specify bucket name and pass a MultiPartFile in parameter with name 'file'. You can also pass the values of the height and width of the saved image. The processed types of images are specified in the application properties (by default is image/jpeg,image/png,image/gif,image/svg+xml)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Multipart File", required = true),
            responses = {
                    @ApiResponse(description = "The image has been uploaded and is available at the return address", responseCode = "200",  content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
                    @ApiResponse(description = "A non-existent bucket name is specified", responseCode = "404", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Unsupported media type or file is not correct", responseCode = "400", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping(value = "/{bucketName}")
    public String uploadImage(@NotNull @PathVariable String bucketName,
                              @NotNull @RequestBody MultipartFile file,
                              @Positive @RequestParam(required = false) Integer width,
                              @Positive @RequestParam(required = false) Integer height) {
        return imageService.uploadImage(bucketName, file, width, height);
    }

    @Operation(
            summary = "Endpoint for load images, not secured",
            description = "to upload an image, you need to insert a link saved in the image database into the url",
            responses = {
                    @ApiResponse(description = "Returns an image in binary representation", responseCode = "200",  content = @Content(mediaType = "application/json", schema = @Schema(implementation = ByteArrayOutputStream.class))),
                    @ApiResponse(description = "A non-existent bucket name or key value is specified", responseCode = "404", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping(value = "/{bucketName}/{objectKey}")
    public byte[] loadImage(@NotNull @PathVariable String bucketName,
                            @NotNull @PathVariable String objectKey) {
        return imageService.loadImage(bucketName, objectKey);
    }

    @Operation(
            summary = "Endpoint for delete images, available only for authorized users",
            description = "to delete an image, you need to insert a link saved in the image database into the url",
            responses = {
                    @ApiResponse(description = "Deletion was successful", responseCode = "200"),
                    @ApiResponse(description = "A non-existent bucket name or key value is specified", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping(value = "/{bucketName}/{objectKey}")
    public ResponseEntity<Void> deleteImage(@NotNull @PathVariable String bucketName,
                                            @NotNull @PathVariable String objectKey) {
        imageService.deleteImage(bucketName, objectKey);
        return ResponseEntity.noContent().build();
    }
}
