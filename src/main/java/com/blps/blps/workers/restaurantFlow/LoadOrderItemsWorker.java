package com.blps.blps.workers.restaurantFlow;

import com.blps.blps.entity.Order;
import com.blps.blps.entity.OrderItem;
import com.blps.blps.repository.OrderRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoadOrderItemsWorker {

    private final OrderRepository orderRepository;

    @JobWorker(type = "load-order-items")
    @Transactional
    public Map<String, Object> loadOrderItems(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Long orderId = ((Number) vars.get("order_id")).longValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            items.add(Map.of(
                    "product_name", item.getProduct().getName(),
                    "quantity", item.getQuantity(),
                    "price", item.getPrice()
            ));
        }

        String commentToRestaurant = order.getCommentToRestaurant();

        return Map.of(
                "restaurant_items", items,
                "restaurant_comment", commentToRestaurant != null ? commentToRestaurant : ""
        );
    }
}