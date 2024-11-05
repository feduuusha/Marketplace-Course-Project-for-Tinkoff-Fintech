package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MarketPlaceUser user;
    @Column(unique = true)
    private Long brandId;

    public UserBrand(MarketPlaceUser user, Long brandId) {
        this.user = user;
        this.brandId = brandId;
    }
}
