package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderPayload;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.controller.payload.order.UpdateOrderPayload;
import ru.itis.marketplace.userservice.entity.Order;

import java.util.List;

@RestController
@RequestMapping("api/v1/user-service/users/{userId:\\d+}")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public List<Order> findAllUserOrders(@PathVariable Long userId,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false, name = "page-size") Integer pageSize,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false, name = "sorted-by") String sortedBy) {
        return orderService.findOrdersByUserIdAndOrderStatus(userId, status, pageSize, page, sortedBy);
    }

    @GetMapping("/orders/{orderId:\\d+}")
    public Order findOrderById(@PathVariable(name = "userId") Long ignoredUserId, @PathVariable Long orderId) {
        return orderService.findOrderById(orderId);
    }

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

    @PutMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderById(@PathVariable(name = "userId") Long ignoredUserId,
                                                @PathVariable Long orderId,
                                                @Valid @RequestBody UpdateOrderPayload payload) {
        orderService.updateOrderById(orderId, payload.country(), payload.locality(), payload.region(),
                payload.postalCode(), payload.street(), payload.houseNumber(), payload.description());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderStatusById(@PathVariable(name = "userId") Long ignoredUserId,
                                                      @PathVariable Long orderId,
                                                      @Valid @NotBlank @RequestParam String status) {
        orderService.updateOrderStatusById(orderId, status);
        return ResponseEntity.noContent().build();
    }

}
