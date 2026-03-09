package com.blps.blps.repository;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    List<Order> findByCourierIdAndStatusIn(Long courierId, List<OrderStatus> statuses);
}
