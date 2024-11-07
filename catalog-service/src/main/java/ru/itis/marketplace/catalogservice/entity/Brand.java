package ru.itis.marketplace.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_seq")
    @SequenceGenerator(name = "brand_seq", sequenceName = "brand_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String description;
    private String linkToLogo;
    private String requestStatus = "under_consideration";
    @OneToMany(mappedBy = "brand", cascade = CascadeType.REMOVE, targetEntity = BrandPhoto.class)
    private List<BrandPhoto> brandPhotos;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.REMOVE, targetEntity = BrandLink.class)
    private List<BrandLink> brandLinks;

    public Brand(String name, String description, String linkToLogo) {
        this.name = name;
        this.description = description;
        this.linkToLogo = linkToLogo;
    }

    public Brand(Long id, String name, String description, String linkToLogo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.linkToLogo = linkToLogo;
    }
}
