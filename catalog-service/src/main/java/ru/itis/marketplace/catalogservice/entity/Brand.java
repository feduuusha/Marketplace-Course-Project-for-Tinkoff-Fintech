package ru.itis.marketplace.catalogservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.itis.marketplace.catalogservice.entity.status.RequestStatus;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String linkToLogo;
    private RequestStatus requestStatus = RequestStatus.UNDER_CONSIDERATION;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = BrandPhoto.class)
    private List<BrandPhoto> brandPhotos;
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = BrandLink.class)
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
