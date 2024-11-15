package ru.itis.marketplace.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_seq")
    @SequenceGenerator(name = "cart_item_seq", sequenceName = "cart_item_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private MarketPlaceUser user;
    private Long productId;
    private Long sizeId;
    private Long quantity;
    @CreationTimestamp
    private Instant creationDateTime;

    public CartItem(MarketPlaceUser user, Long productId, Long sizeId, Long quantity) {
        this.user = user;
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
    }
}
