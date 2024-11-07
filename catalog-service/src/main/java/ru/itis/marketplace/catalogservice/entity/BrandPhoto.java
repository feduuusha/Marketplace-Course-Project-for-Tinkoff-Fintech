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
public class BrandPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_photo_seq")
    @SequenceGenerator(name = "brand_photo_seq", sequenceName = "brand_photo_seq", allocationSize = 1)
    private Long id;
    private String url;
    private Long sequenceNumber;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    @JsonIgnore
    private Brand brand;

    public BrandPhoto(String url, Long sequenceNumber, Brand brand) {
        this.url = url;
        this.sequenceNumber = sequenceNumber;
        this.brand = brand;
    }
}
