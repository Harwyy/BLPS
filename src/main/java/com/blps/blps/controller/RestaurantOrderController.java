package com.blps.blps.controller;

import com.blps.blps.dto.RestaurantOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.restaurantServices.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
public class RestaurantOrderController {

    private final RestaurantService restaurantService;

    @GetMapping("/pending")
    public ResponseEntity<List<RestaurantOrderSummaryDto>> getPendingOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOrdersByStatus(restaurantId, OrderStatus.PAID));
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<RestaurantOrderSummaryDto>> getConfirmedOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getOrdersByStatus(restaurantId, OrderStatus.ASSIGNED));
    }

    @PostMapping("/{orderId}/reject")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> rejectOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.rejectOrder(orderId, restaurantId));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> confirmOrder(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.confirmOrder(orderId, restaurantId));
    }

    @PostMapping("/{orderId}/ready")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> markOrderReady(
            @PathVariable Long restaurantId, @PathVariable Long orderId) {
        return ResponseEntity.ok(restaurantService.markOrderReady(orderId, restaurantId));
    }
}
