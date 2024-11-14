package ru.itis.marketplace.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itis.marketplace.userservice.entity.CustomerOrder;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUserId(Long userId);
    List<CustomerOrder> findByUserIdAndStatus(Long userId, String status);
    @Query("SELECT c_o FROM CustomerOrder c_o RIGHT JOIN FETCH c_o.orderItems o_i where o_i.brandId =:brandId")
    List<CustomerOrder> findCustomerOrderThatContainsOrderItemsWithSpecifiedBrandId(@Param("brandId") Long brandId);
    Optional<CustomerOrder> findByPaymentId(String paymentId);
}
