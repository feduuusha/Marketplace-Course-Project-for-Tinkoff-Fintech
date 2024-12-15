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
import ru.itis.marketplace.catalogservice.controller.payload.brand.NewBrandPayload;
import ru.itis.marketplace.catalogservice.controller.payload.brand.UpdateBrandPayload;
import ru.itis.marketplace.catalogservice.controller.payload.brand.UpdateBrandStatusPayload;
import ru.itis.marketplace.catalogservice.entity.Brand;
import ru.itis.marketplace.catalogservice.service.BrandService;

import java.util.List;

@Tag(name = "Brand Rest Controller", description = "CRUD operations for brand entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandRestController {

    private final BrandService brandService;

    @Operation(
            summary = "Endpoint for getting brand by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brand", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Brand.class))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping(path = "/{brandId:\\d+}")
    public Brand findBrandById(@PathVariable Long brandId) {
        return brandService.findBrandById(brandId);
    }

    @Operation(
            summary = "Endpoint for fully updating brand by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, brand updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or brand with specified name already exist", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping(path = "/{brandId:\\d+}")
    public ResponseEntity<Void> updateBrandById(@PathVariable Long brandId,
                                             @Valid @RequestBody UpdateBrandPayload payload) {
        brandService.updateBrandById(brandId, payload.name(), payload.description(),
                payload.linkToLogo(), payload.requestStatus());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for updating brand status by brand ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, brand status updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Brand not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PatchMapping(path = "/{brandId:\\d+}")
    public ResponseEntity<Void> updateBrandStatusById(@PathVariable Long brandId,
                                                      @Valid @RequestBody UpdateBrandStatusPayload payload) {
        brandService.updateBrandStatusById(brandId, payload.requestStatus());
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Endpoint for deleting brand by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when brand is deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping(path = "/{brandId:\\d+}")
    public ResponseEntity<Void> deleteBrandById(@PathVariable Long brandId) {
        brandService.deleteBrandById(brandId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for getting all brands, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brands", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Brand.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping
    public List<Brand> findBrands(@RequestParam(required = false) String status,
                                  @RequestParam(required = false, name = "page-size") Integer pageSize,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return brandService.findAllBrands(status, pageSize, page, sortedBy);
    }

    @Operation(
            summary = "Endpoint for creating brand, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created brand", headers = @Header(name = "Location", description = "URL of the created Brand"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Brand.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified or brand with specified name already exist", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Brand> createBrand(@Valid @RequestBody NewBrandPayload payload,
                                         UriComponentsBuilder uriComponentsBuilder) {
        Brand brand = brandService.createBrand(payload.name(), payload.description(), payload.linkToLogo());
        return ResponseEntity
                .created(uriComponentsBuilder
                    .replacePath("api/v1/catalog/brands/{brandId}")
                    .build(brand.getId()))
                .body(brand);
    }

    @Operation(
            summary = "Endpoint for getting brands by IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brands", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Brand.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/by-ids/{brandIds}")
    public List<Brand> findAllBrandByIds(@PathVariable List<Long> brandIds) {
        return brandService.findAllBrandByIds(brandIds);
    }

    @Operation(
            summary = "Endpoint for getting brands by an inaccurate name match, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brands", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Brand.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/search")
    public List<Brand> findBrandsByNameLike(@RequestParam String name) {
        return brandService.findBrandsByNameLike(name);
    }
}
