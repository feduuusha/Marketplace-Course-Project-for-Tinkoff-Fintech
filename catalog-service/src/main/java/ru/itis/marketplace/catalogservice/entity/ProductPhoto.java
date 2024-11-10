package ru.itis.marketplace.catalogservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_photo_seq")
    @SequenceGenerator(name = "product_photo_seq", sequenceName = "product_photo_seq", allocationSize = 1)
    private Long id;
    private String url;
    private Long sequenceNumber;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public ProductPhoto(String url, Long sequenceNumber, Product product) {
        this.url = url;
        this.sequenceNumber = sequenceNumber;
        this.product = product;
    }
}