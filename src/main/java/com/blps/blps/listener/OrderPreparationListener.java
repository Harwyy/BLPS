package com.blps.blps.listener;

import com.blps.blps.dto.jms.OrderProcessingMessage;
import com.blps.blps.dto.request.OrderItemRequest;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.repository.ProductRepository;
import com.blps.blps.utils.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderPreparationListener {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DistanceCalculator distanceCalculator;
    private final JmsTemplate jmsTemplate;
    private static final String QUEUE_PAYMENT = "order.payment";

    @JmsListener(destination = "order.preparation", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void prepareOrder(OrderProcessingMessage message) {
        Long orderId = message.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        if (order.getStatus() != OrderStatus.CREATED) {
            return;
        }

        try {
            BigDecimal total = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            Restaurant restaurant = order.getRestaurant();

            for (OrderItemRequest itemRequest : message.getItems()) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new BusinessException("Товар не найден: " + itemRequest.getProductId()));

                if (!product.isAvailable()) {
                    throw new BusinessException("Товар '" + product.getName() + "' временно недоступен");
                }
                if (!product.getRestaurant().getId().equals(restaurant.getId())) {
                    throw new BusinessException("Товар '" + product.getName() + "' не принадлежит ресторану");
                }

                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProduct(product);
                item.setQuantity(itemRequest.getQuantity());
                item.setPrice(product.getPrice());
                orderItems.add(item);

                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            }

            if (order.getItems() == null) {
                order.setItems(new ArrayList<>());
            }
            order.getItems().clear();
            order.getItems().addAll(orderItems);
            order.setTotalAmount(total);
            orderRepository.save(order);

            Address restaurantAddress = restaurant.getAddress();
            Address deliveryAddress = order.getDeliveryAddress();
            boolean withinDistance = distanceCalculator.isWithinDeliveryDistance(restaurantAddress, deliveryAddress);
            if (!withinDistance) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                return;
            }

            order.setStatus(OrderStatus.WAITING_PAYMENT);
            orderRepository.save(order);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    jmsTemplate.convertAndSend(QUEUE_PAYMENT, message);
                }
            });
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}