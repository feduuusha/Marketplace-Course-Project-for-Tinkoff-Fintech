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
import ru.itis.marketplace.catalogservice.controller.payload.product_size.NewProductSizePayload;
import ru.itis.marketplace.catalogservice.entity.ProductSize;
import ru.itis.marketplace.catalogservice.service.ProductSizeService;

import java.util.List;

@Tag(name = "Product Size Rest Controller", description = "CRUD operations for product size entity")
@Validated
@RestController
@RequestMapping("api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductSizeRestController {

    private final ProductSizeService productSizeService;

    @Operation(
            summary = "Endpoint for getting all product sizes, by product ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with product sizes", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductSize.class)))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{productId:\\d+}/sizes")
    public List<ProductSize> findAllProductSizes(@PathVariable Long productId) {
        return productSizeService.findAllProductSizes(productId);
    }

    @Operation(
            summary = "Endpoint for creating product size, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created product size", headers = @Header(name = "Location", description = "URL of the created ProductSize"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSize.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Product not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/{productId:\\d+}/sizes")
    public ResponseEntity<ProductSize> createProductSize(@PathVariable Long productId,
                                               @Valid @RequestBody NewProductSizePayload payload,
                                               UriComponentsBuilder uriComponentsBuilder) {
        ProductSize productSize = productSizeService.createProductSize(productId, payload.name());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/catalog/products/{productId}/sizes/{sizeId}")
                        .build(productSize.getProductId(),  productSize.getId()))
                .body(productSize);
    }

    @Operation(
            summary = "Endpoint for deleting product sizes, by specified IDs, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when product sizes are deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{productId:\\d+}/sizes/{sizeIds}")
    public ResponseEntity<Void> deleteAllProductSizesById(@PathVariable(name = "productId") Long ignoredProductId,
                                                          @PathVariable List<Long> sizeIds) {
        productSizeService.deleteAllProductSizesById(sizeIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for getting product size by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with product size", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductSize.class))),
                    @ApiResponse(description = "Product size not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{productId:\\d+}/sizes/{sizeId}")
    public ResponseEntity<ProductSize> findSizeByIdAndProductId(@PathVariable Long productId,
                                                                @PathVariable Long sizeId) {
        return ResponseEntity.ok(productSizeService.findSizeByIdAndProductId(productId, sizeId));
    }


}
