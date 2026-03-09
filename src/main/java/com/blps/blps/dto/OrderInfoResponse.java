package com.blps.blps.dto;

import com.blps.blps.entity.enums.OrderPaymentStatus;
import com.blps.blps.entity.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderInfoResponse {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private Long courierId;
    private OrderStatus status;
    private Double totalAmount;
    private AddressDto deliveryAddress;
    private OrderPaymentStatus paymentStatus;
    private Integer estimatedDeliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemDto> items;
    private boolean success;
    private String message;
}
