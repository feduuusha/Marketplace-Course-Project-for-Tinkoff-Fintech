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
import ru.itis.marketplace.catalogservice.controller.payload.product_photo.NewProductPhotoPayload;
import ru.itis.marketplace.catalogservice.entity.ProductPhoto;
import ru.itis.marketplace.catalogservice.service.ProductPhotoService;

import java.util.List;

@Tag(name = "Product Photo Rest Controller", description = "CRUD operations for product photo entity")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/catalog/products")
public class ProductPhotoRestController {

    private final ProductPhotoService productPhotoService;

    @Operation(
            summary = "Endpoint for getting all product photos, by product ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with product photos", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductPhoto.class)))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{productId:\\d+}/photos")
    public List<ProductPhoto> findProductPhotos(@PathVariable Long productId) {
        return productPhotoService.findProductPhotos(productId);
    }

    @Operation(
            summary = "Endpoint for deleting product photos, by specified IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when product photos are deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{productId:\\d+}/photos/{photoIds}")
    public ResponseEntity<Void> deleteProductPhotosByIds(@PathVariable(name = "productId") Long ignoredProductId,
                                                         @PathVariable List<Long> photoIds) {
        productPhotoService.deleteProductPhotosByIds(photoIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for creating product photo, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created product photo", headers = @Header(name = "Location", description = "URL of the created ProductPhoto"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductPhoto.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/{productId:\\d+}/photos")
    public ResponseEntity<ProductPhoto> createProductPhoto(@Valid @RequestBody NewProductPhotoPayload payload,
                                           @PathVariable Long productId,
                                           UriComponentsBuilder uriComponentsBuilder) {
        ProductPhoto productPhoto = productPhotoService
                .createProductPhoto(productId, payload.url(), payload.sequenceNumber());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/product/{productId}/photos/{photoId}")
                        .build(productPhoto.getProductId(), productPhoto.getId()))
                .body(productPhoto);
    }
}
