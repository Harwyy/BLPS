package com.blps.blps.workers.courierFlow;

import com.blps.blps.service.courierServices.CourierService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PickUpOrderWorker {

    private final CourierService courierService;

    @JobWorker(type = "pick-up-order")
    @Transactional
    public void pickUpOrder(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Long orderId = ((Number) vars.get("order_id")).longValue();
        Long courierId = ((Number) vars.get("courier_id")).longValue();
        courierService.pickUpOrder(orderId, courierId);
    }
}