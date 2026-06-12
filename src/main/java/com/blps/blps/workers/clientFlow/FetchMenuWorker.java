package com.blps.blps.workers.clientFlow;

import com.blps.blps.dto.response.ProductResponse;
import com.blps.blps.service.PublicRestaurantService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FetchMenuWorker {

    private final PublicRestaurantService publicRestaurantService;

    @JobWorker(type = "fetch-menu")
    public Map<String, Object> fetchMenu(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Long restaurantId = ((Number) vars.get("restaurant_id")).longValue();

        List<ProductResponse> products = publicRestaurantService.getMenu(restaurantId, PageRequest.of(0, 3));

        List<Map<String, Object>> options = new ArrayList<>();
        for (ProductResponse product : products) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", product.getId());
            option.put("label", product.getName() + " - " + product.getPrice() + " P");
            options.add(option);
        }

        return Map.of("menu_list", options);
    }
}