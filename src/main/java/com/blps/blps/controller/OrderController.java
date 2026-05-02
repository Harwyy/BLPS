package com.blps.blps.controller;

import com.blps.blps.dto.request.OrderCreateRequest;
import com.blps.blps.dto.response.OrderResponse;
import com.blps.blps.security.service.XmlUserDetailsService;
import com.blps.blps.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final XmlUserDetailsService xmlUserDetailsService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderResponseByOrderId(id));
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                                     Authentication authentication) {
        Long customerId = getCustomerIdFromAuth(authentication);
        OrderResponse response = orderService.cancelOrderByCustomer(orderId, customerId);
        return ResponseEntity.ok(response);
    }

    private Long getCustomerIdFromAuth(Authentication authentication) {
        String username = authentication.getName();
        return xmlUserDetailsService.getReferenceId(username);
    }
}