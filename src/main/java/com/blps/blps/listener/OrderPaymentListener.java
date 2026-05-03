package com.blps.blps.listener;

import com.blps.blps.dto.jms.OrderProcessingMessage;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class OrderPaymentListener {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final JmsTemplate jmsTemplate;
    private static final String QUEUE_FINALIZATION = "order.finalization";

    @JmsListener(destination = "order.payment", containerFactory = "jmsListenerContainerFactory")
    @Transactional
    public void processPayment(OrderProcessingMessage message) {
        Long orderId = message.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден: " + orderId));

        if (order.getStatus() != OrderStatus.WAITING_PAYMENT) {
            return;
        }

        try {
            boolean paymentSuccess = paymentService.processPayment(order);
            if (!paymentSuccess) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                return;
            }

            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    jmsTemplate.convertAndSend(QUEUE_FINALIZATION, message);
                }
            });
        } catch (Exception e) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}