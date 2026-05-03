package com.blps.blps.job;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class OldOrdersCancelJob extends QuartzJobBean {

    private OrderRepository orderRepository;

    private static final List<OrderStatus> FINAL_STATUSES = List.of(
            OrderStatus.CANCELLED,
            OrderStatus.CANCELLED_BY_REST,
            OrderStatus.DELIVERED
    );

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<Order> oldOrders = orderRepository.findByCreatedAtBeforeAndStatusNotIn(cutoffTime, FINAL_STATUSES);

        if (oldOrders.isEmpty()) {
            return;
        }

        for (Order order : oldOrders) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            order.setStatus(OrderStatus.CANCELLED);

            orderRepository.save(order);
        }
    }
}