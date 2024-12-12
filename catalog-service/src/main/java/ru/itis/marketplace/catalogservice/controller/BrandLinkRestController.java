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
import ru.itis.marketplace.catalogservice.controller.payload.brand_link.NewBrandLinkPayload;
import ru.itis.marketplace.catalogservice.entity.BrandLink;
import ru.itis.marketplace.catalogservice.service.BrandLinkService;

import java.util.List;

@Tag(name = "Brand Link Rest Controller", description = "CRUD operations for brand link entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandLinkRestController {

    private final BrandLinkService brandLinkService;

    @Operation(
            summary = "Endpoint for getting all brand links, by brand ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brand links", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BrandLink.class)))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{brandId:\\d+}/links")
    public List<BrandLink> findAllBrandLinks(@PathVariable Long brandId) {
        return brandLinkService.findAllBrandLinks(brandId);
    }

    @Operation(
            summary = "Endpoint for creating brand link, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created brand link", headers = @Header(name = "Location", description = "URL of the created BrandLink"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BrandLink.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/{brandId:\\d+}/links")
    public ResponseEntity<BrandLink> createBrandLink(@PathVariable Long brandId,
                                                     @Valid @RequestBody NewBrandLinkPayload payload,
                                                     UriComponentsBuilder uriComponentsBuilder) {
        BrandLink brandLink = brandLinkService.createBrandLink(brandId, payload.url(), payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/brands/{brandId}/links/{linkId}")
                        .build(brandLink.getBrandId(), brandLink.getId()))
                .body(brandLink);
    }

    @Operation(
            summary = "Endpoint for deleting brand links, by specified IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when brand links are deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{brandId:\\d+}/links/{linkIds}")
    public ResponseEntity<Void> deleteAllBrandLinkById(@PathVariable(name="brandId") Long ignoredBrandId,
                                                       @PathVariable List<Long> linkIds) {
        brandLinkService.deleteAllBrandLinkById(linkIds);
        return ResponseEntity.noContent().build();
    }
}
