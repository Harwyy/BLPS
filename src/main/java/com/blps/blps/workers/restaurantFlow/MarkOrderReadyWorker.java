package com.blps.blps.workers.restaurantFlow;

import com.blps.blps.service.restaurantServices.RestaurantService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MarkOrderReadyWorker {

    private final RestaurantService restaurantService;

    @JobWorker(type = "mark-order-ready")
    @Transactional
    public void markOrderReady(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Long orderId = ((Number) variables.get("order_id")).longValue();
        Long restaurantId = ((Number) variables.get("restaurant_id")).longValue();
        restaurantService.markOrderReady(orderId, restaurantId);
    }
}