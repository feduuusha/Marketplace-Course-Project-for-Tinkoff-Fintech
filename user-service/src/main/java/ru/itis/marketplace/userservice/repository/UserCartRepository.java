package ru.itis.marketplace.userservice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.marketplace.userservice.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface UserCartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId, Sort sortedBy);
    Optional<CartItem> findByUserIdAndProductIdAndSizeId(Long userId, Long productId, Long sizeId);
    @Query("SELECT SUM(t.quantity) FROM CartItem t WHERE t.userId =:userId GROUP BY t.userId")
    Long findSumOfItemQuantitiesByUserId(Long userId);
    void deleteByUserId(Long userId);
}
