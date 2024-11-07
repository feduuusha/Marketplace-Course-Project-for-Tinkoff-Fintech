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
public class BrandLink {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_link_seq")
    @SequenceGenerator(name = "brand_link_seq", sequenceName = "brand_link_seq", allocationSize = 1)
    private Long id;
    private String url;
    private String name;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    @JsonIgnore
    private Brand brand;

    public BrandLink(String url, String name, Brand brand) {
        this.url = url;
        this.name = name;
        this.brand = brand;
    }
}
