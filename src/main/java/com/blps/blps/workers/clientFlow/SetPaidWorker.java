package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.OrderRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SetPaidWorker {

    private final OrderRepository orderRepository;

    @JobWorker(type = "set-paid")
    public void setPaid(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Long orderId = ((Number) vars.get("order_id")).longValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }
}