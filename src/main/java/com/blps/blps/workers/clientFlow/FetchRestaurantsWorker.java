package com.blps.blps.workers.clientFlow;

import com.blps.blps.dto.RestaurantDto;
import com.blps.blps.dto.response.RestaurantsWithTopByTypeResponse;
import com.blps.blps.service.PublicRestaurantService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FetchRestaurantsWorker {

    private final PublicRestaurantService publicRestaurantService;

    @JobWorker(type = "fetch-restaurants")
    public Map<String, Object> fetchRestaurants(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        String city = (String) vars.get("city");
        boolean all = (boolean) vars.getOrDefault("all", false);

        RestaurantsWithTopByTypeResponse full = publicRestaurantService.getTop3RestaurantsWithCategories(city, all);

        List<Map<String, Object>> options = new ArrayList<>();
        for (RestaurantDto restaurant : full.getOverallTop3()) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", restaurant.getId());
            option.put("label", restaurant.getName());
            options.add(option);
        }

        return Map.of("restaurants_list", options);
    }
}
