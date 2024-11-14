package ru.itis.marketplace.catalogservice.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.itis.marketplace.catalogservice.entity.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    Optional<Brand> findByName(String name);
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.brandLinks links LEFT JOIN b.brandPhotos photos WHERE LOWER(b.name) LIKE LOWER(:name)")
    List<Brand> findByNameLikeIgnoreCase(String name);

    static Specification<Brand> buildFindALlSpecificationByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("brandLinks");
            root.fetch("brandPhotos");
            List<Predicate> predicates = new ArrayList<>(4);
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            return predicates.stream().reduce(criteriaBuilder::and).orElse(null);
        };
    }

}
