package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.*;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SaveOrderWorker {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @JobWorker(type = "save-order")
    public Map<String, Object> saveOrder(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Order order = objectMapper.convertValue(vars.get("order"), Order.class);

        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) vars.get("items");

        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> itemMap : itemsList) {
            Long productId = ((Number) itemMap.get("productId")).longValue();
            Integer quantity = ((Number) itemMap.get("quantity")).intValue();
            Product product = productRepository.findById(productId).orElse(null);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        Long orderId = orderRepository.save(order).getId();
        return Map.of("order_id", orderId);
    }
}