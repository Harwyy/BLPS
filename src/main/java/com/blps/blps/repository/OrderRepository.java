package com.blps.blps.repository;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndRestaurantId(Long id, Long restaurantId);

    Optional<Order> findByIdAndCourierId(Long id, Long courierId);

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    List<Order> findByCourierIdAndStatus(Long courierId, OrderStatus status);

    List<Order> findByCreatedAtBeforeAndStatusNotIn(LocalDateTime cutoffTime, List<OrderStatus> excludedStatuses);

    long countByCourierIdAndStatusIn(Long id, List<OrderStatus> activeStatuses);
}
