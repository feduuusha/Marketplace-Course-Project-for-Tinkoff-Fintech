package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "order")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    private Long id;
    private String paymentLink;
    @Column(unique = true)
    private String paymentId;
    private String country;
    private String locality;
    private String region;
    private String postalCode;
    private String street;
    private String houseNumber;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private MarketPlaceUser user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    private String status;
    private String description;
    @UpdateTimestamp
    private Instant updateDateTime;
    @CreationTimestamp
    private Instant creationDateTime;

}
