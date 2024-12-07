package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "`order`")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    private Long id;
    private String paymentLink;
    private String paymentId;
    private String paymentIntentId;
    private String country;
    private String locality;
    private String region;
    private String postalCode;
    private String street;
    private String houseNumber;
    private Long userId;
    @OneToMany(mappedBy = "orderId", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;
    private String status;
    private String description;
    @UpdateTimestamp
    private Instant updateDateTime;
    @CreationTimestamp
    private Instant creationDateTime;

    public Order(String paymentId, String country, String locality, String region,
                 String postalCode, String street, String houseNumber, Long userId, List<OrderItem> orderItems,
                 String status, String description) {
        this.paymentId = paymentId;
        this.country = country;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.street = street;
        this.houseNumber = houseNumber;
        this.userId = userId;
        this.orderItems = orderItems;
        this.status = status;
        this.description = description;
    }
}
