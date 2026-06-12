package com.blps.blps.workers.clientFlow;

import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.User;
import com.blps.blps.service.UserService;
import com.blps.blps.service.restaurantServices.RestaurantService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateOrderObjectWorker {

    private final UserService userService;
    private final RestaurantService restaurantService;

    @JobWorker(type = "create-order-object")
    public Map<String, Object> createOrderObject(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();

        Object clientIdObj = vars.get("client_id");
        Long userId = Long.parseLong(clientIdObj.toString());

        Object restaurantIdObj = vars.get("restaurant_id");
        Long restaurantId = Long.parseLong(restaurantIdObj.toString());

        User user = userService.getUserById(userId);
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        Object deliveryAddress = vars.get("deliveryAddress");

        String commentToRestaurant = (String) vars.get("commentToRestaurant");
        String commentToCourier = (String) vars.get("commentToCourier");
        Boolean leaveAtDoor = (Boolean) vars.get("leaveAtDoor");

        Map<String, Object> order = new HashMap<>();
        order.put("user", user);
        order.put("restaurant", restaurant);
        order.put("deliveryAddress", deliveryAddress);
        order.put("commentToRestaurant", commentToRestaurant);
        order.put("commentToCourier", commentToCourier);
        order.put("leaveAtDoor", leaveAtDoor);
        order.put("status", "CREATED");

        return Map.of("order", order);
    }
}