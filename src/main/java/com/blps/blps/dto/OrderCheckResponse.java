package com.blps.blps.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderCheckResponse {

    private boolean success;
    private String message;
    private Double totalAmount;
    private Integer estimatedDeliveryTime;
    private List<OrderItemDto> items;
}
