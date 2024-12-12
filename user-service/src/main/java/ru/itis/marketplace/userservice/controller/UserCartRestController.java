package ru.itis.marketplace.userservice.controller;

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
import ru.itis.marketplace.userservice.controller.payload.cart.NewCartItemPayload;
import ru.itis.marketplace.userservice.controller.payload.cart.UpdateCartItemPayload;
import ru.itis.marketplace.userservice.entity.CartItem;
import ru.itis.marketplace.userservice.service.UserCartService;

import java.util.List;
import java.util.Map;

@Tag(name = "User Cart Rest Controller", description = "CRUD operations for user cart")
@Validated
@RestController
@RequestMapping("api/v1/user-service/users/{userId:\\d+}/cart")
@RequiredArgsConstructor
public class UserCartRestController {

    private final UserCartService cartService;

    @Operation(
            summary = "Endpoint for getting all user cart items, by user ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user cart items", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CartItem.class)))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping
    public List<CartItem> findAllCartItemsByUserId(@PathVariable Long userId,
                                                   @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return cartService.findCartItemsByUserId(userId, sortedBy);
    }

    @Operation(
            summary = "Endpoint for creating cart item, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created cart item", headers = @Header(name = "Location", description = "URL of the created CartItem"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItem.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Catalog service is unavailable", responseCode = "503", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping
    public ResponseEntity<CartItem> createCartItem(@PathVariable Long userId,
                                                   @Valid @RequestBody NewCartItemPayload payload,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        CartItem item = cartService.createCartItem(userId, payload.productId(), payload.sizeId(), payload.quantity());
        return ResponseEntity.created(
                uriComponentsBuilder
                        .replacePath("/api/v1/user-service/users/{userId}/cart/{cartItemId}")
                        .build(item.getUserId(), item.getId()))
                .body(item);
    }

    @Operation(
            summary = "Endpoint for updating cart item by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, cart item updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Cart Item not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> updateCartItem(@PathVariable(name = "userId") Long ignoredUserId,
                                               @PathVariable Long cartItemId,
                                               @Valid @RequestBody UpdateCartItemPayload payload) {
        cartService.updateCartItem(cartItemId, payload.quantity());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for deleting cart item by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when cart item is deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping("/{cartItemId:\\d+}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable(name = "userId") Long ignoredUserId,
                                               @PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for deleting all cart items of user cart by user ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response when cart item is deleted", responseCode = "204"),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteAllItemFromUserCartById(@PathVariable Long userId) {
        cartService.deleteAllItemFromUserCartById(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for getting total quantity of cart, by user ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user cart items", responseCode = "200", useReturnTypeSchema = true),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/total-quantity")
    public ResponseEntity<Map<String, Long>> findSumOfItemQuantitiesByUserId(@PathVariable Long userId) {
        Long sum = cartService.findSumOfItemQuantitiesByUserId(userId);
        return ResponseEntity.ok(Map.of("total-quantity", sum));
    }
}
