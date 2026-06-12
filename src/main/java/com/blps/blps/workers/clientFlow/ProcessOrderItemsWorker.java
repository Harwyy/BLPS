package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Product;
import com.blps.blps.service.ProductService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProcessOrderItemsWorker {

    private final ProductService productService;

    @JobWorker(type = "process-order-items")
    public Map<String, Object> processOrderItems(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();

        List<Map<String, Object>> items = (List<Map<String, Object>>) variables.get("items");
        Map<String, Object> order = (Map<String, Object>) variables.get("order");

        List<Map<String, Object>> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer quantity = ((Number) item.get("quantity")).intValue();

            Product product = productService.getProductById(productId);

            BigDecimal price = product.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));

            Map<String, Object> orderItem = Map.of(
                    "productId", product.getId(),
                    "quantity", quantity,
                    "price", price,
                    "productName", product.getName()
            );
            orderItems.add(orderItem);
            total = total.add(itemTotal);
        }

        order.put("totalAmount", total);
        order.put("items", orderItems);

        return Map.of("order", order);
    }
}