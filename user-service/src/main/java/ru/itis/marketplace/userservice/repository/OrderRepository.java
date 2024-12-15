package ru.itis.marketplace.userservice.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.marketplace.userservice.entity.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT c_o FROM Order c_o RIGHT JOIN FETCH c_o.orderItems o_i where o_i.brandId =:brandId")
    List<Order> findOrderThatContainsItemsWithSpecifiedBrandId(@Param("brandId") Long brandId);
    Optional<Order> findByPaymentId(String paymentId);

    static Specification<Order> buildSpecification(Long userId, String status) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("orderItems");
            List<Predicate> predicates = new ArrayList<>(2);
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            return predicates.stream().reduce(criteriaBuilder::and).orElse(null);
        };
    }

    @Query("SELECT o FROM Order o INNER JOIN FETCH o.orderItems o_i where o_i.sizeId in :sizeIds")
    List<Order> findOrderThatContainsSizeIds(List<Long> sizeIds);
}
