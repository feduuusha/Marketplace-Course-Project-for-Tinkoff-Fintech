package ru.itis.marketplace.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "users_brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_brands_seq")
    @SequenceGenerator(name = "users_brands_seq", sequenceName = "users_brands_seq", allocationSize = 1)
    private Long id;
    private Long userId;
    private Long brandId;

    public UserBrand(Long userId, Long brandId) {
        this.userId = userId;
        this.brandId = brandId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserBrand userBrand = (UserBrand) o;
        return getId() != null && Objects.equals(getId(), userBrand.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "userId = " + userId + ", " +
                "brandId = " + brandId + ")";
    }
}
