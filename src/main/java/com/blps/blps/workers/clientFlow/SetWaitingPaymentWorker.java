package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.OrderRepository;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SetWaitingPaymentWorker {

    private final OrderRepository orderRepository;

    @JobWorker(type = "set-waiting-payment")
    public void setWaitingPayment(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object orderIdObj = vars.get("order_id");

        Long orderId = ((Number) orderIdObj).longValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        order.setStatus(OrderStatus.WAITING_PAYMENT);
        orderRepository.save(order);
    }
}