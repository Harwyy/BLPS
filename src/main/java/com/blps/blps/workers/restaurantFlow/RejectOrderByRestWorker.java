package com.blps.blps.workers.restaurantFlow;

import com.blps.blps.service.restaurantServices.RestaurantService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RejectOrderByRestWorker {

    private final RestaurantService restaurantService;

    @JobWorker(type = "reject-order-by-rest")
    @Transactional
    public void rejectOrder(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        Long orderId = ((Number) variables.get("order_id")).longValue();
        Long restaurantId = ((Number) variables.get("restaurant_id")).longValue();
        restaurantService.rejectOrder(orderId, restaurantId);
    }
}