package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.repository.OrderRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaveDeliveryTimeWorker {

    private final OrderRepository orderRepository;

    @JobWorker(type = "save-delivery-time")
    public void saveDeliveryTime(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Long orderId = ((Number) vars.get("order_id")).longValue();
        Object deliveryTimeObj = vars.get("delivery_time");
        Integer deliveryTime = ((Number) deliveryTimeObj).intValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        order.setEstimatedDeliveryTime(deliveryTime);
        orderRepository.save(order);
    }
}