package com.blps.blps.workers.courierFlow;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AssignCourierToOrderWorker {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final ObjectMapper objectMapper;

    @JobWorker(type = "assign-courier-to-order")
    @Transactional
    public Map<String, Object> assignCourierToOrder(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object orderIdObj = vars.get("order_id");

        Long orderId = ((Number) orderIdObj).longValue();

        Object courierObj = vars.get("reserved_courier");

        Courier courier = objectMapper.convertValue(courierObj, Courier.class);;

        Long courierId = courier.getId();

        Order order = orderRepository.findById(orderId).orElse(null);

        Courier managedCourier = courierRepository.findById(courierId).orElse(null);

        order.setCourier(managedCourier);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setAssignedAt(LocalDateTime.now());

        orderRepository.save(order);

        return Map.of(
                "courier_id", courierId
        );
    }
}