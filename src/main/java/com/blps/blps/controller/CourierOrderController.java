package com.blps.blps.controller;

import com.blps.blps.dto.CourierOrderResponse;
import com.blps.blps.service.CourierOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/couriers/{courierId}/orders")
@RequiredArgsConstructor
public class CourierOrderController {

    private final CourierOrderService courierOrderService;

    @GetMapping
    public ResponseEntity<List<CourierOrderResponse>> getOrders(@PathVariable Long courierId) {
        List<CourierOrderResponse> orders = courierOrderService.getActiveOrders(courierId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/pickup")
    public ResponseEntity<CourierOrderResponse> pickupOrder(@PathVariable Long courierId, @PathVariable Long orderId) {
        CourierOrderResponse response = courierOrderService.pickupOrder(courierId, orderId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<CourierOrderResponse> deliverOrder(@PathVariable Long courierId, @PathVariable Long orderId) {
        CourierOrderResponse response = courierOrderService.deliverOrder(courierId, orderId);
        return ResponseEntity.ok(response);
    }
}
