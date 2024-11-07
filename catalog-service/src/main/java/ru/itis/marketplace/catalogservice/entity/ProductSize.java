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
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_size_seq")
    @SequenceGenerator(name = "product_size_seq", sequenceName = "product_size_seq", allocationSize = 1)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public ProductSize(String name, Product product) {
        this.name = name;
        this.product = product;
    }
}
