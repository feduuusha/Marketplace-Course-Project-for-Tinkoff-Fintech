package ru.itis.marketplace.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderPayload;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.controller.payload.order.UpdateOrderPayload;
import ru.itis.marketplace.userservice.entity.Order;

import java.util.List;

@Tag(name = "Order Rest Controller", description = "CRUD operations for user orders")
@RestController
@RequestMapping("api/v1/user-service/users/{userId:\\d+}")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @Operation(
            summary = "Endpoint for getting all user orders, by user ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with user orders and order items", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/orders")
    public List<Order> findAllUserOrders(@PathVariable Long userId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false, name = "page-size") Integer pageSize,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return orderService.findOrdersByUserIdAndOrderStatus(userId, status, pageSize, page, sortedBy);
    }

    @Operation(
            summary = "Endpoint for getting order by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with order and order items", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/orders/{orderId:\\d+}")
    public Order findOrderById(@PathVariable(name = "userId") Long ignoredUserId, @PathVariable Long orderId) {
        return orderService.findOrderById(orderId);
    }

    @Operation(
            summary = "Endpoint for creating order, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response with created order", headers = @Header(name = "Location", description = "URL of the created Order"), responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "User not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Catalog service is unavailable", responseCode = "503", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                      @Valid @RequestBody NewOrderPayload payload,
                                      UriComponentsBuilder uriComponentsBuilder) {
        Order order = orderService.createOrder(userId, payload.country(), payload.locality(), payload.region(),
                payload.postalCode(), payload.street(), payload.houseNumber(), payload.description(), payload.orderItems());
        return ResponseEntity
                .created(uriComponentsBuilder
                        .replacePath("/api/v1/user-service/users/{userId}/orders/{orderId}")
                        .build(userId, order.getId()))
                .body(order);
    }

    @Operation(
            summary = "Endpoint for updating fully order by ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, order updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Order not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PutMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderById(@PathVariable(name = "userId") Long ignoredUserId,
                                                @PathVariable Long orderId,
                                                @Valid @RequestBody UpdateOrderPayload payload) {
        orderService.updateOrderById(orderId, payload.country(), payload.locality(), payload.region(),
                payload.postalCode(), payload.street(), payload.houseNumber(), payload.description());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Endpoint for updating order status by order ID, only for authorized users",
            responses = {
                    @ApiResponse(description = "Successful response, order status updated", responseCode = "204", useReturnTypeSchema = true),
                    @ApiResponse(description = "Incorrect payload or some parameters are not specified", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Order not found", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(description = "Error on the server", responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PatchMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderStatusById(@PathVariable(name = "userId") Long ignoredUserId,
                                                      @PathVariable Long orderId,
                                                      @Valid @NotBlank @RequestParam String status) {
        orderService.updateOrderStatusById(orderId, status);
        return ResponseEntity.noContent().build();
    }

}
