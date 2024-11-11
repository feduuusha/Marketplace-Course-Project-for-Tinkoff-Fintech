package ru.itis.marketplace.catalogservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BrandPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_photo_seq")
    @SequenceGenerator(name = "brand_photo_seq", sequenceName = "brand_photo_seq", allocationSize = 1)
    private Long id;
    private String url;
    private Long sequenceNumber;
    private Long brandId;

    public BrandPhoto(String url, Long sequenceNumber, Long brandId) {
        this.url = url;
        this.sequenceNumber = sequenceNumber;
        this.brandId = brandId;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BrandPhoto that = (BrandPhoto) object;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
