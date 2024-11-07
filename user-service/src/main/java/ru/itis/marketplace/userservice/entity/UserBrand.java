package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users_brands")
@Getter
@Setter
@NoArgsConstructor
public class UserBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_brands_seq")
    @SequenceGenerator(name = "users_brands_seq", sequenceName = "users_brands_seq", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private MarketPlaceUser user;
    private Long brandId;

    public UserBrand(MarketPlaceUser user, Long brandId) {
        this.user = user;
        this.brandId = brandId;
    }
}
