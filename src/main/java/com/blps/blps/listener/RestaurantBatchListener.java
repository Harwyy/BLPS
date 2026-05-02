package com.blps.blps.listener;

import com.blps.blps.dto.jms.OrderAssignCourierMessage;
import com.blps.blps.dto.jms.OrderReadyMessage;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.service.OrderService;
import com.blps.blps.service.courierServices.CourierAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantBatchListener {

    private final OrderService orderService;
    private final CourierAssignmentService courierAssignmentService;

    @JmsListener(destination = "restaurant.batch.ready", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void handleOrderReady(OrderReadyMessage message) {
        Long restaurantId = message.getRestaurantId();
        Long orderId = message.getOrderId();

        try {
            Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
            if (order.getStatus() != OrderStatus.PREPARING) {
                throw new BusinessException(
                        "Заказ " + orderId + " нельзя отметить как готовый. Текущий статус: " + order.getStatus());
            }
            order.setStatus(OrderStatus.READY);
            orderService.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось обработать заказ " + orderId, e);
        }
    }

    @JmsListener(destination = "restaurant.batch.assign-courier", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void handleAssignCourier(OrderAssignCourierMessage message) {
        Long restaurantId = message.getRestaurantId();
        Long orderId = message.getOrderId();

        try {
            Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
            if (order.getStatus() != OrderStatus.READY) {
                throw new BusinessException(
                        "Заказ " + orderId + " нельзя назначить курьеру. Текущий статус: " + order.getStatus());
            }

            courierAssignmentService.assignCourierToReadyOrderWithinTransaction(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось назначить курьера для заказа " + orderId, e);
        }
    }
}
