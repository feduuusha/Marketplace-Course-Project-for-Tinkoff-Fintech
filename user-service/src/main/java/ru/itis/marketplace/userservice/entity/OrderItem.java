package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", allocationSize = 1)
    private Long id;
    private Long productId;
    private Long sizeId;
    private Long brandId;
    private Long quantity;
    private Long orderId;

    public OrderItem(Long productId, Long sizeId, Long brandId, Long quantity) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.brandId = brandId;
        this.quantity = quantity;
    }
}
