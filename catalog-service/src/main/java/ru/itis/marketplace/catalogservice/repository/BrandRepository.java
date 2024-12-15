package ru.itis.marketplace.catalogservice.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.itis.marketplace.catalogservice.entity.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    Optional<Brand> findByName(String name);
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.brandLinks links WHERE b.id in (:ids)")
    List<Brand> joinLinksToBrandsWithIds(List<Long> ids);
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.brandPhotos photos WHERE b.id in (:ids)")
    List<Brand> joinPhotosToBrandsWithIds(List<Long> ids);
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Brand> findByNameLikeIgnoreCase(String name);

    static Specification<Brand> buildFindAllSpecificationByStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(4);
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("requestStatus"), status));
            }
            return predicates.stream().reduce(criteriaBuilder::and).orElse(null);
        };
    }

}
