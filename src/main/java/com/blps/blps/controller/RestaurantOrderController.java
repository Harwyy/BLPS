package com.blps.blps.controller;

import com.blps.blps.dto.RestaurantOrderResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.RestaurantOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
public class RestaurantOrderController {
    private final RestaurantOrderService restaurantOrderService;

    @GetMapping("/status")
    public ResponseEntity<List<RestaurantOrderResponse>> getOrdersByStatus(@PathVariable Long restaurantId) {
        List<RestaurantOrderResponse> orders =
                restaurantOrderService.getOrdersByRestaurantAndStatus(restaurantId, OrderStatus.SENT_TO_RESTAURANT);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<RestaurantOrderResponse> confirmOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        RestaurantOrderResponse response = restaurantOrderService.confirmOrder(restaurantId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/decline")
    public ResponseEntity<RestaurantOrderResponse> declineOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        RestaurantOrderResponse response = restaurantOrderService.declineOrder(restaurantId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/assign-courier")
    public ResponseEntity<Boolean> assignCourier(@PathVariable Long restaurantId, @PathVariable Long orderId) {
        boolean response = restaurantOrderService.assignCourier(orderId, restaurantId);
        return ResponseEntity.ok(response);
    }
}
