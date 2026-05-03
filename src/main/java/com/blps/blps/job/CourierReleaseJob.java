package com.blps.blps.job;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourierReleaseJob extends QuartzJobBean {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.PAID, OrderStatus.PREPARING,
            OrderStatus.READY, OrderStatus.ASSIGNED, OrderStatus.PICKED_UP
    );

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        List<Courier> busyCouriers = courierRepository.findByStatus(CourierStatus.BUSY);
        for (Courier courier : busyCouriers) {
            long realActiveOrders = orderRepository.countByCourierIdAndStatusIn(courier.getId(), ACTIVE_STATUSES);
            if (realActiveOrders != courier.getActiveOrdersCount()) {

                courier.setActiveOrdersCount((int) realActiveOrders);
                if (realActiveOrders == 0 && courier.getStatus() == CourierStatus.BUSY) {
                    courier.setStatus(CourierStatus.AVAILABLE);
                }
                courierRepository.save(courier);
            }
        }
    }
}