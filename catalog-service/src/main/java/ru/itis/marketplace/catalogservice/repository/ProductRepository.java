package ru.itis.marketplace.catalogservice.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.itis.marketplace.catalogservice.entity.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByName(String name);
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.photos photos LEFT JOIN p.sizes sizes WHERE LOWER(p.name) LIKE LOWER(:name)")
    List<Product> findByNameLikeIgnoreCase(String name);

    static Specification<Product> buildProductSpecification(BigDecimal priceFrom, BigDecimal priceTo, String status, Long brandId, Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("photos");
            root.fetch("sizes");
            List<Predicate> predicates = new ArrayList<>(5);
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            if (priceFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceFrom));
            }
            if (priceTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceTo));
            }
            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brandId"), brandId));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            return predicates.stream().reduce(criteriaBuilder::and).orElse(null);
        };
    }
}
