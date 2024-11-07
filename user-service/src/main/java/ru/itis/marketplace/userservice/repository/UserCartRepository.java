package ru.itis.marketplace.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.marketplace.userservice.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface UserCartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    @Query("select sum(t.quantity) from CartItem t where t.user.id =:userId group by t.user.id")
    Long findSumOfItemQuantitiesByUserId(Long userId);

    void deleteByUserId(Long userId);
}
