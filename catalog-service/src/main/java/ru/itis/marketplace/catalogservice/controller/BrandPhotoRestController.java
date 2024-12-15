package ru.itis.marketplace.catalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.catalogservice.controller.payload.brand_photo.NewBrandPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.BrandPhoto;
import ru.itis.marketplace.catalogservice.service.BrandPhotoService;

import java.util.List;

@Tag(name = "Brand Photo Rest Controller", description = "CRUD operations for brand photo entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandPhotoRestController {

    private final BrandPhotoService brandPhotoService;

    @Operation(
            summary = "Endpoint for getting all brand photos, by brand ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brand photos", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BrandPhoto.class)))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{brandId:\\d+}/photos")
    public List<BrandPhoto> findBrandPhotos(@PathVariable Long brandId) {
        return brandPhotoService.findBrandPhotos(brandId);
    }

    @Operation(
            summary = "Endpoint for deleting brand photos, by specified IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when brand photos are deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{brandId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteAllBrandPhotosById(@PathVariable(name = "brandId") Long ignoredBrandId,
                                                         @PathVariable List<Long> photoIds) {
        brandPhotoService.deleteAllBrandPhotosById(photoIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for creating brand photo, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created brand photo", headers = @Header(name = "Location", description = "URL of the created BrandPhoto"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandPhoto.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/{brandId:\\d+}/photos")
    public ResponseEntity<BrandPhoto> createBrandPhoto(@PathVariable Long brandId,
                                              @Valid @RequestBody NewBrandPhotoPayload payload,
                                              UriComponentsBuilder uriComponentsBuilder) {
        BrandPhoto brandPhoto = brandPhotoService.createBrandPhoto(brandId, payload.url(), payload.sequenceNumber());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/brands/{brandId}/photos/{photoId}")
                        .build( brandPhoto.getBrandId(), brandPhoto.getId()))
                .body(brandPhoto);
    }
}
