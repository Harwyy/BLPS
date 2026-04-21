package com.blps.blps.controller;

import com.blps.blps.dto.CourierOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.service.courierServices.CourierService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers/{courierId}/orders")
@RequiredArgsConstructor
public class CourierOrderController {

    private final CourierService courierService;

    @GetMapping("/assigned")
    public ResponseEntity<List<CourierOrderSummaryDto>> getAssignedOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.ASSIGNED));
    }

    @GetMapping("/ready")
    public ResponseEntity<List<CourierOrderSummaryDto>> getReadyOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.READY));
    }

    @GetMapping("/picked-up")
    public ResponseEntity<List<CourierOrderSummaryDto>> getPickedUpOrders(@PathVariable Long courierId) {
        return ResponseEntity.ok(courierService.getOrdersByStatus(courierId, OrderStatus.PICKED_UP));
    }

    @PostMapping("/{orderId}/pickup")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> pickUpOrder(
            @PathVariable Long courierId, @PathVariable Long orderId) {
        return ResponseEntity.ok(courierService.pickUpOrder(orderId, courierId));
    }

    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<RestaurantOrCourierOrderActionResponse> deliverOrder(
            @PathVariable Long courierId, @PathVariable Long orderId) {
        return ResponseEntity.ok(courierService.deliverOrder(orderId, courierId));
    }
}
