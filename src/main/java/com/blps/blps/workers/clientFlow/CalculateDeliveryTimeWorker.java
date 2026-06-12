package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.utils.DeliveryTimeCalculator;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CalculateDeliveryTimeWorker {

    private final OrderRepository orderRepository;
    private final DeliveryTimeCalculator deliveryTimeCalculator;

    @JobWorker(type = "calculate-delivery-time")
    public Map<String, Object> calculateDeliveryTime(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object orderIdObj = vars.get("order_id");
        Long orderId = ((Number) orderIdObj).longValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        var restaurantAddress = order.getRestaurant().getAddress();
        var deliveryAddress = order.getDeliveryAddress();

        Integer deliveryTime = deliveryTimeCalculator.calculateDeliveryTime(restaurantAddress, deliveryAddress);

        return Map.of("delivery_time", deliveryTime);
    }
}