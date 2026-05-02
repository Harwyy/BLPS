package com.blps.blps.controller;

import com.blps.blps.dto.request.BatchOrderRequest;
import com.blps.blps.service.restaurantServices.RestaurantBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/batch")
public class RestaurantBatchController  {

    private final RestaurantBatchService restaurantBatchService;

    @PostMapping("/ready")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<String> markMultipleOrdersReady(
            @PathVariable Long restaurantId,
            @RequestBody BatchOrderRequest request) {
        restaurantBatchService.markMultipleOrdersReadyAsync(restaurantId, request.getOrderIds());
        return ResponseEntity.accepted().body("Запрос на пакетную обработку принят. Результаты будут обработаны асинхронно.");
    }

    @PostMapping("/assign-couriers")
    @PreAuthorize("(hasRole('RESTAURANT') and @restaurantSecurity.isRestaurantMatch(#restaurantId, authentication)) or hasRole('ADMIN')")
    public ResponseEntity<String> assignCouriersMultipleOrders(
            @PathVariable Long restaurantId,
            @RequestBody BatchOrderRequest request) {
        restaurantBatchService.assignCouriersForMultipleOrdersAsync(restaurantId, request.getOrderIds());
        return ResponseEntity.accepted().body("Запрос на пакетную обработку принят. Результаты будут обработаны асинхронно.");
    }
}
