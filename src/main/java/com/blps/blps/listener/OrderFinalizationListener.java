package com.blps.blps.listener;

import com.blps.blps.dto.jms.OrderProcessingMessage;
import com.blps.blps.entity.Order;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.utils.DeliveryTimeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFinalizationListener {

    private final OrderRepository orderRepository;
    private final DeliveryTimeCalculator deliveryTimeCalculator;

    @JmsListener(destination = "order.finalization", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void finalizeOrder(OrderProcessingMessage message) {
        Long orderId = message.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        Integer deliveryTime = deliveryTimeCalculator.calculateDeliveryTime(
                order.getRestaurant().getAddress(),
                order.getDeliveryAddress()
        );
        order.setEstimatedDeliveryTime(deliveryTime);
        orderRepository.save(order);
    }
}