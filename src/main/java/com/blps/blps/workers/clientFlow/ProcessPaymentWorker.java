package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.service.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProcessPaymentWorker {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @JobWorker(type = "process-payment")
    public void processPayment(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Long orderId = ((Number) vars.get("order_id")).longValue();

        Order savedOrder = orderRepository.findById(orderId).orElse(null);

        boolean paymentStatus = paymentService.processPayment(savedOrder);

        if (!paymentStatus) {
            throw new BpmnError("PaymentFailed", "Оплата не прошла");
        }
    }
}