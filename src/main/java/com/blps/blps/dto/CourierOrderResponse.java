package com.blps.blps.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CourierOrderResponse {
    private Long orderId;
    private String status; // статус заказа
    private AddressDto deliveryAddress;
    private String clientName;
    private String clientPhone;
    private Integer estimatedDeliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemDto> items;
}
