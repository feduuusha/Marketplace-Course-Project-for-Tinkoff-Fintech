package ru.itis.marketplace.catalogservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String requestStatus = "under_consideration";
    private Long categoryId;
    private Long brandId;
    @OneToMany(mappedBy = "productId")
    private List<ProductPhoto> photos;
    @OneToMany(mappedBy = "productId")
    private List<ProductSize> sizes;
    @CreationTimestamp
    private Instant additionDateTime;
    @UpdateTimestamp
    private Instant updateDateTime;

    public Product(String name, BigDecimal price, String description, Long categoryId, Long brandId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Product product = (Product) o;
        return getId() != null && Objects.equals(getId(), product.getId());
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
                "price = " + price + ", " +
                "description = " + description + ", " +
                "requestStatus = " + requestStatus + ", " +
                "categoryId = " + categoryId + ", " +
                "brandId = " + brandId + ", " +
                "additionDateTime = " + additionDateTime + ", " +
                "updateDateTime = " + updateDateTime + ")";
    }
}
