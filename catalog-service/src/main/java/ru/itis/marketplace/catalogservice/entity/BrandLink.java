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
public class BrandLink {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_link_seq")
    @SequenceGenerator(name = "brand_link_seq", sequenceName = "brand_link_seq", allocationSize = 1)
    private Long id;
    private String url;
    private String name;
    private Long brandId;

    public BrandLink(String url, String name, Long brandId) {
        this.url = url;
        this.name = name;
        this.brandId = brandId;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BrandLink brandLink = (BrandLink) object;
        return getId() != null && Objects.equals(getId(), brandLink.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
