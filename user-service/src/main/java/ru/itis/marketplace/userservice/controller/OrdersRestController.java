package ru.itis.marketplace.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.itis.marketplace.userservice.controller.payload.order.NewOrderPayload;
import ru.itis.marketplace.userservice.service.OrderService;
import ru.itis.marketplace.userservice.controller.payload.order.UpdateOrderPayload;
import ru.itis.marketplace.userservice.entity.CustomerOrder;

import java.util.List;

@RestController
@RequestMapping("api/v1/user/users/{userId:\\d+}")
@RequiredArgsConstructor
public class OrdersRestController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public List<CustomerOrder> findAllCustomerOrders(@PathVariable Long userId, @RequestParam(required = false) String status) {
        return this.orderService.findAllOrdersByUserIdAndOrderStatus(userId, status);
    }

    @GetMapping("/orders/{orderId:\\d+}")
    public CustomerOrder findOrderByOrderId(@PathVariable Long userId, @PathVariable Long orderId) {
        return this.orderService.findOrderByUserIdAndByOrderId(userId, orderId);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@PathVariable Long userId,
                                      @Valid @RequestBody NewOrderPayload payload,
                                      BindingResult bindingResult,
                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            CustomerOrder order = this.orderService.createOrder(userId, payload.country(), payload.locality(),
                    payload.region(), payload.postalCode(), payload.street(), payload.houseNumber(), payload.description(), payload.orderItems());
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/v1/user/users/{userId}/orders/{orderId}")
                            .build(userId, order.getId())
                    ).body(order);
        }
    }

    @PutMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderById(@PathVariable Long userId,
                                @PathVariable Long orderId,
                                @Valid @RequestBody UpdateOrderPayload payload,
                                BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        } else {
            this.orderService.updateOrderById(userId, orderId, payload.country(), payload.locality(), payload.region(),
                    payload.postalCode(), payload.street(), payload.houseNumber(), payload.description());
            return ResponseEntity.noContent().build();
        }
    }

    @PatchMapping("/orders/{orderId:\\d+}")
    public ResponseEntity<Void> updateOrderStatusById(@PathVariable Long userId,
                                                      @PathVariable Long orderId,
                                                      @Valid @NotBlank @RequestParam String status) {
        this.orderService.updateOrderStatusById(userId, orderId, status);
        return ResponseEntity.noContent().build();
    }

}
