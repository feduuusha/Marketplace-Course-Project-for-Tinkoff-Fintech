package ru.itis.marketplace.catalogservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

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
    @OneToMany(mappedBy = "brandId")
    private List<BrandPhoto> brandPhotos;
    @OneToMany(mappedBy = "brandId")
    private List<BrandLink> brandLinks;

    public Brand(String name, String description, String linkToLogo) {
        this.name = name;
        this.description = description;
        this.linkToLogo = linkToLogo;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Brand brand = (Brand) object;
        return getId() != null && Objects.equals(getId(), brand.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "linkToLogo = " + linkToLogo + ", " +
                "requestStatus = " + requestStatus + ")";
    }
}
