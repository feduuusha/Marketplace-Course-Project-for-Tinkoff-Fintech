package ru.itis.marketplace.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.itis.marketplace.userservice.controller.payload.user_brand.NewUserBrandPayload;
import ru.itis.marketplace.userservice.service.UserBrandService;
import ru.itis.marketplace.userservice.entity.Order;
import ru.itis.marketplace.userservice.entity.UserBrand;
import ru.itis.marketplace.userservice.service.OrderService;

import java.util.List;

@Tag(name = "User Brand Rest Controller", description = "CRUD operations for user brand entity")
@Validated
@RestController
@RequestMapping("api/v1/user-service/users")
@RequiredArgsConstructor
public class UserBrandRestController {

    private final UserBrandService userBrandService;
    private final OrderService orderService;

    @Operation(
            summary = "Endpoint for declaring who is brand owner, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user brand entity", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBrand.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Catalog service is unavailable", responseCode = "503", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/{userId:\\d+}/brands")
    public ResponseEntity<UserBrand> declareBrandOwner(@PathVariable Long userId,
                                                       @Valid @RequestBody NewUserBrandPayload  payload,
                                                       UriComponentsBuilder uriComponentsBuilder) {
        UserBrand userBrand = userBrandService.declareBrandOwner(userId, payload.brandId());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("api/v1/user-service/users/{userId}/brands/{brandId}")
                        .build(userId, payload.brandId()))
                .body(userBrand);
    }

    @Operation(
            summary = "Endpoint for getting all user brands by user ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with brand IDs ", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{userId:\\d+}/brands")
    public List<Long> findAllUserBrands(@PathVariable Long userId) {
        return userBrandService.findAllUserBrands(userId);
    }

    @Operation(
            summary = "Endpoint for deleting user brand by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when user brand is deleted ", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{userId:\\d+}/brands/{brandId:\\d+}")
    public ResponseEntity<Void> deleteUserBrand(@PathVariable(name = "userId") Long ignoredUserId,
                                                @PathVariable Long brandId) {
        userBrandService.deleteUserBrand(brandId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for getting orders by brand ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with orders and order items", responseCode = "200",  content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/{userId:\\d+}/brands/{brandId:\\d+}/orders")
    public List<Order> findOrdersByBrandId(@PathVariable(name ="userId") Long ignoredUserId, @PathVariable Long brandId) {
        return orderService.findOrdersByBrandId(brandId);
    }

    @Operation(
            summary = "Endpoint for getting user brand by brand ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user brand entity ", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserBrand.class))),
                    @ApiResponse(description = "Brand do not have user", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/by-brand/{brandId:\\d+}")
    public UserBrand findUserIdOwnerOfBrand(@PathVariable Long brandId) {
        return userBrandService.findUserBrandByBrandId(brandId);
    }
}
