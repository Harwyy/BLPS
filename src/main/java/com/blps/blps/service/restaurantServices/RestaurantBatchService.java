package com.blps.blps.service.restaurantServices;

import com.blps.blps.dto.jms.OrderAssignCourierMessage;
import com.blps.blps.dto.jms.OrderReadyMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantBatchService  {

    private final JmsTemplate jmsTemplate;
    private static final String QUEUE_BATCH_READY = "restaurant.batch.ready";
    private static final String QUEUE_ASSIGN_COURIER = "restaurant.batch.assign-courier";

    public void markMultipleOrdersReadyAsync(Long restaurantId, List<Long> orderIds) {
        for (Long orderId : orderIds) {
            OrderReadyMessage message = new OrderReadyMessage(restaurantId, orderId);
            jmsTemplate.convertAndSend(QUEUE_BATCH_READY, message);
        }
    }

    public void assignCouriersForMultipleOrdersAsync(Long restaurantId, List<Long> orderIds) {
        for (Long orderId : orderIds) {
            OrderAssignCourierMessage message = new OrderAssignCourierMessage(restaurantId, orderId);
            jmsTemplate.convertAndSend(QUEUE_ASSIGN_COURIER, message);
        }
    }
}