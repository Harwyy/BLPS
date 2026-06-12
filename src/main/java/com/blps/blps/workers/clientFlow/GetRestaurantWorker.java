package com.blps.blps.workers.clientFlow;

import com.blps.blps.repository.RestaurantRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetRestaurantWorker {

    private final RestaurantRepository restaurantRepository;

    public GetRestaurantWorker(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @JobWorker(type = "get-restaurant")
    public void getRestaurant(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Object restaurantIdObj = vars.get("restaurant_id");

        if (restaurantIdObj == null) {
            throw new BpmnError("ResourceNotFoundException", "restaurant_id не передан");
        }

        Long restaurantId = Long.parseLong(restaurantIdObj.toString());
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BpmnError("ResourceNotFoundException", "Ресторан не найден: " + restaurantId));

    }
}