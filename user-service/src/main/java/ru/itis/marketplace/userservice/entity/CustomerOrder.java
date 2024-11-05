package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
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
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private MarketPlaceUser customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    private String status;
    private String description;
    @UpdateTimestamp
    private LocalDateTime updateDateTime;
    @CreationTimestamp
    private LocalDateTime creationDateTime;

}
