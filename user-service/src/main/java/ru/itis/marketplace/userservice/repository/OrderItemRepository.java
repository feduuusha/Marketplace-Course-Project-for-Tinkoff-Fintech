package ru.itis.marketplace.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.itis.marketplace.userservice.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Query("UPDATE OrderItem o set o.brandId =:newBrandId where o.productId =:productId")
    void updateBrandIdForProductWithId(Long productId, Long newBrandId);

    void deleteByOrderId(Long orderId);
}
