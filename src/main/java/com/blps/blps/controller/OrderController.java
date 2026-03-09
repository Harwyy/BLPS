package com.blps.blps.controller;

import com.blps.blps.dto.OrderCheckResponse;
import com.blps.blps.dto.OrderInfoResponse;
import com.blps.blps.dto.OrderRequest;
import com.blps.blps.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/check")
    public ResponseEntity<OrderCheckResponse> checkOrder(@Valid @RequestBody OrderRequest request) {
        OrderCheckResponse response = orderService.checkOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<OrderInfoResponse> confirmOrder(@Valid @RequestBody OrderRequest request) {
        OrderInfoResponse response = orderService.confirmOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderInfoResponse> getOrder(@PathVariable Long id) {
        OrderInfoResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }
}
