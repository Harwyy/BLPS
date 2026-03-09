package com.blps.blps.service;

import com.blps.blps.dto.RestaurantOrderResponse;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.OrderMapper;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantOrderService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<RestaurantOrderResponse> getOrdersByRestaurantAndStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status).stream()
                .map(orderMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantOrderResponse confirmOrder(Long restaurantId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));
        if (order.getStatus() != OrderStatus.SENT_TO_RESTAURANT) {
            throw new BusinessException("Невозможно подтвердить заказ в статусе " + order.getStatus());
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        return orderMapper.mapToResponse(order);
    }

    @Transactional
    public RestaurantOrderResponse declineOrder(Long restaurantId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));
        if (order.getStatus() != OrderStatus.SENT_TO_RESTAURANT) {
            throw new BusinessException("Невозможно отклонить заказ в статусе " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELED_BY_RESTAURANT);
        order = orderRepository.save(order);
        return orderMapper.mapToResponse(order);
    }

    @Transactional
    public boolean assignCourier(Long orderId, Long restaurantId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException("Нельзя назначить курьера для заказа в статусе " + order.getStatus());
        }

        Courier courier = (Courier) courierRepository
                .findFirstByStatus(CourierStatus.AVAILABLE)
                .orElseThrow(() -> new BusinessException("Нет свободных курьеров"));

        order.setCourier(courier);
        order.setStatus(OrderStatus.COURIER_ASSIGNED);
        order.setAssignedAt(LocalDateTime.now());

        courier.setStatus(CourierStatus.BUSY);

        orderRepository.save(order);
        courierRepository.save(courier);
        return true;
    }
}
