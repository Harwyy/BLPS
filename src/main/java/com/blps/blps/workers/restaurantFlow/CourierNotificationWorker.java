package com.blps.blps.workers.restaurantFlow;

import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CourierNotificationWorker {

    private final CamundaClient camundaClient;

    @JobWorker(type = "courier-notification")
    public void notifyRestaurant(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Long orderId = ((Number) vars.get("order_id")).longValue();
        Long restaurantId = ((Number) vars.get("restaurant_id")).longValue();

        camundaClient.newCreateInstanceCommand()
                .bpmnProcessId("courier_process")
                .latestVersion()
                .variables(Map.of("order_id", orderId, "restaurant_id", restaurantId))
                .send()
                .join();
    }

}
