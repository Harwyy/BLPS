package com.blps.blps.workers.courierFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.Address;
import com.blps.blps.repository.OrderRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PrepareCourierDataWorker {

    private final OrderRepository orderRepository;

    @JobWorker(type = "prepare-courier-data")
    @Transactional
    public Map<String, Object> prepareCourierData(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Long orderId = ((Number) vars.get("order_id")).longValue();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BpmnError("BusinessException", "Заказ не найден: " + orderId));

        Address restaurantAddr = order.getRestaurant().getAddress();
        String restaurantAddressStr = formatAddress(restaurantAddr);

        Address deliveryAddr = order.getDeliveryAddress();
        String deliveryAddressStr = formatAddress(deliveryAddr);

        Integer estimatedMinutes = order.getEstimatedDeliveryTime();
        String estimatedTimeStr = (estimatedMinutes != null) ? estimatedMinutes + " минут" : "не рассчитано";

        String commentToCourier = order.getCommentToCourier() != null ? order.getCommentToCourier() : "";

        String customerPhone = order.getUser().getPhone() != null ? order.getUser().getPhone() : "";

        String restaurantPhone = order.getRestaurant().getPhone() != null ? order.getRestaurant().getPhone() : "";

        return Map.of(
                "restaurant_address", restaurantAddressStr,
                "delivery_address", deliveryAddressStr,
                "estimated_delivery_time", estimatedTimeStr,
                "comment_to_courier", commentToCourier,
                "customer_phone", customerPhone,
                "restaurant_phone", restaurantPhone
        );
    }

    private String formatAddress(Address addr) {
        if (addr == null) return "не указан";
        StringBuilder sb = new StringBuilder();
        if (addr.getCity() != null) sb.append(addr.getCity()).append(", ");
        if (addr.getStreet() != null) sb.append(addr.getStreet()).append(", ");
        if (addr.getBuilding() != null) sb.append(addr.getBuilding());
        if (addr.getFloor() != null && !addr.getFloor().isEmpty()) sb.append(", этаж ").append(addr.getFloor());
        if (addr.getApartment() != null && !addr.getApartment().isEmpty()) sb.append(", кв. ").append(addr.getApartment());
        return sb.toString();
    }
}